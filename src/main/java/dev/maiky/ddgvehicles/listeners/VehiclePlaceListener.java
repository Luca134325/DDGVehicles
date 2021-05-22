package dev.maiky.ddgvehicles.listeners;

import dev.maiky.ddgvehicles.Main;
import dev.maiky.ddgvehicles.classes.vehicles.Vehicle;
import dev.maiky.ddgvehicles.classes.vehicles.VehicleManager;
import dev.maiky.ddgvehicles.events.VehiclePlaceEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class VehiclePlaceListener implements Listener {

    private static final double offsetY = 1;

    @EventHandler
    public void onEvent(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Action action = event.getAction();

        if (event.getHand() == EquipmentSlot.HAND) return;
        if (action != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack mainItem = player.getInventory().getItemInMainHand();
        ItemMeta meta = mainItem.getItemMeta();

        if (mainItem.getType() != Material.DIAMOND_PICKAXE && mainItem.getType() != Material.DIAMOND_SPADE
                && mainItem.getType() != Material.DIAMOND_SWORD) return;
        String displayName = meta.getDisplayName();
        if (!displayName.startsWith("§6")) return;
        List<String> lore = meta.getLore();
        if (lore.size() != 2) return;
        if (!lore.get(1).startsWith("§8➥")) return;

        event.setCancelled(true);

        String license = ChatColor.stripColor(lore.get(1).replace("§8➥ §eKenteken: §7", ""));
        VehicleManager manager = Main.getInstance().getVehicleManager();

        VehiclePlaceEvent placeEvent = new VehiclePlaceEvent(license);
        Bukkit.getPluginManager().callEvent(placeEvent);

        if (placeEvent.isCancelled()) {
            return;
        }

        UUID owner = manager.getOwner(license);

        if (owner == null) {
            event.getPlayer().sendMessage("§cZet eerst een eigenaar op dit voertuig d.m.v. §4/vehicle setowner <speler>");
            return;
        }

        if (!owner.equals(event.getPlayer().getUniqueId()) && !player.hasPermission("ddgvehicles.place.override")) {
            event.getPlayer().sendMessage("§cJij bent niet de eigenaar van dit voertuig, vraag §4"
                    + Bukkit.getOfflinePlayer(owner).getName() + " §com toestemming om dit voertuig te kunnen plaatsen.");
            return;
        }

        Location location = event.getClickedBlock().getLocation().add(0, offsetY, 0);
        Vehicle vehicle = new Vehicle(license);
        Main.getAliveVehicles().put(license, vehicle);
        vehicle.spawn(location, mainItem);
        player.getInventory().setItemInMainHand(null);
        player.sendMessage("§3Je hebt het voertuig met het kenteken §b" + license + " §3succesvol geplaatst.");
    }

}
