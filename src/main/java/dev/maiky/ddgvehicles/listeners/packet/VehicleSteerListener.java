package dev.maiky.ddgvehicles.listeners.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import dev.maiky.ddgvehicles.Main;
import dev.maiky.ddgvehicles.classes.vehicles.*;
import dev.maiky.ddgvehicles.listeners.VehicleEnterListener;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.PacketPlayInSteerVehicle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;

public class VehicleSteerListener extends PacketAdapter {

    public static HashMap<Player, Double> velocityMapForward = new HashMap<>();
    public static HashMap<Player, Double> velocityMapUp = new HashMap<>();

    public VehicleSteerListener(Plugin plugin, ListenerPriority listenerPriority, PacketType[] types) {
        super(plugin, listenerPriority, types);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        Player player = event.getPlayer();

        if (player.getVehicle() == null) return;

        Entity entity = player.getVehicle();
        if (!(entity instanceof ArmorStand)) return;
        ArmorStand seat = (ArmorStand) entity;
        String customName = seat.getCustomName();
        if (customName == null) return;
        if (!customName.startsWith("RIDERSEAT")) return;

        VehicleManager manager = Main.getInstance().getVehicleManager();
        String license = customName.split("_")[1];
        Vehicle vehicle = Main.getAliveVehicles().get(license);

        if (vehicle == null) {
            vehicle = new Vehicle(license);
            Main.getAliveVehicles().put(license, vehicle);
        }

        if (vehicle.getFuel() <= 0) return;

        ArmorStand skin = SpawnedVehicleManager.getSkin(license);
        ArmorStand car = SpawnedVehicleManager.getCar(license);

        PacketPlayInSteerVehicle packet = (PacketPlayInSteerVehicle) event.getPacket().getHandle();

        int w = 0;
        boolean w2 = false;
        double w3 = velocityMapForward.get(event.getPlayer());

        double space3 = velocityMapUp.get(event.getPlayer());

        boolean shift = packet.d();
        boolean space = packet.c();
        float forward = packet.b();
        float side = packet.a();

        if (forward > 0) {
            w += 0.1;
            if (w == 0.1) {
                w2 = true;
            }
        }

        if (shift) {
            w3 = 0;
        }

        VehicleDefaultStatsEnum speedLink = VehicleDefaultStatsEnum.valueOf(manager.getModel(license));

        double maxSpeed = speedLink.maxSpeed;
        double optrekSpeed = speedLink.acceleration;
        double fuelUsage = speedLink.fuel;
        double decel = speedLink.brakingSpeed;

        double rotate = speedLink.rotateSpeed;
        double braking = speedLink.deceleration;
        double maxBackSpeed = speedLink.maxBackSpeed;

        List<VehiclePart> vehiclePartList = VehiclePart.getPossibleParts(manager.getModel(license));
        for (VehiclePart part : vehiclePartList) {
            Part p = manager.getPart(license, part);
            if (p == null) continue;
            Part.PartData data = p.getPartData();

            maxSpeed += maxSpeed / 100 * data.speed;
            optrekSpeed += optrekSpeed / 100 * data.acceleration;
            decel += decel / 100 * data.deceleration;
            fuelUsage += fuelUsage / 100 * data.fuel;
        }

        if (space) {
            if (!(w3 <= 0)) {
                w3 -= decel * 2;
            }
        }

        if (forward > 0) {
            if (!w2) {
                if (w3 == VehicleDefaultStatsEnum.kmToB(maxSpeed)) {
                    w3 = VehicleDefaultStatsEnum.kmToB(maxSpeed);
                }
                if (w3 <= VehicleDefaultStatsEnum.kmToB(maxSpeed)) {
                    manager.setKilometerStand(license, manager.getKilometerStand(license) +
                            VehicleDefaultStatsEnum.bToKm(w3));
                    w3 += (VehicleDefaultStatsEnum.kmToB(optrekSpeed) / 3.6);
                }
                car.setVelocity(car.getLocation().getDirection().multiply(w3).setY(-2));
                vehicle.setFuel(vehicle.getFuel() - 0.00025);
            }
        } else if (forward < 0) {
            if (!(w3 <= 0)) {
                w3 -= braking;
            } else {
                if (Math.abs(w3) == maxBackSpeed) {
                    return;
                }

                w3 -= decel;
                car.setVelocity(car.getLocation().getDirection().multiply(-0.1D).setY(-2));
            }
        } else {
            if (w3 <= 0) {
                w3 = 0;
            } else {
                w3 -= decel;
                car.setVelocity(car.getLocation().getDirection().multiply(w3).setY(-2));
            }
        }

        String model = manager.getModel(license);
        if (space) {
            if (model.equals("hercal")) {
                if (space3 >= MetricSystem.rate * 50)
                    if (car.getLocation().getY() >= 170.0D) {
                        space3 = 0.0D;
                    } else {
                        space3 = MetricSystem.rate * 50;
                    }
                if (space3 <= MetricSystem.rate * 50)
                    if (car.getLocation().getY() >= 170.0D) {
                        space3 = 0.0D;
                    } else {
                        space3 += 0.07;
                    }
            }
        } else if (model.equals("hercal")) {
            if (!car.isOnGround()) {
                if (space3 >= MetricSystem.rate * 50)
                    space3 = MetricSystem.rate / 50;
                if (space3 <= MetricSystem.rate * 50)
                    space3 = -0.25D;
            } else {
                space3 = 0.0D;
            }
        }

        if (side > 0) {

            float f = (float) (car.getLocation().getYaw() + -rotate);

            ((CraftArmorStand) car).getHandle().yaw = f;
            ((CraftArmorStand) skin).getHandle().yaw = f;
            ((CraftArmorStand) seat).getHandle().yaw = f;

            List<ArmorStand> seat1 = SpawnedVehicleManager.seatListExcludeRider(license);
            for (ArmorStand seatStand : seat1) {
                ((CraftArmorStand) seatStand).getHandle().yaw = f;
            }

            for (ArmorStand partStand : SpawnedVehicleManager.partList(license)) {
                ((CraftArmorStand) partStand).getHandle().yaw = f;
            }

        } else if (side < 0) {

            float f = (float) (car.getLocation().getYaw() + rotate);

            ((CraftArmorStand) car).getHandle().yaw = f;
            ((CraftArmorStand) skin).getHandle().yaw = f;
            ((CraftArmorStand) seat).getHandle().yaw = f;

            List<ArmorStand> seat1 = SpawnedVehicleManager.seatListExcludeRider(license);
            for (ArmorStand seatStand : seat1) {
                ((CraftArmorStand) seatStand).getHandle().yaw = f;
            }
            for (ArmorStand partStand : SpawnedVehicleManager.partList(license)) {
                ((CraftArmorStand) partStand).getHandle().yaw = f;
            }
        }

        for (ArmorStand armorStand : SpawnedVehicleManager.seatListExcludeRider(license)) {
            Seat s = VehicleSeatLink.valueOf(manager.getModel(license)).getSeats()[
                    Integer.parseInt(armorStand.getCustomName().split("_")[0]
                            .replaceAll("OSEAT", "").replaceAll("RIDERSEAT", ""))];
            double x = s.offsetX;
            double y = s.offsetY;
            double z = s.offsetZ;
            Location carloc = car.getLocation().clone();
            Location newcarloc = carloc.add(carloc.getDirection().multiply(x));
            float z2 = (float) (newcarloc.getZ() + z * Math.sin(Math.toRadians(newcarloc.getYaw())));
            float x2 = (float) (newcarloc.getX() + z * Math.cos(Math.toRadians(newcarloc.getYaw())));
            Location loc = new Location(car.getWorld(), x2, newcarloc.getY() + y, z2, newcarloc.getYaw(), newcarloc.getPitch());

            ((CraftArmorStand) armorStand).getHandle().setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        }

        if (model.equals("hercal")) {
            Vector finalmain;
            if (car.isOnGround()) {
                finalmain = (new Vector(car.getLocation().getDirection().getX(), space3, car.getLocation().getDirection().getZ())).multiply(0.0D);
            } else {
                finalmain = (new Vector(car.getLocation().getDirection().getX(), space3, car.getLocation().getDirection().getZ())).multiply(w3);
            }
            if (car.getLocation().getY() >= 170.0D) {
                space3 = 0.0D;
            } else {
                finalmain.setY(space3);
            }
            car.setVelocity(finalmain);
            ArmorStand armorStand = VehicleEnterListener.wiekenMap.get(license);
            for (ArmorStand wiekenStand : new ArmorStand[]{armorStand}) {
                Location locvp = car.getLocation().clone();
                Location fbvp = locvp.add(locvp.getDirection().setY(0).normalize().multiply(-0.8D));
                float zvp = (float)(fbvp.getZ() + 0.0D * Math.sin(Math.toRadians(fbvp.getYaw())));
                float xvp = (float)(fbvp.getX() + 0.0D * Math.cos(Math.toRadians(fbvp.getYaw())));
                Location locnew = new Location(car.getWorld(), xvp, car.getLocation().getY(), zvp, fbvp.getYaw(), fbvp.getPitch());
                locnew.setYaw(wiekenStand.getLocation().getYaw() + 13.0F);
                ((CraftArmorStand)wiekenStand).getHandle().setLocation(locnew.getX(), locnew.getY(), locnew.getZ(), locnew.getYaw(), locnew.getPitch());
            }
        }

        velocityMapForward.remove(event.getPlayer());
        velocityMapForward.put(event.getPlayer(), w3);

        velocityMapUp.remove(event.getPlayer());
        velocityMapUp.put(event.getPlayer(), space3);

        Seat seatObject2 = VehicleSeatLink.valueOf(manager.getModel(license)).getSeats()[0];

        double riderX, riderZ;
        riderX = seatObject2.offsetX;
        riderZ = seatObject2.offsetZ;

        Location carloc = car.getLocation().clone();
        Location newcarloc = carloc.add(carloc.getDirection().multiply(riderX));
        float z2 = (float) (newcarloc.getZ() + riderZ * Math.sin(Math.toRadians(newcarloc.getYaw())));
        float x2 = (float) (newcarloc.getX() + riderZ * Math.cos(Math.toRadians(newcarloc.getYaw())));
        Location loc = new Location(car.getWorld(), x2, newcarloc.getY() + seatObject2.offsetY, z2, newcarloc.getYaw(), newcarloc.getPitch());
        EntityArmorStand stand = ((CraftArmorStand) seat).getHandle();

        for (ArmorStand partStand : SpawnedVehicleManager.partList(license)) {
            ((CraftArmorStand) partStand).getHandle().setPosition(car.getLocation().getX(), car.getLocation().getY(),
                    car.getLocation().getZ());
        }

        stand.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }
}
