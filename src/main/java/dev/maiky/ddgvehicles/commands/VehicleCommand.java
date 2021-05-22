package dev.maiky.ddgvehicles.commands;

import dev.maiky.ddgvehicles.commands.depend.ISub;
import dev.maiky.ddgvehicles.commands.subcommands.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.*;

public class VehicleCommand extends BukkitCommand {

    public HashMap<String, ISub> subCommands = new HashMap<>();

    public VehicleCommand() {
        super("vehicle");
        super.setPermission("ddgvehicles.vehicle");
        super.setAliases(Arrays.asList("voertuig", "ddgvehicle", "vehicles"));

        subCommands.put("help|Bekijk dit bericht met de commands", new HelpSub(this));
        subCommands.put("menu|Open het vehicle menu", new MenuSub(this));
        subCommands.put("edit|Pas onderdelen aan van je voertuig", new EditSub(this));
        subCommands.put("setowner <speler>|Stel de owner in van een vehicle", new SetOwnerSub(this));
        subCommands.put("addrider <speler>|Voeg een bestuurder toe aan een vehicle", new AddRiderSub(this));
        subCommands.put("addmember <speler>|Voeg een member toe aan een vehicle", new AddMemberSub(this));
        subCommands.put("removerider <speler>|Verwijder een bestuurder van een vehicle", new RemoveRiderSub(this));
        subCommands.put("removemember <speler>|Verwijder een member van een vehicle", new RemoveMemberSub(this));
        subCommands.put("info|Bekijk informatie over een voertuig", new InfoSub(this));
        subCommands.put("moersleutel|Krijg een moersleutel om een voertuig aan te passen", new EditorSub(this));
        subCommands.put("dump|Dump data", new DumpSub(this));
        subCommands.put("jerrycan|Krijg een jerrycan in je inventory", new JerrycanSub(this));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length == 0) {
            final Optional<String> optional = subCommands.keySet().stream().filter(s -> s.startsWith("help|")).findFirst();

            if (!optional.isPresent()) {
                Bukkit.getLogger().warning("Er is een fout opgetreden met het comamndo " + commandLabel + " dit komt omdat er geen 'help' sub-commando is opgegeven.");
                sender.sendMessage("[BMT-CommandLib] §cError opgetreden met het uitvoeren van dit commando, lees de console voor de output van de plugin.");
                return true;
            }

            subCommands.get(optional.get()).fire(sender, args, commandLabel);
            return true;
        }

        String firstArgProvided = args[0];
        String keyOfSubCommands = null;
        for (String s : subCommands.keySet()) {
            if (s.startsWith(firstArgProvided)) {
                keyOfSubCommands = s;
                break;
            }
        }

        if (keyOfSubCommands == null) {
            sender.sendMessage("§cOngeldig sub-commando probeer /" + commandLabel + " help");
            return true;
        }

        if (subCommands.get(keyOfSubCommands).permission != null) {
            if (!sender.hasPermission(subCommands.get(keyOfSubCommands).permission)) {
                sender.sendMessage("§cJe hebt hier geen permissions voor!");
                return true;
            }
        }

        String[] argsNeeded = keyOfSubCommands.split("\\|")[0].split(" ");
        if (argsNeeded.length == 1) {
            subCommands.get(keyOfSubCommands).fire(sender, args, commandLabel);
            return true;
        }

        List<String> needed = new ArrayList<>();
        List<String> optional = new ArrayList<>();

        for (int i = 0; i != argsNeeded.length; i++) {
            String in = argsNeeded[i];
            if (in.startsWith("[") && in.endsWith("]")) {
                optional.add(in);
                continue;
            }
            needed.add(in);
        }

        if (needed.isEmpty() && optional.isEmpty()) {
            subCommands.get(keyOfSubCommands).fire(sender, args, commandLabel);
            return true;
        }

        if (args.length == needed.size()) {
            subCommands.get(keyOfSubCommands).fire(sender, args, commandLabel);
            return true;
        }

        if (args.length == (needed.size() + optional.size())) {
            subCommands.get(keyOfSubCommands).fire(sender, args, commandLabel);
            return true;
        }

        sender.sendMessage("§cGebruik: /" + commandLabel + " " + keyOfSubCommands.split("\\|")[0]);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> keys = new ArrayList<>();
        HashMap<String, String> subToKey = new HashMap<>();
        for (String s : subCommands.keySet()) {
            String cmd = s.split("\\|")[0];
            keys.add(cmd.split(" ")[0]);
            if (cmd.split(" ").length > 1) {
                subToKey.put(cmd, cmd.split(" ")[1]);
            }
        }

        if (args.length == 1) {
            if (args[0] != null) {
                List<String> actualKeys = new ArrayList<>();
                for (String key : keys) {
                    if (key.startsWith(args[0])) {
                        actualKeys.add(key);
                    }
                }
                return actualKeys;
            }
            return keys;
        }

        if (args.length == 2) {
            String arg1 = args[0].toLowerCase();

            if (arg1.equals("setowner")) {

                List<String> players = new ArrayList<>();

                String starter = null;
                if (args[1] != null) {
                    starter = args[1];
                }

                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (starter != null && !p.getName().startsWith(starter))
                        continue;
                    players.add(p.getName());
                }

                return players;
            }
        }
        return null;
    }

}
