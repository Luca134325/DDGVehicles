package dev.maiky.ddgvehicles.listeners;

import dev.maiky.ddgvehicles.Main;
import dev.maiky.ddgvehicles.classes.vehicles.SpawnedVehicleManager;
import dev.maiky.ddgvehicles.classes.vehicles.Vehicle;
import dev.maiky.ddgvehicles.classes.vehicles.VehicleManager;
import dev.maiky.ddgvehicles.classes.vehicles.VehicleSeatLink;
import dev.maiky.ddgvehicles.inventories.player.VehiclePlayerGUI;
import dev.maiky.ddgvehicles.utils.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class VehicleEnterListener implements Listener {

    public static HashMap<String, ArmorStand> wiekenMap = new HashMap<>();
    public static HashMap<String, BukkitRunnable> wiekenBukkitMap = new HashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
        Entity rightClicked = event.getRightClicked();
        Player player = event.getPlayer();

        if (player.getVehicle() != null) return;

        if (!(rightClicked instanceof ArmorStand)) return;
        if (rightClicked.getCustomName() == null) return;

        ArmorStand armorStand = (ArmorStand) rightClicked;
        String customName = armorStand.getCustomName();
        String[] data = customName.split("_");

        if (data.length != 2) return;
        event.setCancelled(true);

        if (player.isSneaking()) return;

        String license = data[1];

        if (player.getInventory().getItemInMainHand() != null) {
            ItemStack item = player.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasDisplayName()){
                if (meta.getDisplayName().contains("Moersleutel"))
                    return;
            }
            if (item.getType() == Material.DIAMOND_HOE && (item.getDurability() == 58 || item.getDurability() == 59))
                return;
        }

        VehicleManager manager = Main.getInstance().getVehicleManager();

        UUID owner = manager.getOwner(license);

        if (owner == null) {
            player.sendMessage("§cMeld bij staff dat dit voertuig hier staat, deze hoort hier namelijk §4niet §cte staan.");
            return;
        }
        
        List<UUID> members = manager.getMembers(license);
        List<UUID> riders = manager.getRiders(license);

        List<UUID> collect = new ArrayList<>();
        if (members != null)
            collect.addAll(members);
        if (riders != null)
            collect.addAll(riders);
        collect.add(owner);

        if (!collect.contains(player.getUniqueId()) && !player.hasPermission("ddgvehicles.staff")) {
            player.sendMessage("§cJe moet lid zijn van dit voertuig erin te kunnen.");
            return;
        }

        /*
        if (!Main.getAliveVehicles().containsKey(license)) {
            Vehicle vehicle = new Vehicle(license);
            for (Entity entity : Bukkit.getWorlds().get(0).getEntities()) {
                if (!(entity instanceof ArmorStand)) continue;
                if (entity.getCustomName() == null) continue;
                if (entity.getCustomName().endsWith(license)) {
                    vehicle.getComponents().add((ArmorStand) entity);
                }
            }
            Main.getAliveVehicles().put(license, vehicle);
        }
         */

        VehiclePlayerGUI gui = new VehiclePlayerGUI(license);
        gui.getInventory().open(player);
    }

    public static void helicopterTools(String license, Location location) {
        ItemStack wieken = new ItemFactory().createItem(Material.DIAMOND_PICKAXE,1,
                506, "Wieken", Arrays.asList("Dit is een server owned item!",
                        "Het niet melden van het bezit van dit item is bannable!"), true);
        ArmorStand partArmorStand = location.getWorld().spawn(location, ArmorStand.class);
        partArmorStand.setHelmet(wieken);
        partArmorStand.setCustomName("WIEKEN_" + license);
        partArmorStand.setVisible(false);
        partArmorStand.setCustomNameVisible(false);
        partArmorStand.setMarker(false);
        partArmorStand.setGravity(true);
        partArmorStand.setFireTicks(0);

        wiekenMap.put(license, partArmorStand);
    }

}
