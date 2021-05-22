package dev.maiky.ddgvehicles.commands.subcommands;

import dev.maiky.ddgvehicles.Main;
import dev.maiky.ddgvehicles.classes.vehicles.Part;
import dev.maiky.ddgvehicles.classes.vehicles.UnindentifiedVehicleObject;
import dev.maiky.ddgvehicles.classes.vehicles.VehicleManager;
import dev.maiky.ddgvehicles.classes.vehicles.VehiclePart;
import dev.maiky.ddgvehicles.commands.VehicleCommand;
import dev.maiky.ddgvehicles.commands.depend.ISub;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InfoSub extends ISub {

    public InfoSub(VehicleCommand command) {
        super(command);
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
            VehicleManager manager = Main.getInstance().getVehicleManager();
            String name = Bukkit.getOfflinePlayer(manager.getOwner(license)).getName();

            List<UUID> list = manager.getRiders(license);
            if (list == null) list = new ArrayList<>();
            list.add(manager.getOwner(license));

            StringBuilder builder = new StringBuilder();
            if (list.size() == 0) {
                builder.append(name);
            } else {
                for (UUID uuid : list) {
                    builder.append(Bukkit.getOfflinePlayer(uuid).getName()).append(", ");
                }
            }

            List<UUID> list2 = manager.getMembers(license);
            if (list2 == null) list2 = new ArrayList<>();
            StringBuilder builder2 = new StringBuilder();
            if (list2.size() == 0) {
                builder2.append("Geen");
            } else {
                for (UUID uuid : list2) {
                    builder2.append(Bukkit.getOfflinePlayer(uuid).getName()).append(", ");
                }
            }

            String model = manager.getModel(license);

            player.sendMessage("§2Kenteken: §a" + license);
            player.sendMessage("§2Eigenaar: §a" + name);
            player.sendMessage("§2Bestuurder" + (list.size() == 1 ? "" : "s") + ": §a" + (list.size() == 0 ? builder.toString() :
                    builder.substring(0, builder.length()-2)));
            player.sendMessage("§2Passagier" + (list2.size() == 1 ? "" : "s") + ": §a" + (list2.size() == 0 ? builder2.toString() :
                    builder2.substring(0, builder2.length()-2)));
            player.sendMessage("§2Addons:");

            String modelPath = manager.getModelPath(license);
            List<VehiclePart> vehiclePartList = VehiclePart.getPossibleParts(model);
            int parts = 0;
            for (VehiclePart vehiclePart : vehiclePartList) {
                if (modelPath.toUpperCase().contains(vehiclePart.toString()))
                    continue;

                Part part = manager.getPart(license, vehiclePart);

                if (part == null) continue;

                int currentPart = part.getMaterialId();
                if (currentPart == -1) {
                    continue;
                }

                parts++;

                UnindentifiedVehicleObject object = Main.getVehicleClassManager().getVehicleObjects()
                        .get(Material.valueOf(part.getMaterial().toUpperCase()))
                        .get(currentPart);

                if (object == null) {
                    for(Integer num : Main.getVehicleClassManager().getVehicleObjects()
                            .get(Material.valueOf(part.getMaterial())).keySet()) {
                        if (Main.getVehicleClassManager().getVehicleObjects().get(Material.valueOf(part.getMaterial()))
                                .get(num).getModel().equals(part.getModelPath())) {
                            object = Main.getVehicleClassManager().getVehicleObjects().get(Material.valueOf(part.getMaterial()))
                                    .get(num);
                        }
                    }
                }

                if (object == null) continue;

                String formattedName = format(object.getModel().split("/")[2]);

                player.sendMessage("§7- §a" + formattedName);
            }
            if (parts == 0) {
                player.sendMessage("§7- §aGeen addons gevonden");
            }
        }
    }

    public String format(String input) {
        String[] words = input.split("_");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            char[] chars = word.toCharArray();
            StringBuilder builder1 = new StringBuilder();
            builder1.append(String.valueOf(chars[0]).toUpperCase());
            for (int i = 1; i != chars.length; i++) {
                builder1.append(String.valueOf(chars[i]).toLowerCase());
            }
            builder.append(builder1.toString()).append(" ");
        }
        return builder.substring(0, builder.toString().length() - 1);
    }

}
