package fr.black_eyes.lootchest;


import org.bukkit.Bukkit;
import org.bukkit.Material;


public final class Mat {
	public static Material STICK, GOLD_NUGGET, GOLD_INGOT, GOLD_BLOCK, TOTEM_OF_UNDYING, CHEST, CLOCK, DIAMOND, ENDER_CHEST, ENDER_EYE, SIGN, TNT, FIREWORK, PRISMARINE, MYCELIUM, IRON_SWORD, DIAMOND_SWORD, FURNACE, ENCHANTED_BOOK, NOTE_BLOCK, END_PORTAL_FRAME, ENCHANTING_TABLE, BLAZE_POWDER, LAVA_BUCKET, STONE, WATER_BUCKET, QUARTZ, SNOW_BALL, IRON_SHOVEL, SLIME_BALL, ROSE_RED, REDSTONE_BLOCK, BARRIER, EMERALD, REDSTONE;
	public static void init_materials(){
		PRISMARINE = Material.valueOf("PRISMARINE_CRYSTALS");
		TNT = Material.valueOf("TNT");
		IRON_SWORD = Material.valueOf("IRON_SWORD");
		DIAMOND_SWORD = Material.valueOf("DIAMOND_SWORD");
		FURNACE = Material.valueOf("FURNACE");
		ENCHANTED_BOOK = Material.valueOf("ENCHANTED_BOOK");
		NOTE_BLOCK = Material.valueOf("NOTE_BLOCK");
		BLAZE_POWDER = Material.valueOf("BLAZE_POWDER");
		LAVA_BUCKET = Material.valueOf("LAVA_BUCKET");
		WATER_BUCKET = Material.valueOf("WATER_BUCKET");
		STONE = Material.valueOf("STONE");
		QUARTZ = Material.valueOf("QUARTZ");
		SLIME_BALL = Material.valueOf("SLIME_BALL");
		REDSTONE_BLOCK = Material.valueOf("REDSTONE_BLOCK");
		BARRIER = Material.valueOf("BARRIER");
		EMERALD = Material.valueOf("EMERALD");
		REDSTONE = Material.valueOf("REDSTONE");
		CHEST = Material.valueOf("CHEST");
		STICK =Material.valueOf("STICK");
		GOLD_NUGGET = Material.valueOf("GOLD_NUGGET");
		GOLD_INGOT = Material.valueOf("GOLD_INGOT");
		GOLD_BLOCK = Material.valueOf("GOLD_BLOCK");
		DIAMOND = Material.valueOf("DIAMOND");
		ENDER_CHEST= Material.valueOf("ENDER_CHEST");
		
		
		if(Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.12") || Bukkit.getVersion().contains("1.11")) {
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
		if(Bukkit.getVersion().contains("1.13")) {
			SIGN = Material.valueOf("SIGN");
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
		if(Bukkit.getVersion().contains("1.14")) {
			SIGN = Material.valueOf("OAK_SIGN");
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
