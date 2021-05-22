package dev.maiky.ddgvehicles.commands.subcommands;

import dev.maiky.ddgvehicles.Main;
import dev.maiky.ddgvehicles.classes.vehicles.VehicleManager;
import dev.maiky.ddgvehicles.commands.VehicleCommand;
import dev.maiky.ddgvehicles.commands.depend.ISub;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class SetOwnerSub extends ISub {

    public SetOwnerSub(VehicleCommand command) {
        super(command);
        super.permission = "vehicle.admin.setowner";
    }

    @Override
    public void fire(CommandSender sender, String[] args, String label) {
        Player player = (Player) sender;

        if (args.length == 2) {
            ItemStack mainItem = player.getInventory().getItemInMainHand();
            ItemMeta meta = mainItem.getItemMeta();

            if (mainItem.getType() != Material.DIAMOND_PICKAXE && mainItem.getType() != Material.DIAMOND_SPADE
                    && mainItem.getType() != Material.DIAMOND_SWORD) {
                player.sendMessage("§cJe hebt geen vehicle in je hand!");
                return;
            }

            String displayName = meta.getDisplayName();
            if (!displayName.startsWith("§6")) {
                player.sendMessage("§cJe hebt geen vehicle in je hand!");
                return;
            }

            List<String> lore = meta.getLore();
            if (lore.size() != 2) {
                player.sendMessage("§cJe hebt geen vehicle in je hand!");
                return;
            }

            if (!lore.get(1).startsWith("§8➥")) {
                player.sendMessage("§cJe hebt geen vehicle in je hand!");
                return;
            }

            String license = ChatColor.stripColor(lore.get(1).replace("§8➥ §eKenteken: §7", ""));
            String target = args[1];
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(target);

            if (offlinePlayer == null) {
                sender.sendMessage("§cDeze speler bestaat niet!");
                return;
            }

            UUID uuid = offlinePlayer.getUniqueId();
            VehicleManager manager = Main.getInstance().getVehicleManager();

            if (manager.getOwner(license) != null && manager.getOwner(license).equals(uuid)) {
                sender.sendMessage("§cDe eigenaar van dit voertuig is al §4" + offlinePlayer.getName() + "§c.");
                return;
            }

            manager.setOwner(license, uuid);

            sender.sendMessage("§2Je hebt de owner van het voertuig met het kenteken §a" + license + " §2veranderd naar §a"
                    + offlinePlayer.getName() + "§2.");
        } else if (args.length == 3) {
            if (!player.hasPermission("ddgvehicles.admin.setowner")) {
                player.sendMessage("§cJe hebt geen permissions om dit commando uit te voeren.");
                return;
            }

            String target = args[1];
            String license = args[2];
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(target);

            if (offlinePlayer == null) {
                sender.sendMessage("§cDeze speler bestaat niet!");
                return;
            }

            UUID uuid = offlinePlayer.getUniqueId();
            VehicleManager manager = Main.getInstance().getVehicleManager();

            if (!manager.licenseExists(license)) {
                sender.sendMessage("§cEr bestaat geen voertuig met het kenteken §4" + license + "§c.");
                return;
            }

            if (manager.getOwner(license) != null && manager.getOwner(license).equals(uuid)) {
                sender.sendMessage("§cDe eigenaar van dit voertuig is al §4" + offlinePlayer.getName() + "§c.");
                return;
            }

            manager.setOwner(license, uuid);

            sender.sendMessage("§2Je hebt de owner van het voertuig met het kenteken §a" + license + " §2veranderd naar §a"
                    + offlinePlayer.getName() + "§2.");
        }
    }
}
