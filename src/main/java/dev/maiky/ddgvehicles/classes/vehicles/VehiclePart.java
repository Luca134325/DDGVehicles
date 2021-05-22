package dev.maiky.ddgvehicles.classes.vehicles;

import dev.maiky.ddgvehicles.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public enum VehiclePart {

    FRAME,BODY,TIRES,SADDLE,KIT,SPOILER,ROOF,FRONT,CHASSIS,BODYKITFRONT,BACK,ENGINE,BODYKITBACK,
    WIELKAS,BODYKIT,WINDOW,BACKBUMPER,VOORBUMPER,FRONTWINDOW,WINDOWFRAME,GRILL,KOPLAMP,LIGHTS,
    PICKUP,SEAT,FRONTLIGHTS,WINDOWBACK,ARMORKIT,BUMPER,ROOFFRAME,GRID,BACKLIGHTS,UITLAAT,SIREN,
    FRONTGRILL,BACKWINDOW,SPATBORD,BOOMBOX,WINDOWFRONT,VEHICLE,UNDER,NEON;

    public static List<VehiclePart> getPossibleParts(String model) {
        List<VehiclePart> parts = new ArrayList<>();

        if (!Main.getVehicleClassManager().getVehicleDatabase().containsKey(model)) return parts;

        Set<VehiclePart> set = Main.getVehicleClassManager().getVehicleDatabase().get(model).keySet();
        parts.addAll(set);

        return parts;
    }

}
