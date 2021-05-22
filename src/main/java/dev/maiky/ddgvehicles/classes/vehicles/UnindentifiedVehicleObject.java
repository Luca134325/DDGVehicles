package dev.maiky.ddgvehicles.classes.vehicles;

import lombok.Getter;
import org.bukkit.Material;

public class UnindentifiedVehicleObject {

    @Getter
    private short damage;
    @Getter
    private String model;
    @Getter
    private Material material;
    @Getter
    private VehiclePartType partType;
    @Getter
    private String mtCustom;

    public UnindentifiedVehicleObject(short damage, String model, Material material, VehiclePartType partType) {
        this.damage = damage;
        this.model = model;
        this.material = material;
        this.partType = partType;
        this.mtCustom = null;
    }

    public UnindentifiedVehicleObject(short damage, String mtCustom, String model, Material material, VehiclePartType partType) {
        this(damage, model, material, partType);
        this.mtCustom = mtCustom;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UnindentifiedVehicleObject{");
        sb.append("damage=").append(damage);
        sb.append(", model='").append(model).append('\'');
        sb.append(", material=").append(material);
        sb.append(", partType=").append(partType);
        sb.append(", mtCustom='").append(mtCustom).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
