package dev.maiky.ddgvehicles.classes.vehicles;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpawnedVehicleManager {

    public static EnterResponse findNonOccupiedSeatAndEnter(String kenteken, Player playerObject, int seat) {
        List<ArmorStand> armorStands = seatList(kenteken);

        ArmorStand armorStand;
        try {
            armorStand = armorStands.get(seat);
            armorStand.setPassenger(playerObject);
        } catch (Exception exception) {
            return new EnterResponse(false, -1);
        }

        return new EnterResponse(true, seat);
    }

    public static ArmorStand getSkin(String kenteken) {
        List<ArmorStand> list = componentList(kenteken);

        for (ArmorStand armorStand : list) {
            if (armorStand.getCustomName().startsWith("MAINFRAME_")) {
                return armorStand;
            }
        }

        return null;
    }

    public static ArmorStand getCar(String kenteken) {
        List<ArmorStand> list = componentList(kenteken);

        for (ArmorStand armorStand : list) {
            if (armorStand.getCustomName().startsWith("CAR")) {
                return armorStand;
            }
        }

        return null;
    }

    public static HashMap<VehiclePart, ArmorStand> partMap(String kenteken) {
        HashMap<VehiclePart, ArmorStand> armorStands = new HashMap<>();
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (!(entity instanceof ArmorStand)) continue;

                if (entity.getCustomName() == null) continue;
                String customName = entity.getCustomName();
                if (!customName.endsWith(kenteken))continue;

                if (customName.startsWith("RIDERSEAT") || customName.startsWith("OSEAT"))
                    continue;

                VehiclePart part = null;
                try {
                    part = VehiclePart.valueOf(customName.split("_")[0]);
                } catch (IllegalArgumentException ignored) {
                }

                ArmorStand armorStand = (ArmorStand) entity;
                armorStands.put(part, armorStand);
            }
        }
        return armorStands;
    }

    public static List<ArmorStand> partList(String kenteken) {
        List<ArmorStand> armorStands = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (!(entity instanceof ArmorStand)) continue;

                if (entity.getCustomName() == null) continue;
                String customName = entity.getCustomName();
                if (!customName.endsWith(kenteken))continue;

                if (customName.startsWith("RIDERSEAT") || customName.startsWith("OSEAT"))
                    continue;

                ArmorStand armorStand = (ArmorStand) entity;
                armorStands.add(armorStand);
            }
        }
        return armorStands;
    }

    public static List<ArmorStand> componentList(String kenteken) {
        List<ArmorStand> armorStands = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (!(entity instanceof ArmorStand)) continue;

                if (entity.getCustomName() == null) continue;
                String customName = entity.getCustomName();
                if (!customName.endsWith(kenteken))continue;

                ArmorStand armorStand = (ArmorStand) entity;
                armorStands.add(armorStand);
            }
        }
        return armorStands;
    }

    public static List<ArmorStand> seatListExcludeRider(String kenteken) {
        List<ArmorStand> armorStands = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (!(entity instanceof ArmorStand)) continue;

                if (entity.getCustomName() == null) continue;
                String customName = entity.getCustomName();
                if (!customName.endsWith(kenteken))continue;
                String[] split = customName.split("_");
                String type = split[0];

                if (!type.substring(0, type.length()-1).equals("OSEAT")) continue;

                ArmorStand armorStand = (ArmorStand) entity;
                armorStands.add(armorStand);
            }
        }
        return armorStands;
    }

    public static boolean isOccupied(String license, int i) {
        List<ArmorStand> list = seatList(license);
        return list.get(i).getPassenger() != null;
    }

    public static List<ArmorStand> seatList(String kenteken) {
        List<ArmorStand> armorStands = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (!(entity instanceof ArmorStand)) continue;

                if (entity.getCustomName() == null) continue;
                String customName = entity.getCustomName();
                if (!customName.endsWith(kenteken))continue;
                String[] split = customName.split("_");
                String type = split[0];

                if (!type.startsWith("RIDERSEAT") && !type.substring(0, type.length()-1).equals("OSEAT")
                        && !type.endsWith(kenteken)) continue;

                ArmorStand armorStand = (ArmorStand) entity;
                armorStands.add(armorStand);
            }
        }
        return armorStands;
    }

    public static ItemStack despawnVehicle(String kenteken) {
        ArmorStand mainFrame = null;
        List<ArmorStand> armorStands = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (!(entity instanceof ArmorStand)) continue;

                if (entity.getCustomName() == null) continue;
                String customName = entity.getCustomName();
                if (!customName.endsWith(kenteken))continue;

                ArmorStand armorStand = (ArmorStand) entity;
                armorStands.add(armorStand);

                if (customName.startsWith("MAINFRAME_"))
                    mainFrame = armorStand;
            }
        }

        // Remove Components
        armorStands.forEach(ArmorStand::remove);

        // Return Main Item
        ItemStack itemStack = null;
        if (mainFrame != null)
            itemStack = mainFrame.getHelmet();
        return itemStack;
    }

}