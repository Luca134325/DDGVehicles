package dev.maiky.ddgvehicles.inventories.player.events;

import dev.maiky.ddgvehicles.Main;
import dev.maiky.ddgvehicles.classes.vehicles.VehicleTrunkLink;
import dev.maiky.ddgvehicles.inventories.player.VehicleTrunkGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Door: Maiky
 * Info: DDGVehicles - 05 Apr 2021
 * Package: dev.maiky.ddgvehicles.inventories.player.events
 */

public class VehicleTrunkPlayerInventoryListener implements Listener {

	@EventHandler
	public void onPlayerInventoryPress(InventoryClickEvent event) {
		Inventory inventory = event.getInventory();
		int size = inventory.getSize();
		if (inventory.getName() == null)return;
		if (!inventory.getName().contains("Vehicle Trunk"))return;

		String license = inventory.getName().substring(inventory.getName().length()-9);
		int rawSlot = event.getRawSlot();

		if (!(rawSlot > size)) return;

		ItemStack itemStack = event.getCurrentItem();
		if (itemStack == null) return;

		event.setCancelled(true);

		ItemStack[] contents = Main.getInstance().getVehicleManager().getTrunk(license);
		int rows = VehicleTrunkLink.valueOf(Main.getInstance().getVehicleManager().getModel(license)).getRows();
		if (contents.length == rows) {
			event.getWhoClicked().sendMessage("§cJe kofferbak is momenteel vol!");
			return;
		}

		List<ItemStack> itemStackList = new ArrayList<>(Arrays.asList(contents));
		event.setCurrentItem(null);
		itemStackList.add(itemStack);
		ItemStack[] arr = new ItemStack[itemStackList.size()];
		for (int i = 0; i < itemStackList.size(); i++)
			arr[i] = itemStackList.get(i);
		Main.getInstance().getVehicleManager().updateTrunk(license, arr);

		VehicleTrunkGUI gui = new VehicleTrunkGUI(license, rows);
		gui.getInventory().open((Player)event.getWhoClicked());
	}

}
