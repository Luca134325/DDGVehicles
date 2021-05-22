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
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class VehicleMenu implements InventoryProvider {

    public final SmartInventory inventory;
    public final String type;
    public final VehiclePart part;
    public final VehiclePartType partType;

    public final HashMap<String, HashMap<VehiclePart, List<UnindentifiedVehicleObject>>> unindentifiedVehicleObjectHashMap = Main.getVehicleClassManager().getVehicleDatabase();
    public final List<ClickableItem> clickableItemList = new ArrayList<>();
    public final HashMap<VehiclePart, List<UnindentifiedVehicleObject>> unindentifiedVehicleObjectList;

    public VehicleMenu(String type, VehiclePart part, VehiclePartType partType) {
        this.partType = partType;
        this.part = part;
        this.unindentifiedVehicleObjectList = unindentifiedVehicleObjectHashMap.get(type);
        this.type = type;
        this.inventory = SmartInventory.builder()
                .id("vehicles")
                .provider(this)
                .size(6, 9)
                .title("§3Vehicles §8▶ §7(§b" + this.unindentifiedVehicleObjectList.get(part).size() + "§7)")
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        for (UnindentifiedVehicleObject unindentifiedVehicleObject : unindentifiedVehicleObjectList.get(part)) {
            if (!unindentifiedVehicleObject.getPartType().equals(partType)) continue;
            boolean isVehicleMain = partType == VehiclePartType.VEHICLES;

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
                        "", "§a§nLinkerklik om aanpassingen te maken", "§a§nRechtklik om te krijgen zonder aanpassingen"));
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

            final List<String> defaultLore = Collections.singletonList("§6Vehicle Addon");
            final ItemFactory itemFactory = new ItemFactory();

            ItemStack finalItemStack = itemStack;
            ClickableItem clickableItem = ClickableItem.of(itemStack, event ->
            {
                if (event.getClick() == ClickType.LEFT) {
                    if (!isVehicleMain) {
                        VehicleAddonEditMenu vehicleAddonEditMenu = new VehicleAddonEditMenu(type, part, partType, unindentifiedVehicleObject,
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
                } else if (event.getClick() == ClickType.RIGHT && !isVehicleMain) {
                    if (player.getInventory().firstEmpty() == -1) {
                        player.sendMessage("§cJe hebt geen genoeg ruimte in je inventory!");
                        return;
                    }

                    ItemStack give = finalItemStack.clone();
                    give = itemFactory.edit(give, null, defaultLore);

                    net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(give);
                    NBTTagCompound tagCompound = nms.getTag() == null ? new NBTTagCompound() : nms.getTag();
                    tagCompound.setString("vehicle_addon", "");
                    tagCompound.setString("addon_speed", "0");
                    tagCompound.setString("addon_acceleration", "0");
                    tagCompound.setString("addon_deceleration", "0");
                    tagCompound.setString("addon_fuel", "0");
                    nms.setTag(tagCompound);

                    give = CraftItemStack.asCraftMirror(nms);
                    player.getInventory().addItem(give);
                    player.sendMessage("§3Er is §b1x " + ChatColor.stripColor(give.getItemMeta().getDisplayName()) + " §3toegevoegd aan je inventory.");
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
            VehicleAddonTypeMenu vehiclePartTypeMenu = new VehicleAddonTypeMenu(this.partType, type);
            vehiclePartTypeMenu.inventory.open(player);
        }));

        if (!pagination.isFirst()) {
            contents.set(5,3,ClickableItem.of(new ItemFactory().createItem(Material.ARROW,1,0,"Vorige pagina"),
                    inventoryClickEvent ->
                    {
                        this.clickableItemList.clear();
                        this.inventory.open(player, pagination.previous().getPage());
                    }));
        }
        if (!pagination.isLast()) {
            contents.set(5,5,ClickableItem.of(new ItemFactory().createItem(Material.ARROW,1,0,"Volgende pagina"),
                    inventoryClickEvent ->
                    {
                        this.clickableItemList.clear();
                        this.inventory.open(player, pagination.next().getPage());
                    }));
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {

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

}