package dev.maiky.ddgvehicles.listeners;

import dev.maiky.ddgvehicles.Main;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityDismountEvent;

/**
 * Door: Maiky
 * Info: DDGVehicles - 06 Apr 2021
 * Package: dev.maiky.ddgvehicles.listeners
 */

public class VehicleDismountListener implements Listener {

	@EventHandler
	public void onDismount(EntityDismountEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		Entity entity = event.getDismounted();
		String customName = entity.getCustomName();

		if (Main.getBossbarMap().containsKey(player)) {
			Main.getBossbarMap().get(player).cancel();
			Main.getBossbarMap().remove(player);
			return;
		}

		if (!(entity instanceof ArmorStand)) return;

		if (customName == null) {
			return;
		}

		if (customName.split("_").length != 2) return;

		String license = customName.split("_")[1];

		if (VehicleEnterListener.wiekenMap.containsKey(license)) {
			VehicleEnterListener.wiekenMap.get(license).remove();
			VehicleEnterListener.wiekenMap.remove(license);
		}

		if (VehicleEnterListener.wiekenBukkitMap.containsKey(license)) {
			VehicleEnterListener.wiekenBukkitMap.get(license).cancel();
			VehicleEnterListener.wiekenBukkitMap.remove(license);
		}
	}

}
