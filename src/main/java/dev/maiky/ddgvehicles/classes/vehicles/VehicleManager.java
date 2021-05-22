package dev.maiky.ddgvehicles.classes.vehicles;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.*;
import dev.maiky.ddgvehicles.Main;
import dev.maiky.ddgvehicles.utils.BukkitSerialization;
import dev.maiky.ddgvehicles.utils.ItemFactory;
import lombok.Getter;
import org.bson.codecs.configuration.CodecRegistry;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.*;

public class VehicleManager {

    private MongoClient mongoClient;
    private DB database;
    private DBCollection collection;

    @Deprecated
    public VehicleManager(CodecRegistry codecRegistry) {
        String mongoHost = Main.getInstance().getConfig().getString("mongodb.host"),
        database = Main.getInstance().getConfig().getString("mongodb.database");

        this.mongoClient = new MongoClient(mongoHost, MongoClientOptions.builder().codecRegistry(codecRegistry).build());
        this.database = this.mongoClient.getDB(database);
        this.collection = this.database.getCollection("vehicles");

        // Iteration Table
        if (this.collection.findOne() == null) {
            DBObject databaseObject = new BasicDBObject("iteration", true);
            databaseObject.put("value", 0);
            this.collection.insert(databaseObject);
        }
    }

    public ItemStack[] getTrunk(String license) {
        DBObject dbObject = new BasicDBObject("license", license);
        DBObject found = this.collection.findOne(dbObject);

        if (found == null) {
            return null;
        }

        if (found.get("trunk") == null) {
            return new ItemStack[0];
        }

        String base64 = (String) found.get("trunk");
        try {
            ItemStack[] items = BukkitSerialization.itemStackArrayFromBase64(base64);
            List<ItemStack> filtered = new ArrayList<>();
            for (ItemStack itemStack : items) {
                if (itemStack == null) continue;
                if (itemStack.getType() == Material.AIR)continue;
                filtered.add(itemStack);
            }
            ItemStack[] filteredArr = new ItemStack[filtered.size()];
            for (int i = 0; i < filtered.size(); i++)
                filteredArr[i] = filtered.get(i);
            return filteredArr;
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void updateTrunk(String license, ItemStack[] items) {
        List<ItemStack> filtered = new ArrayList<>();
        for (ItemStack itemStack : items) {
            if (itemStack == null) continue;
            if (itemStack.getType() == Material.AIR)continue;
            filtered.add(itemStack);
        }
        ItemStack[] filteredArr = new ItemStack[filtered.size()];
        for (int i = 0; i < filtered.size(); i++)
            filteredArr[i] = filtered.get(i);

        DBObject dbObject = new BasicDBObject("license", license);
        DBObject found = this.collection.findOne(dbObject);

        if (found == null) {
            return;
        }

        BasicDBObject set = new BasicDBObject("$set", dbObject);
        set.append("$set", new BasicDBObject("trunk", BukkitSerialization.itemStackArrayToBase64(filteredArr)));
        this.collection.update(found, set);
    }

    /*
    General
     */
    public VehicleCreationResponse createVehicle(String modelPath, String pattern, UnindentifiedVehicleObject vehicleObject) {
        String modelName = modelPath.split("/")[2].split("_")[0];
        String license = generateLicense(pattern);
        int it = iterate();
        DBObject dbObject = new BasicDBObject("id", it);
        dbObject.put("model", modelName);
        dbObject.put("modelPath", modelPath);
        dbObject.put("license", license);
        dbObject.put("trunk", BukkitSerialization.itemStackArrayToBase64(new ItemStack[0]));
        dbObject.put("main", (int)vehicleObject.getDamage());
        dbObject.put("material", vehicleObject.getMaterial().toString());
        dbObject.put("fuel", 100d);
        for (VehiclePart possiblePart : VehiclePart.getPossibleParts(modelName)) {
            dbObject.put(possiblePart.toString(), null);
        }
        this.collection.insert(dbObject);

        ItemFactory itemFactory = new ItemFactory();
        String[] split0 = modelPath.split("/");
        String[] split = split0[2].split("_");
        String name = split[0] + "_" + split[2];

        return new VehicleCreationResponse(
                itemFactory.createItem(vehicleObject.getMaterial(), 1, vehicleObject.getDamage(),
                        "&6" + formatVehicleName(name), Arrays.asList(" ", "&8➥ &eKenteken: &7" + license), true),
                license,
                it,
                formatVehicleName(name)
        );
    }

    public int getMainFrame(String license) {
        Object o = getKeyByLicense(license, "main");
        if (o == null) return -1;
        if (!(o instanceof Integer)) return -1;
        return (int) o;
    }

    public Material getMaterial(String license) {
        Object o = getKeyByLicense(license, "material");
        if (o == null) return null;
        if (!(o instanceof String)) return null;
        return Material.valueOf((String)o);
    }

    public void setOwner(String license, UUID owner) {
        setKeyByLicense(license, "owner", owner.toString());
    }

    public boolean licenseExists(String license) {
        DBObject dbObject = new BasicDBObject("license", license);
        DBObject find = this.collection.findOne(dbObject);
        return find != null;
    }

    public UUID getOwner(String license) {
        Object o = getKeyByLicense(license, "owner");
        if (o == null)
            return null;
        return UUID.fromString((String)o);
    }

    public String getModel(String license) {
        Object o = getKeyByLicense(license, "model");
        if (o == null)
            return null;
        return (String) o;
    }

    public String getModelPath(String license) {
        Object o = getKeyByLicense(license, "modelPath");
        if (o == null)
            return null;
        return (String) o;
    }

    public double getFuel(String license) {
        Object o = getKeyByLicense(license, "fuel");
        if (o == null)
            return 100d;
        return (double) o;
    }

    public void setFuel(String license, double fuel) {
        setKeyByLicense(license, "fuel", fuel < 0 ? 0 : fuel);
    }

    public boolean removeRider(String license, UUID uuid) {
        List<UUID> riders = getRiders(license);
        if (riders == null)
            riders = new ArrayList<>();
        if (!riders.contains(uuid))
            return false;
        riders.remove(uuid);

        BasicDBList list = new BasicDBList();
        list.addAll(riders);

        setKeyByLicense(license, "riders", list);
        return true;
    }

    public boolean removeMember(String license, UUID uuid) {
        List<UUID> members = getMembers(license);
        if (members == null)
            members = new ArrayList<>();
        if (!members.contains(uuid))
            return false;
        members.remove(uuid);

        BasicDBList list = new BasicDBList();
        list.addAll(members);

        setKeyByLicense(license, "members", list);
        return true;
    }

    public boolean addRider(String license, UUID uuid) {
        List<UUID> riders = getRiders(license);
        if (riders == null)
            riders = new ArrayList<>();
        if (riders.contains(uuid))
            return false;
        riders.add(uuid);

        BasicDBList list = new BasicDBList();
        list.addAll(riders);

        setKeyByLicense(license, "riders", list);
        return true;
    }

    public void setKilometerStand(String license, double km) {
        setKeyByLicense(license, "kmStand", km);
    }

    public double getKilometerStand(String license) {
        Object kmStand = getKeyByLicense(license, "kmStand");
        if (kmStand == null)
            return 0d;
        return (double) kmStand;
    }

    public boolean addMember(String license, UUID uuid) {
        List<UUID> members = getMembers(license);
        if (members == null)
            members = new ArrayList<>();
        if (members.contains(uuid))
            return false;
        members.add(uuid);

        BasicDBList list = new BasicDBList();
        list.addAll(members);

        setKeyByLicense(license, "members", list);
        return true;
    }

    public List<UUID> getMembers(String license) {
        Object o = getKeyByLicense(license, "members");
        if (o == null)
            return null;
        List<UUID> members = new ArrayList<>();
        BasicDBList list = (BasicDBList) o;
        for (Object o1 : list) {
            members.add((UUID)o1);
        }
        return members;
    }

    public List<UUID> getRiders(String license) {
        Object o = getKeyByLicense(license, "riders");
        if (o == null)
            return null;
        List<UUID> riders = new ArrayList<>();
        BasicDBList list = (BasicDBList) o;
        for (Object o1 : list) {
            riders.add((UUID)o1);
        }
        return riders;
    }

    public Part.PartData getTotalPartDataBoost(String license) {
        double speed = 0d, acceleration = 0d, deceleration = 0d, fuel = 0d;

        List<VehiclePart> vehiclePartList = VehiclePart.getPossibleParts(getModel(license));
        for (VehiclePart part : vehiclePartList) {
            Part p = getPart(license, part);
            if (p == null) continue;
            Part.PartData data = p.getPartData();

            speed += data.speed;
            acceleration += data.acceleration;
            deceleration += data.deceleration;
            fuel += data.fuel;
        }

        Part.PartData partData = new Part.PartData();
        partData.speed = speed;
        partData.acceleration = acceleration;
        partData.deceleration = deceleration;
        partData.fuel = fuel;

        return partData;
    }

    public void setPart(String license, VehiclePart part, Part partObject) {
        setKeyByLicense(license, part.toString(), partObject);
    }

    public Part getPart(String license, VehiclePart part) {
        Object o = getKeyByLicense(license, part.toString());

        if (o == null)
            return null;

        Part p = new Gson().fromJson(o.toString(), new TypeToken< Part >(){}.getType());
        return p;
    }

    /*
    Main Functions
     */

    public static class VehicleCreationResponse {
        @Getter
        private ItemStack itemStack;
        @Getter
        private String license;
        @Getter
        private int id;
        @Getter
        private String name;

        public VehicleCreationResponse(ItemStack itemStack, String license, int id, String name) {
            this.itemStack = itemStack;
            this.license = license;
            this.id = id;
            this.name = name;
        }
    }

    private String formatVehicleName(String input) {
        String[] words = input.split("_");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            char[] chars = word.toCharArray();
            StringBuilder builder1 = new StringBuilder();
            builder1.append(String.valueOf(chars[0]).toUpperCase());
            for (int i = 1; i != chars.length; i++) {
                builder1.append(String.valueOf(chars[i]).toLowerCase());
            }
            builder.append(builder1.toString()).append(" ");
        }
        return builder.substring(0, builder.toString().length() - 1);
    }

    private String generateLicense(String pattern) {
        char[] chars = {'a','b','c','d','e','f','0','1','2','3','4','5','6','7','8','9'};
        char[] letters = pattern.toCharArray();
        StringBuilder builder = new StringBuilder();

        for (char c : letters) {
            String letter = String.valueOf(c);
            if (letter.equals("#")) {
                builder.append(chars[new Random().nextInt(chars.length)]);
                continue;
            }
            builder.append("-");
        }

        return builder.toString().toUpperCase();
    }

    private int iterate() {
        DBObject databaseObject = new BasicDBObject("iteration", true);
        DBObject find = this.collection.findOne(databaseObject);

        int num = ((int) find.get("value")) + 1;

        BasicDBObject set = new BasicDBObject("$set", databaseObject);
        set.append("$set", new BasicDBObject("value", num));
        this.collection.update(find, set);
        return num;
    }

    private void setKeyByLicense(String id, String key, Object value) {
        DBObject dbObject = new BasicDBObject("license", id);
        DBObject found = this.collection.findOne(dbObject);

        if (found == null) return;

        BasicDBObject set = new BasicDBObject("$set", dbObject);
        set.append("$set", new BasicDBObject(key, value));
        this.collection.update(found, set);
    }

    private Object getKeyByLicense(String id, String key) {
        DBObject dbObject = new BasicDBObject("license", id);
        DBObject found = this.collection.findOne(dbObject);

        if (found == null) return null;
        if (found.get(key) == null) return null;

        return found.get(key);
    }

    private void setKey(int id, String key, Object value) {
        DBObject dbObject = new BasicDBObject("id", id);
        DBObject found = this.collection.findOne(dbObject);

        if (found == null) return;

        BasicDBObject set = new BasicDBObject("$set", dbObject);
        set.append("$set", new BasicDBObject(key, value));
        this.collection.update(found, set);
    }

    private Object getKey(int id, String key) {
        DBObject dbObject = new BasicDBObject("id", id);
        DBObject found = this.collection.findOne(dbObject);

        if (found == null) return null;
        if (found.get(key) == null) return null;

        return found.get(key);
    }

}
