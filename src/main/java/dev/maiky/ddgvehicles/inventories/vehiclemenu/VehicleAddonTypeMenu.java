package dev.maiky.ddgvehicles.inventories.vehiclemenu;

import dev.maiky.ddgvehicles.Main;
import dev.maiky.ddgvehicles.classes.vehicles.UnindentifiedVehicleObject;
import dev.maiky.ddgvehicles.classes.vehicles.VehiclePart;
import dev.maiky.ddgvehicles.classes.vehicles.VehiclePartType;
import dev.maiky.ddgvehicles.utils.ItemFactory;
import dev.maiky.ddgvehicles.utils.Skull;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class VehicleAddonTypeMenu implements InventoryProvider {

    public final SmartInventory inventory;
    public final VehiclePartType partType;
    public final String type;

    public VehicleAddonTypeMenu(VehiclePartType partType, String type) {
        this.partType = partType;
        this.type = type;
        this.inventory = SmartInventory.builder()
                .id("vehiclesAddonType")
                .provider(this)
                .size(6, 9)
                .title("§3Vehicles §8▶ §7Selecteer een soort")
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        ItemFactory itemFactory = new ItemFactory();
        int column = 0;
        int row = 0;
        for (VehiclePart part : VehiclePart.values()) {
            if (!Main.getVehicleClassManager().getVehicleDatabase().get(this.type).containsKey(part))
                continue;
            if (Main.getVehicleClassManager().getVehicleDatabase().get(this.type).get(part) == null)
                continue;

            int hits = 0;
            for (UnindentifiedVehicleObject unindentifiedVehicleObject : Main.getVehicleClassManager().getVehicleDatabase().get(this.type).get(part)) {
                if (!unindentifiedVehicleObject.getPartType().equals(this.partType)) continue;
                hits++;
            }

            if (hits == 0) continue;

            contents.set(row, column, ClickableItem.of(itemFactory.createItem(Material.BOOK,1,0,
                    part.toString()), event ->
            {
                VehicleMenu vehicleMenu = new VehicleMenu(this.type, part, this.partType);
                vehicleMenu.inventory.open(player);
            }));

            column++;
            if (column == 9) {
                column = 0;
                row++;
            }
        }

        contents.fillRow(4, ClickableItem.empty(new ItemFactory().createItem(Material.STAINED_GLASS_PANE,1,7," ")));
        contents.set(5, 4, ClickableItem.of(new ItemFactory().edit(
                Skull.getCustomSkull("a3852bf616f31ed67c37de4b0baa2c5f8d8fca82e72dbcafcba66956a81c4"),
                "&cTerug naar vorige menu"
        ), event ->
        {
            VehiclePartTypeMenu vehiclePartTypeMenu = new VehiclePartTypeMenu(this.type);
            vehiclePartTypeMenu.inventory.open(player);
        }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
