package dev.maiky.ddgvehicles.commands.depend;

import dev.maiky.ddgvehicles.commands.VehicleCommand;
import org.bukkit.command.CommandSender;

;

public abstract class ISub {

    public VehicleCommand command;
    public String permission;

    public ISub(VehicleCommand command) {
        this.command = command;
    }

    public abstract void fire(CommandSender sender, String[] args, String label);

}
