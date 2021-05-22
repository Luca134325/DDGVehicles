package dev.maiky.ddgvehicles.commands.subcommands;

import dev.maiky.ddgvehicles.Main;
import dev.maiky.ddgvehicles.commands.VehicleCommand;
import dev.maiky.ddgvehicles.commands.depend.ISub;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Door: Maiky
 * Info: DDGVehicles - 06 May 2021
 * Package: dev.maiky.ddgvehicles.commands.subcommands
 */

public class DumpSub extends ISub {

	public DumpSub(VehicleCommand command) {
		super(command);
		super.permission = "ddgvehicles.dump";
	}

	@Override
	public void fire(CommandSender sender, String[] args, String label) {
		TextComponent spawned = new TextComponent("§2Contents of Vehicle cache: §f[Hover]");
		spawned.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Main.getAliveVehicles().toString())));
		((Player)sender).spigot().sendMessage(spawned);
	}

}
