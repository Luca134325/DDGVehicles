package dev.maiky.ddgvehicles.listeners;

import dev.maiky.ddgvehicles.Main;
import dev.maiky.ddgvehicles.classes.vehicles.Vehicle;
import dev.maiky.ddgvehicles.classes.vehicles.VehicleManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Door: Maiky
 * Info: DDGVehicles - 08 May 2021
 * Package: dev.maiky.ddgvehicles.listeners
 */

public class VehicleFuelListener implements Listener {

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

		VehicleManager manager = Main.getInstance().getVehicleManager();
		String license = data[1];
		PlayerInventory inventory = player.getInventory();
		ItemStack mainHand = inventory.getItemInMainHand();

		if (mainHand == null) return;
		if (mainHand.getType() != Material.DIAMOND_HOE) return;
		if (mainHand.getDurability() != 58 && mainHand.getDurability() != 59) return;
		if (!mainHand.hasItemMeta()) return;
		if (!mainHand.getItemMeta().isUnbreakable()) return;

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

		UUID owner = manager.getOwner(license);
		List<UUID> members = manager.getMembers(license);
		List<UUID> riders = manager.getRiders(license);

		List<UUID> collect = new ArrayList<>();
		if (members != null)
			collect.addAll(members);
		if (riders != null)
			collect.addAll(riders);
		collect.add(owner);

		if (!collect.contains(player.getUniqueId()) && !player.hasPermission("ddgvehicles.staff")) {
			player.sendMessage("§cJe moet lid zijn van dit voertuig om de tank bij te vullen.");
			return;
		}

		Vehicle vehicle = Main.getAliveVehicles().get(license);

		int percentageJerrycan = Integer.parseInt(player.getItemInHand().getItemMeta().getLore().get(0).replace("%", ""));
		double rawFuel = vehicle.getFuel();
		int percentageVanAuto = (int) Math.round(rawFuel);

		if (percentageVanAuto == 100) return;

		int percentageAfVanJerrycan = Integer.parseInt(String.valueOf(percentageVanAuto - 100).replace("-", ""));

		int possibleOutcome = percentageJerrycan - percentageAfVanJerrycan;
		if (possibleOutcome < 0) {
			percentageAfVanJerrycan += possibleOutcome;
		}


		ItemMeta meta = player.getItemInHand().getItemMeta();
		meta.setLore(Collections.singletonList((percentageJerrycan - percentageAfVanJerrycan) + "%"));
		player.getItemInHand().setItemMeta(meta);

		if ((percentageJerrycan - percentageAfVanJerrycan) == 0) {
			player.getItemInHand().setDurability((short) 59);
		}

		// Add to car
		double outcome = rawFuel + percentageAfVanJerrycan;
		if (outcome > 100) {
			outcome = 100;
		}

		vehicle.setFuel(outcome);
		Main.getAliveVehicles().put(license, vehicle);

		player.sendMessage("§3Je hebt de auto van §b" +
				Bukkit.getOfflinePlayer(manager.getOwner(license)).getName() + "§3 gevuld met §b" + percentageAfVanJerrycan + "%§3.");
	}

	public static boolean isFullJerrycan(ItemStack stack) {
		return stack.getDurability() == (short) 58;
	}

	public static ItemStack getJerrycan(boolean full) {
		ItemStack jerrycan = new ItemStack(Material.DIAMOND_HOE, 1, full ? (short) 58 : (short) 59);
		ItemMeta jerrycanMeta = jerrycan.getItemMeta();
		jerrycanMeta.setDisplayName("Jerrycan");
		jerrycanMeta.setUnbreakable(true);
		jerrycanMeta.setLore(Collections.singletonList(full ? "100%" : "0%"));
		jerrycan.setItemMeta(jerrycanMeta);

		return jerrycan;
	}

}
