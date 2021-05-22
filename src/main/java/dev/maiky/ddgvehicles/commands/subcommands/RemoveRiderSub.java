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

public class RemoveRiderSub extends ISub {

    public RemoveRiderSub(VehicleCommand command) {
        super(command);
    }

    @Override
    public void fire(CommandSender sender, String[] args, String label) {
        Player player = (Player) sender;

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

        VehicleManager manager = Main.getInstance().getVehicleManager();
        boolean add = manager.removeRider(license, offlinePlayer.getUniqueId());

        if (add) {
            player.sendMessage("§3Je hebt §b" + offlinePlayer.getName() + "§3 verwijderd als bestuurder van dit voertuig.");
        }else{
            player.sendMessage("§cDe speler §4" + offlinePlayer.getName() + "§c is geen bestuurder van dit voertuig.");
        }
    }

}
