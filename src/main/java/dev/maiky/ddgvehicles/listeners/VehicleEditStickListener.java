package dev.maiky.ddgvehicles.listeners;

import dev.maiky.ddgvehicles.Main;
import dev.maiky.ddgvehicles.classes.vehicles.Vehicle;
import dev.maiky.ddgvehicles.inventories.edit.VehicleEditGUI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

/**
 * Door: Maiky
 * Info: DDGVehicles - 05 Apr 2021
 * Package: dev.maiky.ddgvehicles.listeners
 */

public class VehicleEditStickListener implements Listener {

	@Getter
	private static HashMap<String, Player> inEdit = new HashMap<>();

	@EventHandler(priority = EventPriority.LOW)
	public void onPress(PlayerInteractAtEntityEvent event) {
		Player p = event.getPlayer();
		Entity rightClicked = event.getRightClicked();

		if (!(rightClicked instanceof ArmorStand))return;
		ArmorStand armorStand = (ArmorStand) rightClicked;
		if (rightClicked.getCustomName() == null) return;

		String customName = armorStand.getCustomName();
		String[] data = customName.split("_");

		if (data.length != 2) return;

		String license = data[1];

		if (p.getInventory().getItemInMainHand() == null)return;
		ItemStack item = p.getInventory().getItemInMainHand();
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return;
		if (!meta.hasDisplayName()) return;
		if (!meta.getDisplayName().contains("Moersleutel"))return;
		if (meta.getLore().size() != 4)return;
		if (!meta.getLore().get(1).contains("Monteer hiermee onderdelen aan een voertuig"))return;

		if (!p.hasPermission("ddgvehicles.monteur")) {
			p.sendMessage("§cError! Jij bent §4geen §cmonteur dus je kunt dit item niet gebruiken!");
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

		if (inEdit.containsKey(license)) {
			p.sendMessage("§cDe medewerker §4" + inEdit.get(license).getName() + " §cis al bezig aan dit voertuig.");
			return;
		}

		inEdit.put(license, p);

		VehicleEditGUI gui = new VehicleEditGUI(license);
		gui.open(p);
	}

}
