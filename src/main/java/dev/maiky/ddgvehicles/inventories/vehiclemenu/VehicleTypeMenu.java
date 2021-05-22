package dev.maiky.ddgvehicles.inventories.vehiclemenu;

import dev.maiky.ddgvehicles.Main;
import dev.maiky.ddgvehicles.classes.vehicles.UnindentifiedVehicleObject;
import dev.maiky.ddgvehicles.classes.vehicles.VehicleClassManager;
import dev.maiky.ddgvehicles.classes.vehicles.VehiclePart;
import dev.maiky.ddgvehicles.classes.vehicles.VehiclePartType;
import dev.maiky.ddgvehicles.utils.ItemFactory;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;

public class VehicleTypeMenu implements InventoryProvider {

    public final SmartInventory inventory;

    public VehicleTypeMenu() {
        this.inventory = SmartInventory.builder()
                .id("vehiclesTypes")
                .provider(this)
                .size(6, 9)
                .title("§3Vehicles §8▶ §7(§b" + Main.getVehicleClassManager().getAllModels().size() + "§7)")
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        ItemFactory itemFactory = new ItemFactory();
        
        int column = 0;
        int row = 0;
        for (String s : Main.getVehicleClassManager().getVehicleDatabase().keySet()) {

            HashMap<VehiclePart, List<UnindentifiedVehicleObject>> data = Main.getVehicleClassManager().getVehicleDatabase().get(s);
            UnindentifiedVehicleObject uvoObject = null;
            for (VehiclePart part2 : VehiclePart.values()) {
                if (!data.containsKey(part2))
                    continue;
                if (data.get(part2) == null)
                    continue;

                for (UnindentifiedVehicleObject unindentifiedVehicleObject : data.get(part2)) {
                    if (!unindentifiedVehicleObject.getPartType().equals(VehiclePartType.VEHICLES)) continue;
                    uvoObject = unindentifiedVehicleObject;
                    break;
                }
            }

            if (uvoObject == null)continue;

            Material material = uvoObject.getMaterial();
            double damage = uvoObject.getDamage();
            ItemStack itemStack = new ItemStack(material, 1, (short)damage);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName("§6" + VehicleMenu.format(s));
            meta.setUnbreakable(true);
            itemStack.setItemMeta(meta);

            contents.set(row, column, ClickableItem.of(itemStack, event ->
            {
                VehiclePartTypeMenu vehicleMenu = new VehiclePartTypeMenu(s);
                vehicleMenu.inventory.open(player);
            }));

            column++;
            if (column == 9) {
                column = 0;
                row++;
            }
        }
        
        contents.fillRow(4, ClickableItem.empty(itemFactory.createItem(Material.STAINED_GLASS_PANE,1,7," ")));
        contents.set(5, 4, ClickableItem.of(itemFactory.createItem(Material.BARRIER,1,0,"&cSluiten"), event -> player.closeInventory()));

        // Search Icon
        ItemStack stack = itemFactory.createItem(Material.COMPASS,1,0,"&bZoeken in database");
        contents.set(5, 6, ClickableItem.of(stack, clickEvent ->
        {
            boolean[] done = {false};

            player.closeInventory();
            player.sendMessage("§3Typ in de chat je search query!");

            Bukkit.getPluginManager().registerEvents(new Listener() {
                @EventHandler(priority = EventPriority.LOWEST)
                public void onChat(AsyncPlayerChatEvent event) {
                    if (done[0]) return;
                    if (event.getPlayer() != player) return;

                    String message = event.getMessage();
                    event.setCancelled(true);

                    if (message.equals("annuleer")) {
                        done[0] = true;
                        inventory.open(player);
                        return;
                    }

                    done[0] = true;
                    long start = System.currentTimeMillis();
                    player.sendMessage("§3Laden...");
                    VehicleClassManager classManager = Main.getVehicleClassManager();
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            HashMap<VehiclePart, UnindentifiedVehicleObject> list = classManager.search(message);
                            VehicleSearchResultMenu resultMenu =  new VehicleSearchResultMenu(message, list);
                            resultMenu.getInventory().open(player);
                            long end = System.currentTimeMillis();
                            player.sendMessage("§3Geladen! Het duurde §b" + ((end - start)) + "ms §3om de vehicle onderdelen te vinden in de database.");
                        }
                    }.runTaskAsynchronously(Main.getInstance());
                }
            }, Main.getInstance());
        }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
