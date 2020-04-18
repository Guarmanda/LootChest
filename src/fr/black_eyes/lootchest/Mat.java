package fr.black_eyes.lootchest;


import org.bukkit.Bukkit;
import org.bukkit.Material;


public final class Mat {
	public static Material  EMERALD_BLOCK, STICK, GOLD_NUGGET, GOLD_INGOT, GOLD_BLOCK, TOTEM_OF_UNDYING, CHEST, CLOCK, DIAMOND, ENDER_CHEST, ENDER_EYE, SIGN, TNT, FIREWORK, PRISMARINE, MYCELIUM, IRON_SWORD, DIAMOND_SWORD, FURNACE, ENCHANTED_BOOK, NOTE_BLOCK, END_PORTAL_FRAME, ENCHANTING_TABLE, BLAZE_POWDER, LAVA_BUCKET, STONE, WATER_BUCKET, QUARTZ, SNOW_BALL, IRON_SHOVEL, SLIME_BALL, ROSE_RED, REDSTONE_BLOCK, BARRIER, EMERALD, REDSTONE;
	public static void init_materials(){
		
		TNT = Material.TNT;
		IRON_SWORD = Material.IRON_SWORD;
		DIAMOND_SWORD = Material.DIAMOND_SWORD;
		FURNACE = Material.FURNACE;
		ENCHANTED_BOOK = Material.ENCHANTED_BOOK;
		NOTE_BLOCK = Material.NOTE_BLOCK;
		BLAZE_POWDER = Material.BLAZE_POWDER;
		LAVA_BUCKET = Material.LAVA_BUCKET;
		WATER_BUCKET = Material.WATER_BUCKET;
		STONE = Material.STONE;
		QUARTZ = Material.QUARTZ;
		SLIME_BALL = Material.SLIME_BALL;
		REDSTONE_BLOCK = Material.REDSTONE_BLOCK;

		EMERALD = Material.EMERALD;
		REDSTONE = Material.REDSTONE;
		CHEST = Material.CHEST;
		STICK =Material.STICK;
		GOLD_NUGGET = Material.GOLD_NUGGET;
		GOLD_INGOT = Material.GOLD_INGOT;
		GOLD_BLOCK = Material.GOLD_BLOCK;
		DIAMOND = Material.DIAMOND;
		EMERALD_BLOCK = Material.EMERALD_BLOCK;
		ENDER_CHEST= Material.ENDER_CHEST;
		
		if(!org.bukkit.Bukkit.getVersion().contains("1.7")){
			BARRIER = Material.valueOf("BARRIER");
			PRISMARINE = Material.valueOf("PRISMARINE_CRYSTALS");
		}
		else {
			BARRIER = Material.valueOf("WOOL");
			PRISMARINE = Material.valueOf("ENDER_PEARL");
		}
		if(Bukkit.getVersion().contains("1.7") ||Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10")|| Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
			SIGN = Material.valueOf("SIGN");
			FIREWORK = Material.valueOf("FIREWORK");
			MYCELIUM = Material.valueOf("MYCEL");
			END_PORTAL_FRAME = Material.valueOf("ENDER_PORTAL_FRAME");
			ENCHANTING_TABLE = Material.valueOf("ENCHANTMENT_TABLE");
			SNOW_BALL = Material.valueOf("SNOW_BALL");
			IRON_SHOVEL = Material.valueOf("IRON_SPADE");
			ROSE_RED = Material.valueOf("RED_ROSE");
			CLOCK = Material.valueOf("WATCH");
			ENDER_EYE = Material.valueOf("EYE_OF_ENDER");
			TOTEM_OF_UNDYING = Material.valueOf("STONE");
		}
		if(Bukkit.getVersion().contains("1.13") || Bukkit.getVersion().contains("1.14") || Bukkit.getVersion().contains("1.15")) {
			if(Bukkit.getVersion().contains("1.14") || Bukkit.getVersion().contains("1.15")){
				SIGN = Material.valueOf("OAK_SIGN");
			}
			else {
				SIGN = Material.valueOf("SIGN");
			}
			FIREWORK = Material.valueOf("FIREWORK_ROCKET");
			MYCELIUM = Material.valueOf("MYCELIUM");
			END_PORTAL_FRAME = Material.valueOf("END_PORTAL_FRAME");
			ENCHANTING_TABLE = Material.valueOf("ENCHANTING_TABLE");
			SNOW_BALL = Material.valueOf("SNOWBALL");
			IRON_SHOVEL = Material.valueOf("IRON_SHOVEL");
			ROSE_RED = Material.valueOf("RED_TULIP");
			CLOCK = Material.valueOf("CLOCK");
			ENDER_EYE = Material.valueOf("ENDER_EYE");
			TOTEM_OF_UNDYING = Material.valueOf("TOTEM_OF_UNDYING");
		}

	}
}
