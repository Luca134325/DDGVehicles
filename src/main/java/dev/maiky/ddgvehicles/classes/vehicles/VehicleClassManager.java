package dev.maiky.ddgvehicles.classes.vehicles;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VehicleClassManager {

    @Getter
    private final HashMap<String, HashMap<VehiclePart, List<UnindentifiedVehicleObject>>> vehicleDatabase = new HashMap<>();

    @Getter
    private final HashMap<Material, HashMap<Integer, UnindentifiedVehicleObject>> vehicleObjects = new HashMap<>();

    @Getter
    private final List<UnindentifiedVehicleObject> allModels = new ArrayList<>();

    public HashMap<VehiclePart, UnindentifiedVehicleObject> searchSpecificVehicle(String input) {
        List<UnindentifiedVehicleObject> list = new ArrayList<>();
        for (UnindentifiedVehicleObject unindentifiedVehicleObject :
                allModels) {
            if (unindentifiedVehicleObject.getModel().toLowerCase().contains(input.toLowerCase())) {
                list.add(unindentifiedVehicleObject);
            }
        }
        HashMap<VehiclePart, UnindentifiedVehicleObject> parts = new HashMap<>();
        for (UnindentifiedVehicleObject unindentifiedVehicleObject : list) {
            for (String s : vehicleDatabase.keySet()) {
                for (VehiclePart part : vehicleDatabase.get(s).keySet()) {
                    if (vehicleDatabase.get(s).get(part).contains(unindentifiedVehicleObject)) {
                        parts.put(part, unindentifiedVehicleObject);
                    }
                }
            }
        }

        return parts;
    }

    public HashMap<VehiclePart, UnindentifiedVehicleObject> search(String input) {
        List<UnindentifiedVehicleObject> list = new ArrayList<>();
        for (UnindentifiedVehicleObject unindentifiedVehicleObject :
        allModels) {
            if (unindentifiedVehicleObject.getModel().toLowerCase().contains(input.toLowerCase())) {
                list.add(unindentifiedVehicleObject);
            }
        }
        HashMap<VehiclePart, UnindentifiedVehicleObject> parts = new HashMap<>();
        for (UnindentifiedVehicleObject unindentifiedVehicleObject : list) {
            for (String s : vehicleDatabase.keySet()) {
                for (VehiclePart part : vehicleDatabase.get(s).keySet()) {
                    if (vehicleDatabase.get(s).get(part).contains(unindentifiedVehicleObject)) {
                        parts.put(part, unindentifiedVehicleObject);
                    }
                }
            }
        }

        return parts;
    }

    public void loadAll() throws FileNotFoundException {
        Material[] materials = {Material.DIAMOND_PICKAXE,Material.DIAMOND_SPADE,Material.DIAMOND_SWORD};
        for (Material material : materials) {
            load(material);
        }

        String[] colors = {"blue","green","orange","pink","red","white","yellow"};
        String[] models = {"range","canary","master"};

        int id = -1;
        for (String model : models) {
            for (String color : colors) {
                String builtString = model + "_neon_" + color;
                UnindentifiedVehicleObject ufo = new UnindentifiedVehicleObject((short)id, builtString,
                        "extra/addons/" + builtString, Material.TORCH,
                        VehiclePartType.ADDONS);
                HashMap<VehiclePart, List<UnindentifiedVehicleObject>> mapping = vehicleDatabase.get(model);
                List<UnindentifiedVehicleObject> list;
                if (mapping.containsKey(VehiclePart.NEON)) {
                    list = mapping.get(VehiclePart.NEON);
                } else {
                    list = new ArrayList<>();
                }
                list.add(ufo);
                mapping.put(VehiclePart.NEON, list);
                vehicleDatabase.put(model, mapping);
                HashMap<Integer, UnindentifiedVehicleObject> map;
                if (vehicleObjects.containsKey(Material.TORCH)) {
                    map = vehicleObjects.get(Material.TORCH);
                } else {
                    map = new HashMap<>();
                }
                map.put(id, ufo);
                vehicleObjects.put(Material.TORCH, map);
                id--;
            }
        }
    }

    public void load(Material material) throws FileNotFoundException {
        Gson gson = new Gson();
        File file = new File(".vehicles/item/" + (material == Material.DIAMOND_SPADE ? "diamond_shovel" : material.toString().toLowerCase()) + ".json");
        JsonObject jsonObject = gson.fromJson(new FileReader(file), JsonObject.class);

        JsonArray array = jsonObject.getAsJsonArray("overrides");

        for (JsonElement element : array) {
            JsonObject objectOfElement = element.getAsJsonObject();
            String model;
            String category;
            VehiclePart vehiclePart;

            try {
                String temp = objectOfElement.get("model").getAsString().split("/")[2];

                String[] splittedString = temp.split("_");

                if (splittedString.length > 4)
                    continue;

                model = splittedString[0];
                category = splittedString[1];
                vehiclePart = VehiclePart.valueOf(category.toUpperCase());
            } catch(ArrayIndexOutOfBoundsException | IllegalArgumentException exception) {
                continue;
            }

            JsonObject predicate = objectOfElement.getAsJsonObject("predicate");

            if (predicate.get("damage") == null) {
                continue;
            }

            double damage = predicate.get("damage").getAsDouble();
            if (damage == 0d) continue;
            short durability = (short) Math.round(damage * (material == Material.DIAMOND_SWORD ? 1562 : 1561));

            HashMap<VehiclePart, List<UnindentifiedVehicleObject>> uvo = new HashMap<>();
            if (vehicleDatabase.containsKey(model))
                uvo = vehicleDatabase.get(model);

            VehiclePartType type;
            try {
                type = VehiclePartType.valueOf(objectOfElement.get("model").getAsString().split("/")[1].toUpperCase());
            } catch (ArrayIndexOutOfBoundsException exception) {
                Bukkit.getLogger().warning("Error opgetreden bij model " + objectOfElement.get("model").getAsString() + ": het lijkt erop deze model zorgt voor een ArrayIndexOutOfBoundsException");
                continue;
            }

            UnindentifiedVehicleObject unindentifiedVehicleObject = new UnindentifiedVehicleObject(durability,
                    objectOfElement.get("model").getAsString(), material, type);

            List<UnindentifiedVehicleObject> uvo2 = new ArrayList<>();
            if (uvo.containsKey(vehiclePart)) {
                uvo2 = uvo.get(vehiclePart);
            }

            HashMap<Integer, UnindentifiedVehicleObject> hashMap = new HashMap<>();
            if (vehicleObjects.containsKey(material)) {
                hashMap = vehicleObjects.get(material);
            }
            hashMap.put((int)unindentifiedVehicleObject.getDamage(), unindentifiedVehicleObject);
            vehicleObjects.put(material, hashMap);

            uvo2.add(unindentifiedVehicleObject);
            uvo.put(vehiclePart, uvo2);
            vehicleDatabase.put(model, uvo);

            allModels.add(unindentifiedVehicleObject);
        }
    }

}
