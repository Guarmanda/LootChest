package fr.black_eyes.lootchest;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author Black_Eyes
 * Singleton-like class to initialize and get the config options
 *
 */
public class Config {



public int default_reset_time,
default_item_chance,
Cooldown_Before_Plugin_Start,
Minimum_Number_Of_Players_For_Natural_Spawning,
FALL_Height,
PART_number,
Radius_Without_Monsters_For_Opening_Chest,
PART_respawn_ticks,
defaultRespawnProtection,
default_maxFilledSlots;

public Boolean saveDataFileDuringReload, 
use_players_locations_for_randomspawn,
save_Chest_Locations_At_Every_Spawn,
UseHologram,
RemoveEmptyChests,
CheckForUpdates,
ConsoleMessages,
Prevent_Chest_Spawn_In_Protected_Places,
RemoveChestAfterFirstOpenning,
PreventHopperPlacingUnderLootChest,
Protect_From_Explosions,
FALL_Let_Block_Above_Chest_After_Fall,
FALL_Enabled,
FALL_Enable_Fireworks,
PART_enable,
NOTE_bungee_broadcast,
NOTE_per_world_message,
NOTE_message_on_chest_take,
NOTE_natural_e,
NOTE_command_e,
NOTE_allcmd_e,
WorldBorder_Check_For_Spawn,
Destroy_Naturally_Instead_Of_Removing_Chest,
TIMER_Show_Timer,
allow_spawning_on_water;

public String FALL_Optionnal_Color_If_Block_Is_Wool,
PART_default_particle,
FALL_Block,
NOTE_natural_msg,
NOTE_command_msg,
NOTE_allcmd_msg,
TIMER_H_Sep,
TIMER_M_Sep,
TIMER_S_Sep,
TIMER_Format;

public double FALL_Speed,
PART_radius,
Hologram_distance_to_chest,
PART_speed;


/**
 * 
 */
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
 * @return the only instance of the config options. Usefull for anything but reloading the plugin.
 */
public static Config getInstance()
{
	return instance;
}


/**
 * Initialize all the config options by reading the YAML config file 
 * @param config The config file to get the options from, and initialize them
 */
public Config(FileConfiguration config) {
	defaultRespawnProtection = config.getInt("respawn_protection_time_in_second_by_default");
	default_reset_time = config.getInt("default_reset_time");
	default_item_chance = config.getInt("default_item_chance");
	Cooldown_Before_Plugin_Start = config.getInt("Cooldown_Before_Plugin_Start");
	Minimum_Number_Of_Players_For_Natural_Spawning = config.getInt("Minimum_Number_Of_Players_For_Natural_Spawning");
	FALL_Height = config.getInt("Fall_Effect.Height");
	PART_number = config.getInt("Particles.number");
	PART_respawn_ticks = config.getInt("Particles.respawn_ticks");
	Radius_Without_Monsters_For_Opening_Chest= config.getInt("Radius_Without_Monsters_For_Opening_Chest");
	default_maxFilledSlots = config.getInt("Max_Filled_Slots_By_Default");

	saveDataFileDuringReload = config.getBoolean("SaveDataFileDuringReload");
	allow_spawning_on_water = config.getBoolean("allow_spawning_on_water");
	use_players_locations_for_randomspawn = config.getBoolean("use_players_locations_for_randomspawn");
	save_Chest_Locations_At_Every_Spawn = config.getBoolean("save_Chest_Locations_At_Every_Spawn");
	UseHologram = config.getBoolean("UseHologram") && !Bukkit.getVersion().contains("1.7");
	RemoveEmptyChests = config.getBoolean("RemoveEmptyChests");
	CheckForUpdates = config.getBoolean("CheckForUpdates");
	ConsoleMessages = config.getBoolean("ConsoleMessages");
	Prevent_Chest_Spawn_In_Protected_Places = config.getBoolean("Prevent_Chest_Spawn_In_Protected_Places");
	RemoveChestAfterFirstOpenning = config.getBoolean("RemoveChestAfterFirstOpenning");
	PreventHopperPlacingUnderLootChest = config.getBoolean("PreventHopperPlacingUnderLootChest");
	FALL_Let_Block_Above_Chest_After_Fall = config.getBoolean("Fall_Effect.Let_Block_Above_Chest_After_Fall");
	FALL_Enabled = config.getBoolean("Fall_Effect.Enabled");
	FALL_Enable_Fireworks = config.getBoolean("Fall_Effect.Enable_Fireworks");
	PART_enable = config.getBoolean("Particles.enable");
	NOTE_bungee_broadcast = config.getBoolean("respawn_notify.bungee_broadcast");
	NOTE_per_world_message = config.getBoolean("respawn_notify.per_world_message");
	NOTE_message_on_chest_take = config.getBoolean("respawn_notify.message_on_chest_take");
	NOTE_natural_e = config.getBoolean("respawn_notify.natural_respawn.enabled");
	NOTE_command_e = config.getBoolean("respawn_notify.respawn_with_command.enabled");
	NOTE_allcmd_e = config.getBoolean("respawn_notify.respawn_all_with_command.enabled");
	Protect_From_Explosions = config.getBoolean("Protect_From_Explosions");
	WorldBorder_Check_For_Spawn = config.getBoolean("WorldBorder_Check_For_Spawn") && !Bukkit.getVersion().contains("1.7");
	Destroy_Naturally_Instead_Of_Removing_Chest = config.getBoolean("Destroy_Naturally_Instead_Of_Removing_Chest");
	TIMER_Show_Timer = config.getBoolean("Timer_on_hologram.Show_Timer_On_Hologram");

	
	FALL_Optionnal_Color_If_Block_Is_Wool = config.getString("Fall_Effect.Optionnal_Color_If_Block_Is_Wool");
	PART_default_particle = config.getString("Particles.default_particle");
	FALL_Block = config.getString("Fall_Effect.Block");
	NOTE_natural_msg = config.getString("respawn_notify.natural_respawn.message");
	NOTE_command_msg = config.getString("respawn_notify.respawn_with_command.message");
	NOTE_allcmd_msg = config.getString("respawn_notify.respawn_all_with_command.message");
	TIMER_H_Sep = config.getString("Timer_on_hologram.Hours_Separator");
	TIMER_M_Sep = config.getString("Timer_on_hologram.Minutes_Separator");
	TIMER_S_Sep = config.getString("Timer_on_hologram.Seconds_Separator");
	TIMER_Format = config.getString("Timer_on_hologram.Format");

	FALL_Speed = config.getDouble("Fall_Effect.Speed");
	PART_radius= config.getDouble("Particles.radius");
	PART_speed= config.getDouble("Particles.speed");
	Hologram_distance_to_chest = config.getDouble("Hologram_distance_to_chest");
	

}




	
}
