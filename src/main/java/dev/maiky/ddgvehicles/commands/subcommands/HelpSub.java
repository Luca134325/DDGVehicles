package dev.maiky.ddgvehicles.commands.subcommands;

import dev.maiky.ddgvehicles.Main;
import dev.maiky.ddgvehicles.commands.VehicleCommand;
import dev.maiky.ddgvehicles.commands.depend.ISub;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpSub extends ISub {

    public HelpSub(VehicleCommand command) {
        super(command);
    }

    @Override
    public void fire(CommandSender sender, String[] args, String label) {
        sender.sendMessage(" ");
        sender.sendMessage("§b§lDDGVehicles §7by §f20DJ04 §7& §fMaiky1304");
        sender.sendMessage(" ");
        for (String s : super.command.subCommands.keySet()) {
            ISub sub = super.command.subCommands.get(s);
            if (sub != null) {
                if (sub.permission != null) {
                    if (!sender.hasPermission(sub.permission))continue;
                }
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage(" §3§l➜ §b/" + label + " " + s.split("\\|")[0] + " §8- §7" + s.split("\\|")[1]);
            } else {
                Player player = (Player) sender;
                TextComponent textComponent = new TextComponent(" §3§l➜ "),
                rest = new TextComponent("§b/" + label + " " + s.split("\\|")[0]);
                rest.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]
                        {
                                new TextComponent("§7" + s.split("\\|")[1])
                        }));
                rest.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + label + " " + s.split("\\|")[0]));
                player.spigot().sendMessage(textComponent, rest);
            }
        }
        sender.sendMessage(" ");
        sender.sendMessage("§7Versie: §f" + Main.getInstance().getDescription().getVersion());
        sender.sendMessage(" ");
    }

}
