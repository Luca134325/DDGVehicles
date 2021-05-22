package dev.maiky.ddgvehicles.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Door: Maiky
 * Info: DDGVehicles - 05 Apr 2021
 * Package: dev.maiky.ddgvehicles.listeners
 */

public class UserDisconnectListener implements Listener {

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		Player p = event.getPlayer();

		String license = null;
		for (String license2 : VehicleEditStickListener.getInEdit().keySet()) {
			if (VehicleEditStickListener.getInEdit().get(license2).equals(p)) {
				license = license2;
			}
		}

		if (license == null)return;
		VehicleEditStickListener.getInEdit().remove(license);
	}

}
