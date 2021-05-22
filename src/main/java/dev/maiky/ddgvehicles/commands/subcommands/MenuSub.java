package dev.maiky.ddgvehicles.commands.subcommands;

import dev.maiky.ddgvehicles.commands.VehicleCommand;
import dev.maiky.ddgvehicles.commands.depend.ISub;
import dev.maiky.ddgvehicles.inventories.vehiclemenu.VehicleTypeMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MenuSub extends ISub {

    public MenuSub(VehicleCommand command) {
        super(command);
        super.permission = "ddgvehicles.admin.menu";
    }

    @Override
    public void fire(CommandSender sender, String[] args, String label) {
        VehicleTypeMenu vehicleMenu = new VehicleTypeMenu();
        vehicleMenu.inventory.open((Player)sender);
    }
}
