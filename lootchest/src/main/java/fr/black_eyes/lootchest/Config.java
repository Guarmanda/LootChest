package fr.black_eyes.lootchest;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author Black_Eyes
 * Singleton-like class to initialize and get the config options
 *
 */
public class Config {



public final int defaultResetTime,
		defaultItemChance,
		cooldownBeforePluginStart,
		minimumNumberOfPlayersForNaturalSpawning,
		fallHeight,
		partNumber,
		radiusWithoutMonstersForOpeningChest,
		partRespawnTicks,
		defaultRespawnProtection,
		defaultMaxFilledSlots,
		minimumNumberOfPlayersForCommandSpawning,
		maxHeightForRandomSpawn,
		minHeightForRandomSpawn;

public final boolean saveDataFileDuringReload,
		usePlayersLocationsForRandomSpawn,
		saveChestLocationsAtEverySpawn,
		removeEmptyChests,
		checkForUpdates,
		consoleMessages,
		preventChestSpawnInProtectedPlaces,
		removeChestAfterFirstOpening,
		preventHopperPlacingUnderLootChest,
		protectFromExplosions,
		fallEnabled,
		fallEnableFireworks,
		partEnable,
		noteBungeeBroadcast,
		notePerWorldMessage,
		noteMessageOnChestTake,
		noteNaturalE,
		noteCommandE,
		noteAllcmdE,
		destroyNaturallyInsteadOfRemovingChest,
		timerShowTimer,
		allowSpawningOnWater,
		noteAllcmdWorldE,
		lootin,
		spawnOnNonSolidBlocks;
public boolean worldborderCheckForSpawn, usehologram;


public final String partDefaultParticle,
		noteNaturalMsg,
		noteCommandMsg,
		noteAllcmdMsg,
		noteAllcmdMsgWorld,
		timerHSep,
		timerMSep,
		timerSSep,
		timerFormat;
public String fallBlock;

public final double FALL_Speed,
PART_radius,
Hologram_distance_to_chest,
PART_speed;


/**
 *
 * @return the only instance of the config options. Usefull for anything but reloading the plugin.
 */
@Getter
private static Config instance = null;


/**
 * Creates a new instance replacing the old one, and re-initialize the config options
 * Usefull for reloading the plugin
 * @param config The file to get the options from
 * @return a new instance of the config options
 */
public static Config getInstance(FileConfiguration config)
{
    instance = new Config(config);
    return instance;
}


    /**
 * Initialize all the config options by reading the YAML config file 
 * @param config The config file to get the options from, and initialize them
 */
public Config(FileConfiguration config) {
	defaultRespawnProtection = config.getInt("respawn_protection_time_in_second_by_default");
	defaultResetTime = config.getInt("default_reset_time");
	defaultItemChance = config.getInt("default_item_chance");
	cooldownBeforePluginStart = config.getInt("Cooldown_Before_Plugin_Start");
	minimumNumberOfPlayersForNaturalSpawning = config.getInt("Minimum_Number_Of_Players_For_Natural_Spawning");
	fallHeight = config.getInt("Fall_Effect.Height");
	partNumber = config.getInt("Particles.number");
	partRespawnTicks = config.getInt("Particles.respawn_ticks");
	radiusWithoutMonstersForOpeningChest = config.getInt("Radius_Without_Monsters_For_Opening_Chest");
	defaultMaxFilledSlots = config.getInt("Max_Filled_Slots_By_Default");
	minimumNumberOfPlayersForCommandSpawning = config.getInt("Minimum_Number_Of_Players_For_Command_Spawning");
	maxHeightForRandomSpawn = config.getInt("Max_Height_For_Random_Spawn");
	minHeightForRandomSpawn = config.getInt("Minimum_Height_For_Random_Spawn");

	saveDataFileDuringReload = config.getBoolean("SaveDataFileDuringReload");
	allowSpawningOnWater = config.getBoolean("allow_spawning_on_water");
	usePlayersLocationsForRandomSpawn = config.getBoolean("use_players_locations_for_randomspawn");
	saveChestLocationsAtEverySpawn = config.getBoolean("save_Chest_Locations_At_Every_Spawn");
	usehologram = config.getBoolean("UseHologram") && !Bukkit.getVersion().contains("1.7");
	removeEmptyChests = config.getBoolean("RemoveEmptyChests");
	checkForUpdates = config.getBoolean("CheckForUpdates");
	consoleMessages = config.getBoolean("ConsoleMessages");
	preventChestSpawnInProtectedPlaces = config.getBoolean("Prevent_Chest_Spawn_In_Protected_Places");
	removeChestAfterFirstOpening = config.getBoolean("RemoveChestAfterFirstOpening");
	preventHopperPlacingUnderLootChest = config.getBoolean("PreventHopperPlacingUnderLootChest");
	fallEnabled = config.getBoolean("Fall_Effect.Enabled");
	fallEnableFireworks = config.getBoolean("Fall_Effect.Enable_Fireworks");
	partEnable = config.getBoolean("Particles.enable");
	noteBungeeBroadcast = config.getBoolean("respawn_notify.bungee_broadcast");
	notePerWorldMessage = config.getBoolean("respawn_notify.per_world_message");
	noteMessageOnChestTake = config.getBoolean("respawn_notify.message_on_chest_take");
	noteNaturalE = config.getBoolean("respawn_notify.natural_respawn.enabled");
	noteCommandE = config.getBoolean("respawn_notify.respawn_with_command.enabled");
	noteAllcmdE = config.getBoolean("respawn_notify.respawn_all_with_command.enabled");
	noteAllcmdWorldE = config.getBoolean("respawn_notify.respawn_all_with_command_in_world.enabled");
	protectFromExplosions = config.getBoolean("Protect_From_Explosions");
	worldborderCheckForSpawn = config.getBoolean("WorldBorder_Check_For_Spawn") && !Bukkit.getVersion().contains("1.7");
	destroyNaturallyInsteadOfRemovingChest = config.getBoolean("Destroy_Naturally_Instead_Of_Removing_Chest");
	timerShowTimer = config.getBoolean("Timer_on_hologram.Show_Timer_On_Hologram");
	lootin = config.getBoolean("EnableLootin");
	spawnOnNonSolidBlocks = config.getBoolean("spawn_on_non_solid_blocks");
	
	partDefaultParticle = config.getString("Particles.default_particle");
	fallBlock = config.getString("Fall_Effect.Block");
	noteNaturalMsg = config.getString("respawn_notify.natural_respawn.message");
	noteCommandMsg = config.getString("respawn_notify.respawn_with_command.message");
	noteAllcmdMsg = config.getString("respawn_notify.respawn_all_with_command.message");
	noteAllcmdMsgWorld = config.getString("respawn_notify.respawn_all_with_command_in_world.message");
	timerHSep = config.getString("Timer_on_hologram.Hours_Separator");
	timerMSep = config.getString("Timer_on_hologram.Minutes_Separator");
	timerSSep = config.getString("Timer_on_hologram.Seconds_Separator");
	timerFormat = config.getString("Timer_on_hologram.Format");

	FALL_Speed = config.getDouble("Fall_Effect.Speed");
	PART_radius= config.getDouble("Particles.radius");
	PART_speed= config.getDouble("Particles.speed");
	Hologram_distance_to_chest = config.getDouble("Hologram_distance_to_chest");
	

}




	
}
