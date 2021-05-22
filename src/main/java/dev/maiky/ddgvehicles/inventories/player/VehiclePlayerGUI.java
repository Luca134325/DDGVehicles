package dev.maiky.ddgvehicles.inventories.player;

import dev.maiky.ddgvehicles.Main;
import dev.maiky.ddgvehicles.classes.vehicles.*;
import dev.maiky.ddgvehicles.listeners.VehicleEnterListener;
import dev.maiky.ddgvehicles.listeners.packet.VehicleSteerListener;
import dev.maiky.ddgvehicles.utils.ItemFactory;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Door: Maiky
 * Info: DDGVehicles - 04 Apr 2021
 * Package: dev.maiky.ddgvehicles.inventories.player
 */

public class VehiclePlayerGUI implements InventoryProvider {

	private final SmartInventory inventory;
	private final String license;

	public String getLicense() {
		return license;
	}

	public SmartInventory getInventory() {
		return inventory;
	}

	public VehiclePlayerGUI(String license) {
		this.license = license;
		this.inventory = SmartInventory.builder()
				.id("vehicle_info")
				.provider(this)
				.size(3, 9)
				.title(ChatColor.translateAlternateColorCodes('&', "&3Voertuig &8- &b" + license))
				.build();
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		ItemFactory itemFactory = new ItemFactory();
		VehicleManager manager = Main.getInstance().getVehicleManager();

		String model = manager.getModel(getLicense());
		ItemStack glass = itemFactory.createItem(Material.STAINED_GLASS_PANE,1,7);
		int seatCount = VehicleSeatLink.valueOf(model).getSeats().length;

		int column = 1;
		for (int i = 0; i < seatCount; i++) {
			ItemStack itemStack = itemFactory.createItem(Material.IRON_INGOT,
					1, 0, "&aStoel #" + (i + 1),
					Arrays.asList("", (SpawnedVehicleManager.isOccupied(license, i) ? "&6&o(Bezet)" : "&aKlik om te zitten")));
			net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(itemStack);
			NBTTagCompound tagCompound = nms.getTag() == null ? new NBTTagCompound() : nms.getTag();
			tagCompound.setString("mtcustom", model.equals(VehicleSeatLink.colos.toString()) ? "motor_zadel" : "auto_stoel");
			nms.setTag(tagCompound);
			itemStack = CraftItemStack.asCraftMirror(nms);
			int finalI = i;
			contents.set(1, column, ClickableItem.of(itemStack, event ->
			{
				List<UUID> riders = manager.getRiders(license);
				if (riders == null) riders = new ArrayList<>();
				List<UUID> members = manager.getMembers(license);
				if (members == null) members = new ArrayList<>();
				UUID owner = manager.getOwner(license);

				if (finalI == 0 && !riders.contains(player.getUniqueId()) && !owner.equals(player.getUniqueId()) && !player
						.hasPermission("ddgvehicles.staff")) {
					player.sendMessage("§cJe kunt deze stoel niet gebruiken, omdat je geen rider bent van dit voertuig.");
					return;
				}

				if (!riders.contains(player.getUniqueId()) && !owner.equals(player.getUniqueId()) &&
				!members.contains(player.getUniqueId()) && !player
						.hasPermission("ddgvehicles.staff")) {
					player.sendMessage("§cJe kunt deze stoel niet gebruiken, omdat je geen member bent van dit voertuig.");
					return;
				}

				VehicleSteerListener.velocityMapForward.put(player, 0d);
				VehicleSteerListener.velocityMapUp.put(player, 0d);

				if (manager.getModel(license).equals(VehicleSeatLink.hercal.toString())) {
					VehicleEnterListener.helicopterTools(license, Objects.requireNonNull(SpawnedVehicleManager.getCar(license)).getLocation());
				}

				player.closeInventory();

				EnterResponse enterResponse = SpawnedVehicleManager.findNonOccupiedSeatAndEnter(license, player, finalI);

				if (finalI == 0) {
					if (!model.equals("fiets")) {
						final BossBar bossBar = Bukkit.createBossBar("§f" + ((int)Math.round(manager.getFuel(license))) + "% §6Fuel", BarColor.GREEN, BarStyle.SEGMENTED_10);
						bossBar.addPlayer(player);

						BukkitRunnable updater = new BukkitRunnable() {
							@Override
							public void run() {
								manager.setFuel(license, Main.getAliveVehicles().get(license).getFuel());
							}
						};
						updater.runTaskTimerAsynchronously(Main.getInstance(), 0, 100);

						BukkitRunnable fuelRunnable = new BukkitRunnable() {

							@Override
							public void run() {
								double newFuel = Main.getAliveVehicles().get(license).getFuel();
								int percentage = (int) Math.round(newFuel);
								bossBar.setTitle("§f" + percentage + "% §6Fuel");
								bossBar.setProgress(newFuel / 100.0D);
								bossBar.addPlayer(player);
							}

							@Override
							public synchronized void cancel() throws IllegalStateException {
								super.cancel();
								updater.cancel();
								bossBar.removePlayer(player);
							}
						};
						fuelRunnable.runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
						Main.getBossbarMap().put(player, fuelRunnable);
					}
				}

				if (!enterResponse.isState()) {
					player.sendMessage("§cDit voertuig is vol, deze stoel is bezet!");
					return;
				}

				player.sendMessage("§3Je " + (enterResponse.getSeatNumber() == 0 ? "bestuurt nu het voertuig van §b"
						: "zit nu in het voertuig van §b") + Bukkit.getOfflinePlayer(owner).getName() + "§3.");
			}));

			column++;
		}

		int rows = VehicleTrunkLink.valueOf(model).getRows();
		contents.set(1, 7, ClickableItem.of(itemFactory.createItem(Material.CHEST,1,0,
				"&aKofferbak", Arrays.asList("", "&7Deze beschikt over &2" + rows + " &7slots.")), event ->
		{
			if (!manager.getOwner(license).equals(player.getUniqueId()) && !player.hasPermission("ddgvehicles.trunk.override")) {
				player.sendMessage("§cJe kan niet in de kofferbak van dit voertuig, alleen de §4eigenaar §ckan dit doen.");
				return;
			}

			VehicleTrunkGUI vehicleTrunkGUI = new VehicleTrunkGUI(license, rows);
			vehicleTrunkGUI.getInventory().open(player);
		}));

		int column2 = 0, row = 0;
		for (int i = 0; i < 27; i++) {
			Optional<ClickableItem> clickableItem = contents.get(SlotPos.of(row, column2));

			if (clickableItem.isPresent()) {
				column2++;
				if (column2 == 9) {
					row++;
					column2 = 0;
				}
				continue;
			}

			contents.set(row, column2, ClickableItem.empty(glass));

			column2++;
			if (column2 == 9) {
				row++;
				column2 = 0;
			}
		}
	}

	@Override
	public void update(Player player, InventoryContents contents) {

	}
}
