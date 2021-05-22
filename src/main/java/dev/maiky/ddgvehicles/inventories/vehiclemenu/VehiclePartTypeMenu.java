package dev.maiky.ddgvehicles.inventories.vehiclemenu;

import dev.maiky.ddgvehicles.Main;
import dev.maiky.ddgvehicles.classes.vehicles.UnindentifiedVehicleObject;
import dev.maiky.ddgvehicles.classes.vehicles.VehicleClassManager;
import dev.maiky.ddgvehicles.classes.vehicles.VehiclePart;
import dev.maiky.ddgvehicles.classes.vehicles.VehiclePartType;
import dev.maiky.ddgvehicles.utils.ItemFactory;
import dev.maiky.ddgvehicles.utils.Skull;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;

public class VehiclePartTypeMenu implements InventoryProvider {

    public final SmartInventory inventory;
    public final String type;

    public VehiclePartTypeMenu(String type) {
        this.type = type;
        this.inventory = SmartInventory.builder()
                .id("vehiclesTypes")
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
        for (VehiclePartType part : VehiclePartType.values()) {
            contents.set(row, column, ClickableItem.of(itemFactory.createItem(Material.BOOK,1,0,
                    part.toString()), event ->
            {
                VehicleAddonTypeMenu vehicleMenu = new VehicleAddonTypeMenu(part, this.type);
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
            VehicleTypeMenu vehiclePartTypeMenu = new VehicleTypeMenu();
            vehiclePartTypeMenu.inventory.open(player);
        }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
