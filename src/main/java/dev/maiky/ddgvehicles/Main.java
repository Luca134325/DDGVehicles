package dev.maiky.ddgvehicles;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.mongodb.MongoClient;
import dev.maiky.ddgvehicles.classes.commands.CommandManager;
import dev.maiky.ddgvehicles.classes.vehicles.Vehicle;
import dev.maiky.ddgvehicles.classes.vehicles.VehicleClassManager;
import dev.maiky.ddgvehicles.classes.vehicles.VehicleManager;
import dev.maiky.ddgvehicles.inventories.edit.VehicleEditGUI;
import dev.maiky.ddgvehicles.inventories.player.events.VehicleTrunkPlayerInventoryListener;
import dev.maiky.ddgvehicles.listeners.*;
import dev.maiky.ddgvehicles.listeners.packet.VehicleSteerListener;
import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public final class Main extends JavaPlugin {

	@Getter
	@Setter
	private static List<String> driven = new ArrayList<>();

	@Getter
	@Setter
	private static HashMap<String, Vehicle> aliveVehicles = new HashMap<>();

	@Getter
	@Setter
	private static VehicleClassManager vehicleClassManager;

	@Getter
	@Setter
	private static HashMap<Player, BukkitRunnable> bossbarMap = new HashMap<>();

	@Getter
	@Setter
	private static Main instance;

	@Getter
	@Setter
	private CommandManager commandManager;

	@Getter
	@Setter
	private VehicleManager vehicleManager;

	@Override
	public void onEnable() {
		// Main
		setInstance(this);

		// Config
		getConfig().options().copyDefaults(true);
		saveConfig();

		// Vehicle Manager
		CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		setVehicleManager(new VehicleManager(pojoCodecRegistry));

		// Vehicle Class Manager
		setVehicleClassManager(new VehicleClassManager());
		try {
			getVehicleClassManager().loadAll();
		} catch (FileNotFoundException e) {
			Bukkit.getLogger().warning("! - Zorg ervoor dat het mapje '.vehicles' te vinden is in de root folder");
			Bukkit.getPluginManager().disablePlugin(this);
		}

		// Protocol
		ProtocolLibrary.getProtocolManager().addPacketListener(new VehicleSteerListener(Main.getInstance(),
				ListenerPriority.HIGHEST, new PacketType[]{PacketType.Play.Client.STEER_VEHICLE}));

		// Bukkit events
		Bukkit.getPluginManager().registerEvents(new VehicleEditGUI.VehicleEditGUIListener(), this);
		Bukkit.getPluginManager().registerEvents(new VehiclePlaceListener(), this);
		Bukkit.getPluginManager().registerEvents(new VehiclePickupListener(), this);
		Bukkit.getPluginManager().registerEvents(new VehicleEnterListener(), this);
		Bukkit.getPluginManager().registerEvents(new VehicleTrunkPlayerInventoryListener(), this);
		Bukkit.getPluginManager().registerEvents(new VehicleEditStickListener(), this);
		Bukkit.getPluginManager().registerEvents(new VehicleDismountListener(), this);
		Bukkit.getPluginManager().registerEvents(new UserDisconnectListener(), this);
		Bukkit.getPluginManager().registerEvents(new VehicleFuelListener(), this);

		// Commands
		setCommandManager(new CommandManager());
		getCommandManager().registerAllCommands();
	}

	public static void notifyStaff(String msg) {
		Bukkit.getOnlinePlayers().forEach(p -> {
			if (p.hasPermission("ddgvehicles.staffnotify")) {
				p.sendMessage("§4§l[STAFF - DDGVEHICLES]: §c" + msg);
			}
		});
	}

}
