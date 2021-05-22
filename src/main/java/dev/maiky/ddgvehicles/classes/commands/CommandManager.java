package dev.maiky.ddgvehicles.classes.commands;

import dev.maiky.ddgvehicles.Main;
import org.bukkit.command.Command;
import org.bukkit.command.defaults.BukkitCommand;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class CommandManager {

    public void registerAllCommands() {
        //        //Getting command map from CraftServer
        Method commandMap = null;
        try {
            commandMap = Main.getInstance().getServer().getClass().getMethod("getCommandMap", null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        //Invoking the method and getting the returned object (SimpleCommandMap)
        Object cmdmap = null;
        try {
            if (commandMap != null) {
                cmdmap = commandMap.invoke(Main.getInstance().getServer(), null);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        //getting register method with parameters String and Command from SimpleCommandMap
        Method register = null;
        try {
            if (cmdmap != null) {
                register = cmdmap.getClass().getMethod("register", String.class, Command.class);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        Reflections reflections = new Reflections("dev.maiky.ddgvehicles.commands");
        Set<Class<? extends BukkitCommand>> classes = reflections.getSubTypesOf(BukkitCommand.class);
        for (Class<? extends BukkitCommand> clazz : classes) {
            //Registering the command provided above
            BukkitCommand command = null;
            try {
                command = clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            try {
                if (register != null) {
                    if (command != null) {
                        register.invoke(cmdmap, command.getName(), command);
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            //All the exceptions thrown above are due to reflection, They will be thrown if any of the above methods
            //and objects used above change location or turn private. IF they do, let me know to update the thread!
        }
    }

}
