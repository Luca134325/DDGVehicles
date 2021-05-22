package dev.maiky.ddgvehicles.listeners;

import dev.maiky.ddgvehicles.Main;
import dev.maiky.ddgvehicles.classes.vehicles.SpawnedVehicleManager;
import dev.maiky.ddgvehicles.classes.vehicles.Vehicle;
import dev.maiky.ddgvehicles.classes.vehicles.VehicleManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class VehiclePickupListener implements Listener {

    @EventHandler
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
        Entity rightClicked = event.getRightClicked();
        Player player = event.getPlayer();

        if (!(rightClicked instanceof ArmorStand)) return;
        if (rightClicked.getCustomName() == null) return;

        ArmorStand armorStand = (ArmorStand) rightClicked;
        String customName = armorStand.getCustomName();
        String[] data = customName.split("_");

        if (data.length != 2) return;
        event.setCancelled(true);

        if (!player.isSneaking()) return;

        String license = data[1];
        VehicleManager manager = Main.getInstance().getVehicleManager();
        UUID owner = manager.getOwner(license);

        if (owner == null) {
            player.sendMessage("§cMeld bij staff dat dit voertuig hier staat, deze hoort hier namelijk §4niet §cte staan.");
            return;
        }

        if (!owner.equals(player.getUniqueId()) && !player.hasPermission("ddgvehicles.staff")) {
            player.sendMessage("§cJij bent niet de eigenaar van dit voertuig, vraag §4"
                    + Bukkit.getOfflinePlayer(owner).getName() + " §com toestemming om dit voertuig te kunnen oppakken.");
            return;
        }

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage("§cJe hebt geen genoeg ruimte in je inventory om dit voertuig op te pakken, maak inventory ruimte vrij en probeer het opnieuw.");
            return;
        }

         if (!Main.getAliveVehicles().containsKey(license)) {
            Vehicle vehicle = new Vehicle(license);
            /*
             for (Entity entity : Bukkit.getWorlds().get(0).getEntities()) {
                if (!(entity instanceof ArmorStand)) continue;
                if (entity.getCustomName() == null) continue;
                if (entity.getCustomName().endsWith(license)) {
                    vehicle.getComponents().add((ArmorStand) entity);
                }
            }
             */
            Main.getAliveVehicles().put(license, vehicle);
        }

        ItemStack itemStack = SpawnedVehicleManager.despawnVehicle(license);

        if (itemStack == null) {
            Bukkit.getLogger().warning("Fout opgetreden bij het oppakken van het voertuig met de license " + license + " [Oppakker: " + player.getName() + "; Locatie: "
                    + player.getLocation().getX() + " " + player.getLocation().getY() + " " + player.getLocation().getZ() + " -> " +
                    player.getLocation().getWorld().getName());
            Main.notifyStaff("Fout opgetreden bij het oppakken van het voertuig met de license " + license + " [Oppakker: " + player.getName() + "; Locatie: "
                    + player.getLocation().getX() + " " + player.getLocation().getY() + " " + player.getLocation().getZ() + " -> " +
                    player.getLocation().getWorld().getName());
            player.sendMessage("§cEr is een fout opgetreden met het oppakken van je voertuig, het item kon niet worden opgehaald, staff die online is is ingelicht. Maak meteen een ticket met een screenshot van dit bericht.");
            return;
        }

        Main.getAliveVehicles().remove(license);

        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            player.getInventory().setItemInMainHand(itemStack);
        } else {
            player.getInventory().addItem(itemStack);
        }

        player.sendMessage("§3Je hebt je voertuig met het kenteken §b" + license + " §3succesvol opgepakt.");
    }

}
