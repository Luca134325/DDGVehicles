package dev.maiky.ddgvehicles.inventories.player;

import dev.maiky.ddgvehicles.Main;
import dev.maiky.ddgvehicles.classes.vehicles.VehicleManager;
import dev.maiky.ddgvehicles.utils.ItemFactory;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Door: Maiky
 * Info: DDGVehicles - 04 Apr 2021
 * Package: dev.maiky.ddgvehicles.inventories.player
 */

public class VehicleTrunkGUI implements InventoryProvider {

	private final SmartInventory inventory;
	private final String license;
	private final int rows;

	public int getRows() {
		return rows;
	}

	public String getLicense() {
		return license;
	}

	public SmartInventory getInventory() {
		return inventory;
	}

	public VehicleTrunkGUI(String license, int rows) {
		this.rows = rows;
		this.license = license;
		this.inventory = SmartInventory.builder()
				.id("vehicle_trunk")
				.title(ChatColor.translateAlternateColorCodes('&', "&3Vehicle Trunk &8- &b" + license))
				.size(6, 9)
				.provider(this)
				.build();
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		VehicleManager manager = Main.getInstance().getVehicleManager();
		ItemFactory itemFactory = new ItemFactory();
		ItemStack[] items = manager.getTrunk(this.getLicense());
		ClickableItem[] trunkItems = new ClickableItem[items.length];

		for (int i = 0; i < items.length; i++) {
			final int fI = i;
			trunkItems[i] = ClickableItem.of(items[i], event -> {
				
				if (player.getInventory().firstEmpty() == -1) {
					player.sendMessage("§cJe hebt geen genoeg ruimte in je inventory om dit item uit je kofferbak te halen.");
					return;
				}

				player.getInventory().addItem(items[fI]);
				items[fI] = null;
				manager.updateTrunk(license, items);
				init(player, contents);
			});
		}

		Pagination pagination = contents.pagination();
		pagination.setItemsPerPage(Math.min(this.getRows(), 36));
		pagination.setItems(trunkItems);
		pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL,0,0));

		contents.fillRow(4, ClickableItem.empty(itemFactory.createItem(Material.STAINED_GLASS_PANE,1,7," ")));
		contents.set(5, 4, ClickableItem.of(itemFactory.createItem(Material.BARRIER,1,0,"&c&lSluiten"),
				event -> player.closeInventory()));

		if (!pagination.isLast()) {
			contents.set(5, 6, ClickableItem.of(itemFactory.createItem(Material.ARROW,1,0,
					"Volgende pagina"), event -> getInventory().open(player, pagination.next().getPage())));
		}

		if (!pagination.isFirst()) {
			contents.set(5, 2, ClickableItem.of(itemFactory.createItem(Material.ARROW,1,0,
					"Vorige pagina"), event -> getInventory().open(player, pagination.previous().getPage())));
		}
	}

	@Override
	public void update(Player player, InventoryContents contents) {

	}
}
