package dev.maiky.ddgvehicles.inventories.edit;

import dev.maiky.ddgvehicles.Main;
import dev.maiky.ddgvehicles.classes.vehicles.*;
import dev.maiky.ddgvehicles.listeners.VehicleEditStickListener;
import dev.maiky.ddgvehicles.utils.BukkitSerialization;
import dev.maiky.ddgvehicles.utils.ItemFactory;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class VehicleEditGUI {

    public final Inventory inventory;

    private final String license;

    public VehicleEditGUI(String license) {
        this.license = license;
        this.inventory = Bukkit.createInventory(null, 6*9, "§3Vehicle §8▶ §b" + license);
        this.init();
    }

    public void init() {
        VehicleManager manager = Main.getInstance().getVehicleManager();
        String model = manager.getModel(license);
        String modelPath = manager.getModelPath(license);
        List<VehiclePart> vehiclePartList = VehiclePart.getPossibleParts(model);
        ItemFactory itemFactory = new ItemFactory();
        int i = 0;
        for (VehiclePart vehiclePart : vehiclePartList) {
            if (modelPath.toUpperCase().contains(vehiclePart.toString()))
                continue;

            Part part = manager.getPart(license, vehiclePart);

            if (part == null) {
                inventory.setItem(i, itemFactory.createItem(Material.BOOK, 1, 0, "§6" + c(vehiclePart.toString().toLowerCase()),
                        Arrays.asList(" ", "§aInstalleer een onderdeel door erop te klikken","§ain je inventory, deze zal dan vanzelf verschijnen.")));
            } else {
                org.bukkit.inventory.ItemStack item = null;
                try {
                    item = BukkitSerialization.itemStackArrayFromBase64(part.getItemBase64())[0];
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                inventory.setItem(i, item);
            }
            i++;
        }

        inventory.setItem(49, itemFactory.createItem(Material.BARRIER,1,0,"&cSluit menu"));
    }

    public void open(Player p) {
        p.openInventory(inventory);
    }

    public String format(String input) {
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

    public static class VehicleEditGUIListener implements Listener {

        @EventHandler
        public void onClose(InventoryCloseEvent event) {
            if (event.getInventory().getName() == null)return;
            if (!event.getInventory().getName().startsWith("§3Vehicle §8▶ §b"))return;
            String license = event.getInventory().getName().replace("§3Vehicle §8▶ §b", "");
            if (!VehicleEditStickListener.getInEdit().containsKey(license))return;
            VehicleEditStickListener.getInEdit().remove(license);
        }

        @EventHandler
        public void onEvent(InventoryClickEvent event) {

            Player player = (Player) event.getWhoClicked();

            if (!event.getInventory().getName().startsWith("§3Vehicle §8▶ §b")) return;

            event.setCancelled(true);

            int rawSlot = event.getRawSlot();
            int closeButton = 49;
            if (rawSlot == closeButton) {
                event.getWhoClicked().closeInventory();
            }

            String license = event.getInventory().getName().replace("§3Vehicle §8▶ §b", "");
            VehicleManager manager = Main.getInstance().getVehicleManager();

            if (rawSlot <= 53) {
                // Part Inventory
                ItemStack itemStack = CraftItemStack.asNMSCopy(event.getCurrentItem());
                if (itemStack.getTag() == null) return;
                NBTTagCompound tagCompound = itemStack.getTag();
                if (!tagCompound.hasKey("modelPath")) return;
                String modelPath = tagCompound.getString("modelPath");

                // Clicked on a part
                VehiclePart part;
                try {
                    part = VehiclePart.valueOf(modelPath.split("/")[2].split("_")[1].toUpperCase());
                } catch(IllegalArgumentException exception) {
                    event.getWhoClicked().sendMessage("§cEr is een error opgetreden in dit menu, contacteer een Admin. (PART INVENTORY)");
                    Bukkit.getLogger().warning("Error opgetreden bij ophalen van Vehicle Part in " + this.getClass().toString());
                    return;
                }

                if (player.getInventory().firstEmpty() == -1) {
                    player.sendMessage("§cMaak eerst inventory ruimte vrij, voordat je dit onderdeel uit het voertuig haalt.");
                    return;
                }

                Part part1 = manager.getPart(license, part);
                manager.setPart(license, part, null);

                try {
                    player.getInventory().addItem(BukkitSerialization.itemStackArrayFromBase64(part1.getItemBase64()));
                } catch (IOException exception) {
                    player.sendMessage("§cEr is een fout opgetreden bij het toevoegen van het onderdeel in je inventory, contacteer een developer.");
                }

                boolean liveEdit = SpawnedVehicleManager.componentList(license).size() != 0;
                if (liveEdit) updateVehicleLive(license);

                VehicleEditGUI gui = new VehicleEditGUI(license);
                gui.open(player);
            } else {
                // Player Inventory
                if (event.getCurrentItem() == null) return;
                if (event.getCurrentItem().getType() == null) return;
                if (event.getCurrentItem().getType() == Material.AIR) return;

                ItemStack itemStack = CraftItemStack.asNMSCopy(event.getCurrentItem());

                if (itemStack.getTag() == null) {
                    player.sendMessage("§cHet item waar je op klikt is geen vehicle onderdeel.");
                    return;
                }
                NBTTagCompound tagCompound = itemStack.getTag();
                if (!tagCompound.hasKey("vehicle_addon")) {
                    player.sendMessage("§cHet item waar je op klikt is geen vehicle onderdeel.");
                    return;
                }
                String modelPath = tagCompound.getString("modelPath");

                // Clicked on a part
                VehiclePart part;
                try {
                    part = VehiclePart.valueOf(modelPath.split("/")[2].split("_")[1].toUpperCase());
                } catch(IllegalArgumentException exception) {
                    event.getWhoClicked().sendMessage("§cEr is een error opgetreden in dit menu, contacteer een Admin. (PLAYER INVENTORY)");
                    Bukkit.getLogger().warning("Error opgetreden bij ophalen van Vehicle Part in " + this.getClass().toString());
                    return;
                }

                Part p = manager.getPart(license, part);
                if (p != null) {
                    player.sendMessage("§cHaal eerst het onderdeel '" + c(part.toString().toLowerCase()) + "' van dit voertuig af voordat je een ander onderdeel erop probeert" +
                            " te zetten.");
                    return;
                }

                // Check for vehicle compatibility
                String key = modelPath.split("/")[2].split("_")[0];
                String keyCompare = manager.getModelPath(license).split("/")[2].split("_")[0];

                if (!key.equals(keyCompare)) {
                    player.sendMessage("§cDit onderdeel is niet compatibel met dit voertuig.");
                    return;
                }

                double addonSpeed = Double.parseDouble(tagCompound.getString("addon_speed")),
                        addonAcceleration = Double.parseDouble(tagCompound.getString("addon_acceleration")),
                        addonDeceleration = Double.parseDouble(tagCompound.getString("addon_deceleration")),
                        addonFuel = Double.parseDouble(tagCompound.getString("addon_fuel"));

                Part.PartData data = new Part.PartData();
                data.speed = addonSpeed;
                data.acceleration = addonAcceleration;
                data.deceleration = addonDeceleration;
                data.fuel = addonFuel;

                String base64 = BukkitSerialization.itemStackArrayToBase64(new org.bukkit.inventory.ItemStack[]{event.getCurrentItem()});

                manager.setPart(license, part, new Part(modelPath, event.getCurrentItem().getType().toString(),
                        event.getCurrentItem().getDurability(), data, base64));

                boolean liveEdit = SpawnedVehicleManager.componentList(license).size() != 0;
                if (liveEdit) updateVehicleLive(license);

                event.setCurrentItem(null);

                VehicleEditGUI gui = new VehicleEditGUI(license);
                gui.open(player);
            }
        }

    }

    public static void updateVehicleLive(String license) {
        Location mainLocation = Objects.requireNonNull(SpawnedVehicleManager.getCar(license)).getLocation();
        org.bukkit.inventory.ItemStack vehicleItem = SpawnedVehicleManager.despawnVehicle(license);
        Main.getAliveVehicles().remove(license);

        Vehicle vehicle = new Vehicle(license);
        Main.getAliveVehicles().put(license, vehicle);
        vehicle.spawn(mainLocation, vehicleItem);
    }

    public static String c(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

}
