package fr.black_eyes.lootchest;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {



public int default_reset_time,
default_item_chance,
Cooldown_Before_Plugin_Start,
Minimum_Number_Of_Players_For_Natural_Spawning,
Hologram_distance_to_chest,
FALL_Height,
PART_number,
PART_respawn_ticks;

public Boolean use_players_locations_for_randomspawn,
save_Chest_Locations_At_Every_Spawn,
UseHologram,
RemoveEmptyChests,
CheckForUpdates,
ConsoleMessages,
Prevent_Chest_Spawn_In_Protected_Places,
RemoveChestAfterFirstOpenning,
Show_Timer_On_Hologram,
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
NOTE_allcmd_e;

public String FALL_Optionnal_Color_If_Block_Is_Wool,
PART_default_particle,
FALL_Block,
NOTE_natural_msg,
NOTE_command_msg,
NOTE_allcmd_msg;

public double FALL_Speed,
PART_radius,
PART_speed;


private static final Config instance = new Config();
public static Config getInstance() {
    return instance;
}

public Config() {
	FileConfiguration config = Main.getInstance().getConfig();
	default_reset_time = config.getInt("default_reset_time");
	default_item_chance = config.getInt("default_item_chance");
	Cooldown_Before_Plugin_Start = config.getInt("Cooldown_Before_Plugin_Start");
	Minimum_Number_Of_Players_For_Natural_Spawning = config.getInt("Minimum_Number_Of_Players_For_Natural_Spawning");
	Hologram_distance_to_chest = config.getInt("Hologram_distance_to_chest");
	FALL_Height = config.getInt("Fall_Effect.Height");
	PART_number = config.getInt("Particles.number");
	PART_respawn_ticks = config.getInt("Particles.respawn_ticks");

	use_players_locations_for_randomspawn = config.getBoolean("use_players_locations_for_randomspawn");
	save_Chest_Locations_At_Every_Spawn = config.getBoolean("save_Chest_Locations_At_Every_Spawn");
	UseHologram = config.getBoolean("UseHologram");
	RemoveEmptyChests = config.getBoolean("RemoveEmptyChests");
	CheckForUpdates = config.getBoolean("CheckForUpdates");
	ConsoleMessages = config.getBoolean("ConsoleMessages");
	Prevent_Chest_Spawn_In_Protected_Places = config.getBoolean("Prevent_Chest_Spawn_In_Protected_Places");
	RemoveChestAfterFirstOpenning = config.getBoolean("RemoveChestAfterFirstOpenning");
	Show_Timer_On_Hologram = config.getBoolean("Show_Timer_On_Hologram");
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

	FALL_Optionnal_Color_If_Block_Is_Wool = config.getString("Fall_Effect.Optionnal_Color_If_Block_Is_Wool");
	PART_default_particle = config.getString("Particles.default_particle");
	FALL_Block = config.getString("Fall_Effect.Block");
	NOTE_natural_msg = config.getString("respawn_notify.natural_respawn.message");
	NOTE_command_msg = config.getString("respawn_notify.respawn_with_command.message");
	NOTE_allcmd_msg = config.getString("respawn_notify.respawn_all_with_command.message");

	FALL_Speed = config.getDouble("Fall_Effect.Speed");
	PART_radius= config.getDouble("Particles.radius");
	PART_speed= config.getDouble("Particles.speed");
	

}




	
}
