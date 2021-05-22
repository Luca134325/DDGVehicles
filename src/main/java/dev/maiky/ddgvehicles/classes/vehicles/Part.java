package dev.maiky.ddgvehicles.classes.vehicles;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

public class Part {

    @Getter @Setter
    private ObjectId id;

    @Getter @Setter
    private String modelPath, material;

    @Getter @Setter
    private String itemBase64;

    @Getter @Setter
    private int materialId;

    @Getter @Setter
    private PartData partData;

    public Part(String modelPath, String material, int materialId, PartData partData, String itemBase64) {
        this.modelPath = modelPath;
        this.material = material;
        this.materialId = materialId;
        this.partData = partData;
        this.itemBase64 = itemBase64;
    }

    public static class PartData {

        @Getter @Setter
        private ObjectId id;

        @Getter @Setter
        public double deceleration = 0d, fuel = 0d, speed = 0d, acceleration = 0d;

    }

}
