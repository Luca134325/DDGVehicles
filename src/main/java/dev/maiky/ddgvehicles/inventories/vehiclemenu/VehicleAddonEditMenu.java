package dev.maiky.ddgvehicles.inventories.vehiclemenu;

import dev.maiky.ddgvehicles.Main;
import dev.maiky.ddgvehicles.classes.vehicles.Part;
import dev.maiky.ddgvehicles.classes.vehicles.UnindentifiedVehicleObject;
import dev.maiky.ddgvehicles.classes.vehicles.VehiclePart;
import dev.maiky.ddgvehicles.classes.vehicles.VehiclePartType;
import dev.maiky.ddgvehicles.utils.BukkitSerialization;
import dev.maiky.ddgvehicles.utils.ItemFactory;
import dev.maiky.ddgvehicles.utils.Skull;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class VehicleAddonEditMenu implements InventoryProvider {

    public final SmartInventory inventory;
    public final String type;
    public final VehiclePart part;
    public final VehiclePartType partType;
    public final UnindentifiedVehicleObject unindentifiedVehicleObject;

    public Part partProperties;

    public ItemStack itemStack;

    public final HashMap<String, HashMap<VehiclePart, List<UnindentifiedVehicleObject>>> unindentifiedVehicleObjectHashMap = Main.getVehicleClassManager().getVehicleDatabase();;
    public final HashMap<VehiclePart, List<UnindentifiedVehicleObject>> unindentifiedVehicleObjectList;

    public VehicleAddonEditMenu(String type, VehiclePart part, VehiclePartType partType, UnindentifiedVehicleObject unindentifiedVehicleObject,
                                ItemStack itemStack) {
        this.itemStack = itemStack;
        this.partType = partType;
        this.part = part;
        this.unindentifiedVehicleObjectList = unindentifiedVehicleObjectHashMap.get(type);
        this.unindentifiedVehicleObject = unindentifiedVehicleObject;
        this.type = type;


        this.partProperties = new Part(
                this.unindentifiedVehicleObject.getModel(),
                this.itemStack.getType().toString(),
                this.itemStack.getDurability(),
                new Part.PartData(),
                BukkitSerialization.itemStackArrayToBase64(new ItemStack[]{this.itemStack})
        );

        this.inventory = SmartInventory.builder()
                .id("vehicleAddonEditor")
                .provider(this)
                .size(4, 9)
                .title("§3Vehicles §8▶ §7Addon Properties")
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        final List<String> defaultLore = Collections.singletonList("§6Vehicle Addon");
        final ItemFactory itemFactory = new ItemFactory();

        // Left Side
        contents.set(1, 1, ClickableItem.empty(this.itemStack.clone()));
        contents.set(2, 1, ClickableItem.of(itemFactory.edit(Skull.getCustomSkull("f84f597131bbe25dc058af888cb29831f79599bc67c95c802925ce4afba332fc")
        , "Terug naar part menu"), event -> new VehicleMenu(this.type, this.part, this.partType).inventory.open(player)));

        // Right Side
        contents.set(1, 3, ClickableItem.of(itemFactory.createItem(Material.PAPER, 1, 0, "Extra % snelheid",
                Arrays.asList("", "Huidige waarde: " + this.partProperties.getPartData().speed, "", "Klik om te wijzigen")),
                event ->
                {
                    boolean[] done = {false};
                    player.sendMessage("§2Vul nu een waarde in voor §a" + event.getCurrentItem().getItemMeta().getDisplayName() + " §2of typ §aannuleer §2" +
                            "om dit te annuleren.");
                    player.closeInventory();

                    Bukkit.getPluginManager().registerEvents(new Listener() {
                        @EventHandler
                        public void onChat(AsyncPlayerChatEvent e) {
                            if (done[0]) return;

                            Player player1 = e.getPlayer();
                            if (player1 != player) return;

                            e.setCancelled(true);

                            String input = e.getMessage();

                            if (input.equalsIgnoreCase("annuleer")) {
                                player.sendMessage("§cHuidige actie geannuleerd.");
                                done[0] = true;
                                inventory.open(player);
                                return;
                            }

                            double d;
                            try {
                                d = Double.parseDouble(input);
                            } catch (NumberFormatException exception) {
                                inventory.open(player);
                                done[0] = true;
                                player.sendMessage("§cDit is geen geldig getal!");
                                return;
                            }

                            

                            partProperties.getPartData().speed = d;
                            done[0] = true;
                            inventory.open(player);
                        }
                    }, Main.getInstance());
                }));
        contents.set(1, 4, ClickableItem.of(itemFactory.createItem(Material.PAPER, 1, 0, "Extra % acceleratie",
                Arrays.asList("", "Huidige waarde: " + this.partProperties.getPartData().acceleration, "", "Klik om te wijzigen")),
                event ->
                {
                    boolean[] done = {false};
                    player.sendMessage("§2Vul nu een waarde in voor §a" + event.getCurrentItem().getItemMeta().getDisplayName() + " §2of typ §aannuleer §2" +
                            "om dit te annuleren.");
                    player.closeInventory();

                    Bukkit.getPluginManager().registerEvents(new Listener() {
                        @EventHandler
                        public void onChat(AsyncPlayerChatEvent e) {
                            if (done[0]) return;

                            Player player1 = e.getPlayer();
                            if (player1 != player) return;

                            e.setCancelled(true);

                            String input = e.getMessage();

                            if (input.equalsIgnoreCase("annuleer")) {
                                player.sendMessage("§cHuidige actie geannuleerd.");
                                done[0] = true;
                                inventory.open(player);
                                return;
                            }

                            double d;
                            try {
                                d = Double.parseDouble(input);
                            } catch (NumberFormatException exception) {
                                inventory.open(player);
                                done[0] = true;
                                player.sendMessage("§cDit is geen geldig getal!");
                                return;
                            }

                            

                            partProperties.getPartData().acceleration = d;
                            done[0] = true;
                            inventory.open(player);
                        }
                    }, Main.getInstance());
                }));
        contents.set(1, 5, ClickableItem.of(itemFactory.createItem(Material.PAPER, 1, 0, "Extra % deceleratie",
                Arrays.asList("", "Huidige waarde: " + this.partProperties.getPartData().deceleration, "", "Klik om te wijzigen")),
                event ->
                {
                    boolean[] done = {false};
                    player.sendMessage("§2Vul nu een waarde in voor §a" + event.getCurrentItem().getItemMeta().getDisplayName() + " §2of typ §aannuleer §2" +
                            "om dit te annuleren.");
                    player.closeInventory();

                    Bukkit.getPluginManager().registerEvents(new Listener() {
                        @EventHandler
                        public void onChat(AsyncPlayerChatEvent e) {
                            if (done[0]) return;

                            Player player1 = e.getPlayer();
                            if (player1 != player) return;

                            e.setCancelled(true);

                            String input = e.getMessage();

                            if (input.equalsIgnoreCase("annuleer")) {
                                player.sendMessage("§cHuidige actie geannuleerd.");
                                done[0] = true;
                                inventory.open(player);
                                return;
                            }

                            double d;
                            try {
                                d = Double.parseDouble(input);
                            } catch (NumberFormatException exception) {
                                inventory.open(player);
                                done[0] = true;
                                player.sendMessage("§cDit is geen geldig getal!");
                                return;
                            }

                            

                            partProperties.getPartData().deceleration = d;
                            done[0] = true;
                            inventory.open(player);
                        }
                    }, Main.getInstance());
                }));
        contents.set(1, 6, ClickableItem.of(itemFactory.createItem(Material.PAPER, 1, 0, "Extra % fuel",
                Arrays.asList("", "Huidige waarde: " + this.partProperties.getPartData().fuel, "", "Klik om te wijzigen")),
                event ->
                {
                    boolean[] done = {false};
                    player.sendMessage("§2Vul nu een waarde in voor §a" + event.getCurrentItem().getItemMeta().getDisplayName() + " §2of typ §aannuleer §2" +
                            "om dit te annuleren.");
                    player.closeInventory();

                    Bukkit.getPluginManager().registerEvents(new Listener() {
                        @EventHandler
                        public void onChat(AsyncPlayerChatEvent e) {
                            if (done[0]) return;

                            Player player1 = e.getPlayer();
                            if (player1 != player) return;

                            e.setCancelled(true);

                            String input = e.getMessage();

                            if (input.equalsIgnoreCase("annuleer")) {
                                player.sendMessage("§cHuidige actie geannuleerd.");
                                done[0] = true;
                                inventory.open(player);
                                return;
                            }

                            double d;
                            try {
                                d = Double.parseDouble(input);
                            } catch (NumberFormatException exception) {
                                inventory.open(player);
                                done[0] = true;
                                player.sendMessage("§cDit is geen geldig getal!");
                                return;
                            }

                            partProperties.getPartData().fuel = d;
                            done[0] = true;
                            inventory.open(player);
                        }
                    }, Main.getInstance());
                }));

        // Finish
        contents.set(1, 7, ClickableItem.of(itemFactory.createItem(Material.EMERALD_BLOCK,1,0, "&a&lVoltooien",
                Arrays.asList("", "§7speed: §f" + this.partProperties.getPartData().speed,
                        "§7acceleration: §f" + this.partProperties.getPartData().acceleration,
                        "§7deceleration: §f" + this.partProperties.getPartData().deceleration,
                        "§7fuel: §f" + this.partProperties.getPartData().fuel)),
                event ->
                {
                    if (player.getInventory().firstEmpty() == -1) {
                        player.sendMessage("§cJe hebt geen genoeg ruimte in je inventory!");
                        return;
                    }

                    ItemStack give = this.itemStack.clone();
                    give = itemFactory.edit(give, null, defaultLore);

                    net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(give);
                    NBTTagCompound tagCompound = nms.getTag() == null ? new NBTTagCompound() : nms.getTag();
                    tagCompound.setString("vehicle_addon", "");
                    tagCompound.setString("addon_speed", String.valueOf(this.partProperties.getPartData().speed));
                    tagCompound.setString("addon_acceleration", String.valueOf(this.partProperties.getPartData().acceleration));
                    tagCompound.setString("addon_deceleration", String.valueOf(this.partProperties.getPartData().deceleration));
                    tagCompound.setString("addon_fuel", String.valueOf(this.partProperties.getPartData().fuel));
                    nms.setTag(tagCompound);

                    give = CraftItemStack.asCraftMirror(nms);
                    player.getInventory().addItem(give);
                    player.sendMessage("§3Er is §b1x " + ChatColor.stripColor(give.getItemMeta().getDisplayName()) + " §3toegevoegd aan je inventory.");
                }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
