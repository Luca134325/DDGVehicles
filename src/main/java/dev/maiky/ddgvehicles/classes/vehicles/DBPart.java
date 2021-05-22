package dev.maiky.ddgvehicles.classes.vehicles;

import lombok.Getter;

public class DBPart {

    @Getter
    private String material;
    @Getter
    private int id;

    public DBPart(String material, int id) {
        this.material = material;
        this.id = id;
    }

}
