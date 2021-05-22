package dev.maiky.ddgvehicles.commands.subcommands;

import dev.maiky.ddgvehicles.commands.VehicleCommand;
import dev.maiky.ddgvehicles.commands.depend.ISub;
import dev.maiky.ddgvehicles.listeners.VehicleFuelListener;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Door: Maiky
 * Info: DDGVehicles - 08 May 2021
 * Package: dev.maiky.ddgvehicles.commands.subcommands
 */

public class JerrycanSub extends ISub {

	public JerrycanSub(VehicleCommand command) {
		super(command);
		super.permission = "ddgvehicles.jerrycan";
	}

	@Override
	public void fire(CommandSender sender, String[] args, String label) {
		Player player = (Player) sender;
		ItemStack itemStack = VehicleFuelListener.getJerrycan(true);

		if (player.getInventory().firstEmpty() == -1) {
			player.sendMessage("§cJe hebt geen inventory ruimte!");
			return;
		}

		player.getInventory().addItem(itemStack);
	}

}
