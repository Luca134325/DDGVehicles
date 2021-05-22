package dev.maiky.ddgvehicles.inventories.vehiclemenu;

import dev.maiky.ddgvehicles.Main;
import dev.maiky.ddgvehicles.classes.vehicles.UnindentifiedVehicleObject;
import dev.maiky.ddgvehicles.classes.vehicles.VehicleManager;
import dev.maiky.ddgvehicles.classes.vehicles.VehiclePart;
import dev.maiky.ddgvehicles.classes.vehicles.VehiclePartType;
import dev.maiky.ddgvehicles.utils.ItemFactory;
import dev.maiky.ddgvehicles.utils.Skull;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import lombok.Getter;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Door: Maiky
 * Info: DDGVehicles - 10 May 2021
 * Package: dev.maiky.ddgvehicles.inventories.vehiclemenu
 */

public class VehicleSearchResultMenu implements InventoryProvider {

	@Getter
	private final SmartInventory inventory;

	@Getter
	private final HashMap<VehiclePart, UnindentifiedVehicleObject> list;

	public VehicleSearchResultMenu(String query, HashMap<VehiclePart, UnindentifiedVehicleObject> list) {
		this.list = list;
		this.inventory = SmartInventory.builder()
				.id("vehicle_search")
				.title("§3Zoekresultaten voor §b" + query)
				.provider(this)
				.size(6, 9)
				.build();
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		List<ClickableItem> clickableItemList = new ArrayList<>();
		for (VehiclePart partNameClass : getList().keySet()) {
			UnindentifiedVehicleObject unindentifiedVehicleObject = getList().get(partNameClass);
			boolean isVehicleMain = unindentifiedVehicleObject.getPartType() == VehiclePartType.VEHICLES;

			System.out.println(unindentifiedVehicleObject);

			Material material = unindentifiedVehicleObject.getMaterial();
			double damage = unindentifiedVehicleObject.getDamage();
			String formattedName;
			try {
				formattedName = format(unindentifiedVehicleObject.getModel().split("/")[2]);
			} catch(ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
				continue;
			}

			boolean customItem = false;
			if (unindentifiedVehicleObject.getMtCustom() != null) {
				damage = (short) 0;
				customItem = true;
			}

			ItemStack itemStack = new ItemStack(material, 1, (short)damage);
			ItemMeta meta = itemStack.getItemMeta();
			meta.setDisplayName("§6" + formattedName);
			if (!isVehicleMain) {
				meta.setLore(Arrays.asList("", "§aModel: §7" + unindentifiedVehicleObject.getModel(),
						"§aRType: §7" + unindentifiedVehicleObject.getMaterial().toString(),
						"§aPart #: §7" + (int) unindentifiedVehicleObject.getDamage(),
						"", "§a§nKlik om aanpassingen te maken"));
			} else {
				meta.setLore(Arrays.asList("", "§aModel: §7" + unindentifiedVehicleObject.getModel(),
						"§aRType: §7" + unindentifiedVehicleObject.getMaterial().toString(),
						"§aPart #: §7" + (int) unindentifiedVehicleObject.getDamage(),
						"", "§a§nKlik om deze vehicle te maken"));
			}
			meta.setUnbreakable(true);
			itemStack.setItemMeta(meta);

			if (!isVehicleMain) {
				net.minecraft.server.v1_12_R1.ItemStack itemStack1 = CraftItemStack.asNMSCopy(itemStack);
				NBTTagCompound tagCompound = itemStack1.getTag() == null ? new NBTTagCompound() : itemStack1.getTag();
				tagCompound.setString("modelPath", unindentifiedVehicleObject.getModel());
				itemStack1.setTag(tagCompound);
				itemStack = CraftItemStack.asCraftMirror(itemStack1);
			}

			if (customItem) {
				net.minecraft.server.v1_12_R1.ItemStack itemStack1 = CraftItemStack.asNMSCopy(itemStack);
				NBTTagCompound tagCompound = itemStack1.getTag() == null ? new NBTTagCompound() : itemStack1.getTag();
				tagCompound.setString("mtcustom", unindentifiedVehicleObject.getMtCustom());
				itemStack1.setTag(tagCompound);
				itemStack = CraftItemStack.asCraftMirror(itemStack1);
			}

			ItemStack finalItemStack = itemStack;
			ClickableItem clickableItem = ClickableItem.of(itemStack, event ->
			{
				if (!isVehicleMain) {
					VehicleAddonEditMenu vehicleAddonEditMenu = new VehicleAddonEditMenu(unindentifiedVehicleObject.getModel(), partNameClass,
							unindentifiedVehicleObject.getPartType(), unindentifiedVehicleObject,
							finalItemStack);
					vehicleAddonEditMenu.inventory.open(player);
				}else {
					if (player.getInventory().firstEmpty() == -1) {
						player.sendMessage("§cJe moet meer §4inventory ruimte§c vrij maken om deze vehicle te maken.");
						return;
					}

					VehicleManager.VehicleCreationResponse creationResponse = Main.getInstance().getVehicleManager().createVehicle(
							unindentifiedVehicleObject.getModel(),
							"##-###-##",
							unindentifiedVehicleObject
					);
					player.getInventory().addItem(creationResponse.getItemStack());
					player.sendMessage("§aJe hebt een voertuig aangemaakt met de naam §2" +
							creationResponse.getName() + " §aen het kenteken §2" + creationResponse.getLicense()
							+ "§a, dit voertuig is opgeslagen onder het id §2" + creationResponse.getId() + "§a in de database.");
				}
			});
			clickableItemList.add(clickableItem);
		}

		Pagination pagination = contents.pagination();
		pagination.setItemsPerPage(36);
		ClickableItem[] clickableItems = new ClickableItem[clickableItemList.size()];
		for (int i = 0; i < clickableItemList.size(); i++) {
			clickableItems[i] = clickableItemList.get(i);
		}
		pagination.setItems(clickableItems);
		pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL,0,0));

		contents.fillRow(4, ClickableItem.empty(new ItemFactory().createItem(Material.STAINED_GLASS_PANE,1,7," ")));
		contents.set(5, 4, ClickableItem.of(new ItemFactory().edit(
				Skull.getCustomSkull("a3852bf616f31ed67c37de4b0baa2c5f8d8fca82e72dbcafcba66956a81c4"),
				"&cTerug naar vorige menu"
		), event ->
		{
			VehicleTypeMenu vehicleTypeMenu = new VehicleTypeMenu();
			vehicleTypeMenu.inventory.open(player);
		}));

		if (!pagination.isFirst()) {
			contents.set(5,3,ClickableItem.of(new ItemFactory().createItem(Material.ARROW,1,0,"Vorige pagina"),
					inventoryClickEvent ->
					{
						clickableItemList.clear();
						inventory.open(player, pagination.previous().getPage());
					}));
		}
		if (!pagination.isLast()) {
			contents.set(5,5,ClickableItem.of(new ItemFactory().createItem(Material.ARROW,1,0,"Volgende pagina"),
					inventoryClickEvent ->
					{
						clickableItemList.clear();
						inventory.open(player, pagination.next().getPage());
					}));
		}
	}

	public static String format(String input) {
		String[] words = input.split("_");
		StringBuilder builder = new StringBuilder();
		for (String word : words) {
			char[] chars = word.toCharArray();
			StringBuilder builder1 = new StringBuilder();
			builder1.append(String.valueOf(chars[0]).toUpperCase());
			for (int i = 1; i != chars.length; i++) {
				builder1.append(String.valueOf(chars[i]).toLowerCase());
			}
			builder.append(builder1.toString()).append(" ");
		}
		return builder.substring(0, builder.toString().length() - 1);
	}

	@Override
	public void update(Player player, InventoryContents contents) {

	}
}
