package fr.black_eyes.lootchest;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;


public final class Mat {
	public static Material INK_SACK, SOUL_FIRE, NAUTILUS_SHELL, SPAWNER, GLOW_INK_SAC, SPORE_BLOSSOM, END_ROD, CRYING_OBSIDIAN, HONEY_BLOCK, LAVA, DRAGONS_BREATH, DOLPHIN_SPAWN_EGG, CRIMSON_FUNGUS, COMPOSTER, CAMPFIRE, BUBBLE_COLUMN, SOUL_SAND, EMERALD_BLOCK, STICK, GOLD_NUGGET, GOLD_INGOT, GOLD_BLOCK, TOTEM_OF_UNDYING, CHEST, CLOCK, DIAMOND, ENDER_CHEST, ENDER_EYE, SIGN, TNT, FIREWORK, PRISMARINE, MYCELIUM, IRON_SWORD, DIAMOND_SWORD, FURNACE, ENCHANTED_BOOK, NOTE_BLOCK, END_PORTAL_FRAME, ENCHANTING_TABLE, BLAZE_POWDER, LAVA_BUCKET, STONE, WATER_BUCKET, QUARTZ, SNOW_BALL, IRON_SHOVEL, SLIME_BALL, ROSE_RED, REDSTONE_BLOCK, BARRIER, EMERALD, REDSTONE;
	public static Material TRAPPED_CHEST, BARREL;
	public static void init_materials(){ 
		SOUL_FIRE = Material.FIRE;
		NAUTILUS_SHELL = Material.STONE;
		GLOW_INK_SAC = Material.GLOWSTONE_DUST;
		SPORE_BLOSSOM = Material.RED_MUSHROOM;
		END_ROD = Material.BLAZE_ROD;
		CRYING_OBSIDIAN = Material.STONE;
		HONEY_BLOCK = Material.STONE;
		DRAGONS_BREATH = Material.DRAGON_EGG;
		DOLPHIN_SPAWN_EGG = Material.STONE;
		CRIMSON_FUNGUS = Material.STONE;
		COMPOSTER = Material.STONE;
		CAMPFIRE= Material.STONE;
		BUBBLE_COLUMN = Material.STONE;
		SOUL_SAND = Material.SOUL_SAND;
		TRAPPED_CHEST = Material.TRAPPED_CHEST;
		BARREL = Material.CHEST;	
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
		
		if(!Bukkit.getVersion().contains("1.7") ){
			if(!Bukkit.getVersion().contains("1.8") ) {
				END_ROD = Material.valueOf("END_ROD");
			}
			
			BARRIER = Material.valueOf("BARRIER");
			PRISMARINE = Material.valueOf("PRISMARINE_CRYSTALS");
		}
		else {
			
			BARRIER = Material.valueOf("WOOL");
			PRISMARINE = Material.valueOf("ENDER_PEARL");
		}
		if(Bukkit.getVersion().contains("1.7") ||Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10")|| Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
			if(!(Bukkit.getVersion().contains("1.7") ||Bukkit.getVersion().contains("1.8"))) DRAGONS_BREATH = Material.valueOf("DRAGONS_BREATH"); 
			SPAWNER = Material.valueOf("MOB_SPAWNER"); 
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
			INK_SACK = Material.valueOf("INK_SACK");
		}
		else {
		//if(Bukkit.getVersion().contains("1.13") || Bukkit.getVersion().contains("1.14") || Bukkit.getVersion().contains("1.15") || Bukkit.getVersion().contains("1.16")|| Bukkit.getVersion().contains("1.17")) {
			
			if(!Bukkit.getVersion().contains("1.13")){
				if(!Bukkit.getVersion().contains("1.14")) {
					if(!Bukkit.getVersion().contains("1.15")) {
						if(!Bukkit.getVersion().contains("1.16")) {
							SPORE_BLOSSOM = Material.valueOf("SPORE_BLOSSOM");
							GLOW_INK_SAC = Material.valueOf("GLOW_INK_SAC");
						}
						SOUL_FIRE = Material.valueOf("SOUL_FIRE");
						CRIMSON_FUNGUS = Material.valueOf("CRIMSON_FUNGUS");
						CRYING_OBSIDIAN = Material.valueOf("CRYING_OBSIDIAN");
					}
					HONEY_BLOCK = Material.valueOf("HONEY_BLOCK");
				}
				CAMPFIRE = Material.valueOf("CAMPFIRE");
				COMPOSTER = Material.valueOf("COMPOSTER");
				SIGN = Material.valueOf("OAK_SIGN");
				BARREL = Material.valueOf("BARREL");
			}
			else {
				SIGN = Material.valueOf("SIGN");
			}
			SPAWNER = Material.valueOf("SPAWNER");
			DRAGONS_BREATH = Material.valueOf("DRAGON_BREATH");
			INK_SACK = Material.valueOf("INK_SAC");
			NAUTILUS_SHELL = Material.valueOf("NAUTILUS_SHELL");
			DOLPHIN_SPAWN_EGG = Material.valueOf("DOLPHIN_SPAWN_EGG");
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
			BUBBLE_COLUMN = Material.valueOf("BUBBLE_COLUMN");
		}
		

	}
	
	public static boolean isALootChestBlock(Block block) {
		return (block.getType().equals(Mat.CHEST) || block.getType().equals(Mat.TRAPPED_CHEST) || block.getType().equals(Mat.BARREL));
	}
}
