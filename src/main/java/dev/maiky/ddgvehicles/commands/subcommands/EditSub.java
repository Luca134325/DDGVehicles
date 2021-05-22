package dev.maiky.ddgvehicles.commands.subcommands;

import dev.maiky.ddgvehicles.Main;
import dev.maiky.ddgvehicles.classes.vehicles.VehicleManager;
import dev.maiky.ddgvehicles.commands.VehicleCommand;
import dev.maiky.ddgvehicles.commands.depend.ISub;
import dev.maiky.ddgvehicles.inventories.edit.VehicleEditGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class EditSub extends ISub {

    public EditSub(VehicleCommand command) {
        super(command);
        super.permission = "ddgvehicles.vehicle.edit";
    }

    @Override
    public void fire(CommandSender sender, String[] args, String label) {
        Player player = (Player) sender;
        if (args.length == 1) {
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
            VehicleEditGUI gui = new VehicleEditGUI(license);
            gui.open(player);
        } else if (args.length == 2) {
            if (!player.hasPermission("ddgvehicles.admin.edit")) {
                player.sendMessage("§cJe hebt geen permissions om dit commando uit te voeren.");
                return;
            }

            String license = args[1];

            VehicleManager manager = Main.getInstance().getVehicleManager();

            if (!manager.licenseExists(license)) {
                sender.sendMessage("§cEr bestaat geen voertuig met het kenteken §4" + license + "§c.");
                return;
            }

            VehicleEditGUI gui = new VehicleEditGUI(license);
            gui.open(player);
        }
    }

}
