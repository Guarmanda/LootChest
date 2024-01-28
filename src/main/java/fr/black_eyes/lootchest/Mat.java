package fr.black_eyes.lootchest;


import org.bukkit.Material;
import org.bukkit.block.Block;


public final class Mat {
	public static Material SCULK, SCULK_SHRIEKER, WHITE_DYE, RED_CONCRETE, VILLAGER_SPAWN_EGG, WARPED_FUNGUS, LIGHTNING_ROD, POINTED_DRIPSTONE, SOUL_LANTERN, WATER, INK_SACK, NAUTILUS_SHELL, SPAWNER, GLOW_INK_SAC, SPORE_BLOSSOM, END_ROD, CRYING_OBSIDIAN, HONEY_BLOCK, LAVA, DRAGONS_BREATH, DOLPHIN_SPAWN_EGG, CRIMSON_FUNGUS, COMPOSTER, CAMPFIRE, BUBBLE_COLUMN, SOUL_SAND, EMERALD_BLOCK, STICK, GOLD_NUGGET, GOLD_INGOT, GOLD_BLOCK, TOTEM_OF_UNDYING, CHEST, CLOCK, DIAMOND, ENDER_CHEST, ENDER_EYE, SIGN, TNT, FIREWORK, PRISMARINE, MYCELIUM, IRON_SWORD, DIAMOND_SWORD, FURNACE, ENCHANTED_BOOK, NOTE_BLOCK, END_PORTAL_FRAME, ENCHANTING_TABLE, BLAZE_POWDER, STONE, QUARTZ, SNOW_BALL, IRON_SHOVEL, SLIME_BALL, ROSE_RED, REDSTONE_BLOCK, BARRIER, EMERALD, REDSTONE;
	public static Material LEAVES, TRAPPED_CHEST, BARREL;
	public static void init_materials(){ 
		int v = Main.getVersion();
		SCULK = Material.OBSIDIAN;
		SCULK_SHRIEKER = Material.OBSIDIAN;
		RED_CONCRETE = Material.REDSTONE_BLOCK;
		WARPED_FUNGUS = Material.BROWN_MUSHROOM;
		LIGHTNING_ROD = Material.BLAZE_ROD;
		STONE = Material.STONE;
		SOUL_LANTERN = Material.JACK_O_LANTERN;
		NAUTILUS_SHELL = STONE;
		GLOW_INK_SAC = Material.GLOWSTONE_DUST;
		SPORE_BLOSSOM = Material.RED_MUSHROOM;
		END_ROD = Material.BLAZE_ROD;
		CRYING_OBSIDIAN = STONE;
		HONEY_BLOCK = STONE;
		DRAGONS_BREATH = Material.DRAGON_EGG;
		DOLPHIN_SPAWN_EGG = STONE;
		CRIMSON_FUNGUS = STONE;
		COMPOSTER = STONE;
		CAMPFIRE= Material.TORCH;
		BUBBLE_COLUMN = STONE;
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
		WATER = Material.WATER;
		QUARTZ = Material.QUARTZ;
		SLIME_BALL = Material.SLIME_BALL;
		REDSTONE_BLOCK = Material.REDSTONE_BLOCK;
		LAVA = Material.LAVA;
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
		POINTED_DRIPSTONE = STONE;
		LEAVES = STONE;
		
		
		if(v>7){
			if(v>8) {
				END_ROD = Material.valueOf("END_ROD");
			}
			WATER = Material.WATER_BUCKET;
			BARRIER = Material.valueOf("BARRIER");
			PRISMARINE = Material.valueOf("PRISMARINE_CRYSTALS");
		}
		else {
			CAMPFIRE = Material.FIRE;
			BARRIER = Material.valueOf("WOOL");
			PRISMARINE = Material.valueOf("ENDER_PEARL");
		}
		if(v>=6 && v <=12 ) {
			if(v>8) DRAGONS_BREATH = Material.valueOf("DRAGONS_BREATH"); 
			VILLAGER_SPAWN_EGG = Material.valueOf("MONSTER_EGG");
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
			WHITE_DYE = INK_SACK;
			LEAVES = Material.valueOf("LEAVES");
		}
		else {
			
			if(v>13){
				if(v>14) {
					if(v>15) {
						if(v>16) {
							SPORE_BLOSSOM = Material.valueOf("SPORE_BLOSSOM");
							GLOW_INK_SAC = Material.valueOf("GLOW_INK_SAC");
							if(v>18) {
								SCULK = Material.valueOf("SCULK");
								SCULK_SHRIEKER = Material.valueOf("SCULK_SHRIEKER");
							}
						}
						WARPED_FUNGUS = Material.valueOf("WARPED_FUNGUS");
						CRIMSON_FUNGUS = Material.valueOf("CRIMSON_FUNGUS");
						CRYING_OBSIDIAN = Material.valueOf("CRYING_OBSIDIAN");
						SOUL_LANTERN = Material.valueOf("SOUL_LANTERN");
					}
					
					HONEY_BLOCK = Material.valueOf("HONEYCOMB");
				}else {
					SOUL_LANTERN = Material.valueOf("LANTERN");
				}
				CAMPFIRE = Material.valueOf("CAMPFIRE");
				COMPOSTER = Material.valueOf("COMPOSTER");
				SIGN = Material.valueOf("OAK_SIGN");
				BARREL = Material.valueOf("BARREL");
			}
			else {
				SIGN = Material.valueOf("SIGN");
			}
			LEAVES = Material.valueOf("OAK_LEAVES");
			WHITE_DYE = Material.valueOf("GRAY_DYE");
			RED_CONCRETE= Material.valueOf("RED_CONCRETE");
			VILLAGER_SPAWN_EGG = Material.valueOf("VILLAGER_SPAWN_EGG");
			
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
			if(v >=17) {
				POINTED_DRIPSTONE = Material.valueOf("POINTED_DRIPSTONE");
				LIGHTNING_ROD = Material.valueOf("LIGHTNING_ROD");
			}
		}
		

	}
	
	public static boolean isALootChestBlock(Block block) {
		return (block.getType().equals(Mat.CHEST) || block.getType().equals(Mat.TRAPPED_CHEST) || block.getType().equals(Mat.BARREL));
	}
}
