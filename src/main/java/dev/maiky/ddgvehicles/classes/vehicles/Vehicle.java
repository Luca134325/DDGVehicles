package dev.maiky.ddgvehicles.classes.vehicles;

import dev.maiky.ddgvehicles.Main;
import dev.maiky.ddgvehicles.utils.ItemFactory;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Vehicle {
    @Getter
    private static final ItemFactory itemFactory = new ItemFactory();

    @Getter
    private final VehicleManager vehicleManager;
    @Getter
    private final List<VehiclePart> compatibleParts;
    @Getter
    private final List<ArmorStand> components = new ArrayList<>();
    @Getter
    private final String license;
    @Getter
    private final String model;
    @Getter
    private final Material material;
    @Getter @Setter
    private double fuel;

    public Vehicle(String license) {
        // Vehicle Manager
        this.vehicleManager = Main.getInstance().getVehicleManager();

        // General Information
        this.license = license;
        this.model = this.vehicleManager.getModel(this.license);
        this.material = this.vehicleManager.getMaterial(this.license);

        // Compatible Parts
        this.compatibleParts = VehiclePart.getPossibleParts(this.model);

        // Fuel
        this.fuel = this.vehicleManager.getFuel(this.license);
    }

    public void spawn(Location location, ItemStack itemStack) {
        int main = this.vehicleManager.getMainFrame(this.license);
        if (main == -1) {
            Bukkit.getLogger().warning("Error: Voertuig " + this.license + " kan NIET spawnen zonder main frame, dit moet handmatig in de database gefixed worden.");
            return;
        }

        ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class);

        // Options
        armorStand.setHelmet(itemStack);
        armorStand.setCustomName("MAINFRAME_" + license);
        armorStand.setVisible(false);
        armorStand.setCustomNameVisible(false);
        armorStand.setMarker(false);
        armorStand.setGravity(true);
        armorStand.setFireTicks(0);
        this.getComponents().add(armorStand);

        ArmorStand car = location.getWorld().spawn(location, ArmorStand.class);
        car.setCustomName("CAR_" + license);
        car.setVisible(false);
        car.setCustomNameVisible(false);
        car.setMarker(false);
        car.setGravity(true);
        car.setFireTicks(0);
        this.getComponents().add(car);

        for (VehiclePart part : this.compatibleParts) {
            Part dbPart = this.vehicleManager.getPart(this.license, part);

            if (dbPart == null) continue;

            int partId = dbPart.getMaterialId();
            ItemStack helmet = getItemFactory().createItem(Material.valueOf(dbPart.getMaterial().toUpperCase()), 1, (short)partId, part.toString(), Arrays.asList("Dit is een server owned item!",
                    "Het niet melden van het bezit van dit item is bannable!"), true);

            if (partId == 0) {
                UnindentifiedVehicleObject ufo = null;
                for(Integer num : Main.getVehicleClassManager().getVehicleObjects()
                        .get(helmet.getType()).keySet()) {
                    if (Main.getVehicleClassManager().getVehicleObjects().get(helmet.getType())
                    .get(num).getModel().equals(dbPart.getModelPath())) {
                        ufo = Main.getVehicleClassManager().getVehicleObjects().get(helmet.getType())
                                .get(num);
                    }
                }

                if (ufo == null) continue;

                net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(helmet);
                NBTTagCompound tagCompound = nms.getTag() == null ? new NBTTagCompound() :
                        nms.getTag();
                tagCompound.setString("mtcustom", ufo.getMtCustom());
                nms.setTag(tagCompound);
                helmet = CraftItemStack.asCraftMirror(nms);
            }

            ArmorStand partArmorStand = location.getWorld().spawn(location, ArmorStand.class);
            partArmorStand.setHelmet(helmet);
            partArmorStand.setCustomName(part.toString() + "_" + license);
            partArmorStand.setVisible(false);
            partArmorStand.setCustomNameVisible(false);
            partArmorStand.setMarker(false);
            partArmorStand.setGravity(true);
            partArmorStand.setFireTicks(0);
            this.getComponents().add(partArmorStand);
        }

        VehicleSeatLink seatLink;
        try {
            seatLink = VehicleSeatLink.valueOf(this.model);
        } catch (IllegalArgumentException exception) {
            return;
        }

        int i = 0;
        for (Seat seat : seatLink.getSeats()) {
            double x = seat.offsetX, y = seat.offsetY, z = seat.offsetZ;

            Location seatLocation = location.clone().add(z, y, x);
            ArmorStand partArmorStand = seatLocation.getWorld().spawn(seatLocation, ArmorStand.class);
            if (i == 0) {
                partArmorStand.setCustomName("RIDERSEAT" + i + "_" + license);
            } else {
                partArmorStand.setCustomName("OSEAT" + i + "_" + license);
            }
            partArmorStand.setVisible(false);
            partArmorStand.setCustomNameVisible(false);
            partArmorStand.setMarker(false);
            partArmorStand.setGravity(false);
            partArmorStand.setFireTicks(0);
            this.getComponents().add(partArmorStand);
            i++;
        }
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Vehicle{");
        sb.append("vehicleManager=").append(vehicleManager);
        sb.append(", compatibleParts=").append(compatibleParts);
        sb.append(", components=").append(components);
        sb.append(", license='").append(license).append('\'');
        sb.append(", model='").append(model).append('\'');
        sb.append(", material=").append(material);
        sb.append('}');
        return sb.toString();
    }
}
