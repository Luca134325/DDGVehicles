package dev.maiky.ddgvehicles.commands.subcommands;

import dev.maiky.ddgvehicles.commands.VehicleCommand;
import dev.maiky.ddgvehicles.commands.depend.ISub;
import dev.maiky.ddgvehicles.utils.ItemFactory;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * Door: Maiky
 * Info: DDGVehicles - 05 Apr 2021
 * Package: dev.maiky.ddgvehicles.commands.subcommands
 */

public class EditorSub extends ISub {

	public EditorSub(VehicleCommand command) {
		super(command);
		super.permission = "ddgvehicles.anwb";
	}

	@Override
	public void fire(CommandSender sender, String[] args, String label) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cJe kan dit commando alleen uitvoeren als speler.");
			return;
		}

		ItemFactory itemFactory = new ItemFactory();
		ItemStack editStick = itemFactory.createItem(Material.STICK, 1, 0, "§eMoersleutel",
				Arrays.asList("", "&7Monteer hiermee onderdelen aan een voertuig","","&4&lServer Owned"));
		((Player)sender).getInventory().addItem(editStick);
		sender.sendMessage("§3Je hebt een §bmoersleutel §3gekregen in je inventory.");
	}
}
