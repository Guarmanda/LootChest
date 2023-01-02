package fr.black_eyes.lootchest.particles;


import fr.black_eyes.lootchest.Mat;
import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

/**
 * <b>Particle Library</b>
 * <p>
 * I'm @Black_Eyes , I listed 1.7 to 1.18 particles, indicating version of appearance for each particles,
 * With this, I could get all particles and check their compatibility with version X.
 * Then I searched and found a good api to spawn/summon them in any version.
 * <p>
 * 
 * @author Black_Eyes
 * @version 1.7-1.18
 */


public enum Particle {


	ASH(Mat.SOUL_SAND, 16),
	BARRIER(Mat.BARRIER, 8, ParticleProperty.DELETED_IN_1_18),
	//BLOCK_MARKER(Mat.BARRIER,18),
	BUBBLE_COLUMN_UP(Mat.WATER, 13),
	BUBBLE_POP(Material.GLASS, 13),
	CAMPFIRE_COSY_SMOKE(Mat.CAMPFIRE, 14),
	CAMPFIRE_SIGNAL_SMOKE(Mat.CAMPFIRE, 14),
	CLOUD(Mat.WHITE_DYE, -1),
	COMPOSTER(Mat.COMPOSTER, 14),
	CRIMSON_SPORE(Mat.CRIMSON_FUNGUS, 17),
	CRIT(Material.NETHER_STAR, -1),
	CRIT_MAGIC(Material.NETHER_STAR, -1),
	CURRENT_DOWN(Mat.BUBBLE_COLUMN, 13),
	DAMAGE_INDICATOR(Mat.DIAMOND_SWORD, 9),
	DOLPHIN(Mat.DOLPHIN_SPAWN_EGG, 13),
	DRAGON_BREATH(Mat.DRAGONS_BREATH, 9),
	DRIP_LAVA(Mat.LAVA, -1),
	DRIP_WATER(Mat.WATER, -1),
	DRIPPING_DRIPSTONE_LAVA(Mat.POINTED_DRIPSTONE, 17),
	DRIPPING_DRIPSTONE_WATER(Mat.POINTED_DRIPSTONE, 17),
	DRIPPING_HONEY(Mat.HONEY_BLOCK, 15),
	DRIPPING_OBSIDIAN_TEAR(Mat.CRYING_OBSIDIAN, 16),
	//DUST_COLOR_TRANSITION(Material.GLOWSTONE_DUST, 17),
	ELECTRIC_SPARK(Mat.LIGHTNING_ROD, 17),
	ENCHANTMENT_TABLE(Mat.ENCHANTING_TABLE, -1),
	END_ROD(Mat.END_ROD, 9),
	EXPLOSION_HUGE(Mat.TNT, -1),
	EXPLOSION_LARGE(Mat.TNT, -1),
	EXPLOSION_NORMAL(Mat.TNT, -1),
	FALLING_DRIPSTONE_LAVA(Mat.LAVA, 17),
	FALLING_DRIPSTONE_WATER(Mat.WATER, 17),
	//FALLING_DUST(Material.GLOWSTONE_DUST, 10),
	FALLING_HONEY(Mat.HONEY_BLOCK, 15),
	FALLING_LAVA(Mat.LAVA, 14),
	FALLING_NECTAR(Mat.HONEY_BLOCK, 15),
	FALLING_OBSIDIAN_TEAR(Mat.CRYING_OBSIDIAN, 16),
	FALLING_SPORE_BLOSSOM(Mat.SPORE_BLOSSOM, 17),
	FALLING_WATER(Mat.WATER,14),
	FIREWORKS_SPARK(Mat.FIREWORK, -1),
	FLAME(Material.FIRE, -1),
	FLASH(Material.BEACON, 14),
	FOOTSTEP(Mat.STONE, 8 ,ParticleProperty.DELETED_IN_1_13 ),
	GLOW(Material.GLOWSTONE_DUST, 17),
	GLOW_SQUID_INK(Mat.GLOW_INK_SAC, 17),
	HEART(Mat.RED_CONCRETE, -1),
	ITEM_TAKE(Mat.STONE, 8, ParticleProperty.DELETED_IN_1_13),
	LANDING_HONEY(Mat.HONEY_BLOCK,15),
	LANDING_LAVA(Mat.LAVA,14),
	LANDING_OBSIDIAN_TEAR(Mat.CRYING_OBSIDIAN,16),
	LAVA(Mat.LAVA, -1),
	LIGHT(Material.TORCH,17, ParticleProperty.DELETED_IN_1_18),
	MOB_APPEARANCE(Mat.SPAWNER, 8),
	NAUTILUS(Mat.NAUTILUS_SHELL, 13),
	NOTE(Mat.NOTE_BLOCK, -1),
	PORTAL(Mat.END_PORTAL_FRAME, -1),
	//REDSTONE(Mat.REDSTONE, -1),
	REVERSE_PORTAL(Mat.END_PORTAL_FRAME, 16),
	SCRAPE(Material.IRON_BLOCK, 17),
	SCULK_CHARGE(Mat.SCULK, 19),
	SCULK_CHARGE_POP(Mat.SCULK, 19),
	SCULK_SOUL(Mat.SCULK, 19),
	SHRIEK(Mat.SCULK_SHRIEKER, 19),
	SLIME(Mat.SLIME_BALL, -1),
	SMALL_FLAME(Mat.CAMPFIRE, 17),
	SMOKE_LARGE(Mat.CAMPFIRE, -1),
	SMOKE_NORMAL(Mat.CAMPFIRE, -1),
	SNEEZE(Material.PAPER, 14),
	SNOW_SHOVEL(Mat.SNOW_BALL, -1),
	SNOWBALL(Mat.SNOW_BALL, -1),
	SNOWFLAKE(Mat.SNOW_BALL, 17),
	SONIC_BOOM(Mat.PRISMARINE, 19),
	SOUL(Mat.SOUL_LANTERN, 16),
	SOUL_FIRE_FLAME(Mat.SOUL_LANTERN, 17),
	SPELL(Mat.ENCHANTED_BOOK, -1),
	SPELL_INSTANT(Mat.ENCHANTED_BOOK, -1),
	//SPELL_MOB(Mat.ENCHANTED_BOOK, -1),
	//SPELL_MOB_AMBIENT(Mat.ENCHANTED_BOOK, -1),
	SPELL_WITCH(Mat.ENCHANTED_BOOK, -1),
	SPIT(Mat.WATER, 11),
	SPORE_BLOSSOM_AIR(Mat.SPORE_BLOSSOM, 17),
	SQUID_INK(Mat.INK_SACK, 13),
	SUSPENDED_DEPTH(Mat.WATER, -1),
	SWEEP_ATTACK(Mat.DIAMOND_SWORD, 9),
	TOTEM(Mat.TOTEM_OF_UNDYING, 11),
	TOWN_AURA(Mat.ENCHANTED_BOOK, -1),
	//VIBRATION(Mat.STONE, 17),
	VILLAGER_ANGRY(Mat.VILLAGER_SPAWN_EGG, -1),
	VILLAGER_HAPPY(Mat.VILLAGER_SPAWN_EGG, -1),
	WARPED_SPORE(Mat.WARPED_FUNGUS, 16),
	WATER_DROP(Mat.WATER, 8),
	WATER_SPLASH(Mat.WATER, -1),
	WATER_WAKE(Mat.WATER, -1),
	WAX_OFF(Mat.HONEY_BLOCK, 17),
	WAX_ON(Mat.HONEY_BLOCK, 17),
	WHITE_ASH(Mat.SOUL_SAND, 16);

	private static int version = 0;
	// New name since 1.9 (some particles where renamed in 1.9)
	@Getter private final Material mat;
	@Getter private final int requiredVersion;
	private final ParticleType particle;
	private final List<ParticleProperty> properties;
	//I want an array that gives an item for each particle, to set them in a selecting menu.




	/**
	 * Returns the version of your server (1.x)
	 * 
	 * @return The version number
	 */
	public static int getVersion() {
		if(version == 0) {
			version = Integer.parseInt((Bukkit.getBukkitVersion().split("-")[0]).split("[.]")[1]);
		}
		return version;
	}

	

	/**
	 * Construct a new particle effect
	 * 
	 * @param name Name of this particle effect
	 * @param id Id of this particle effect
	 * @param requiredVersion Version which is required (1.x)
	 * @param properties Properties of this particle effect
	 */
	private Particle(Material mate, int requiredVersion, ParticleProperty... properties) {
		this.requiredVersion = requiredVersion;
		this.properties = Arrays.asList(properties);
		this.mat = mate;
		if(isSupported()) this.particle = ParticleType.of(getName());
		else this.particle = null;
	}
	


	/**
	 * Returns the name of this particle effect
	 * 
	 * @return The name
	 */
	public String getName() {
		return this.toString();
	}
	


	/**
	 * Determine if this particle effect has a specific property
	 * 
	 * @return Whether it has the property or not
	 */
	public boolean hasProperty(ParticleProperty property) {
		return properties.contains(property);
	}

	/**
	 * Determine if this particle effect is supported by your current server version
	 * 
	 * @return Whether the particle effect is supported or not
	 */
	public boolean isSupported() {
		if(hasProperty(ParticleProperty.DELETED_IN_1_13) && getVersion() >12)
			return false;
		if(hasProperty(ParticleProperty.DELETED_IN_1_18) && getVersion() >17)
			return false;
		if (requiredVersion == -1) 
			return true;
		return (getVersion() >= requiredVersion);
	}

	/**
	 * Determine if the distance between @param location and one of the players exceeds 256
	 * 
	 * @param location Location to check
	 * @return Whether the distance exceeds 256 or not
	 */
	private static boolean isFarAway(Location location, Player player) {
		Location ploc = player.getLocation();
		try{
			return ploc.distanceSquared(location) > 65536;
		}catch(IllegalArgumentException e) {
			return true;
		}
	}






	/**
	 * Displays a particle effect which is only visible for all players within a certain range in the world of @param center
	 * 
	 * @param offsetX Maximum distance particles can fly away from the center on the x-axis
	 * @param offsetY Maximum distance particles can fly away from the center on the y-axis
	 * @param offsetZ Maximum distance particles can fly away from the center on the z-axis
	 * @param speed Display speed of the particles
	 * @param amount Amount of particles
	 * @param center Center location of the effect
	 * @param range Range of the visibility
	 * @throws ParticleVersionException If the particle effect is not supported by the server version
	 * @throws ParticleDataException If the particle effect requires additional data
	 * @throws IllegalArgumentException If the particle effect requires water and none is at the center location
	 * @see ParticlePacket
	 * @see ParticlePacket#sendTo(Location, double)
	 */
	public void display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, double range) throws IllegalArgumentException {
		particle.spawn(center, amount, offsetX, offsetY, offsetZ, speed, null);
	}

	/**
	 * Displays a particle effect which is only visible for the specified players
	 * 
	 * @param offsetX Maximum distance particles can fly away from the center on the x-axis
	 * @param offsetY Maximum distance particles can fly away from the center on the y-axis
	 * @param offsetZ Maximum distance particles can fly away from the center on the z-axis
	 * @param speed Display speed of the particles
	 * @param amount Amount of particles
	 * @param center Center location of the effect
	 * @param players Receivers of the effect
	 * @throws ParticleVersionException If the particle effect is not supported by the server version
	 * @throws ParticleDataException If the particle effect requires additional data
	 * @throws IllegalArgumentException If the particle effect requires water and none is at the center location
	 * @see ParticlePacket
	 * @see ParticlePacket#sendTo(Location, List)
	 */
	public void display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, List<Player> players) throws  IllegalArgumentException {
		for(Player p: players) if(!isFarAway(center, p)) particle.spawn(p, center, amount, offsetX, offsetY, offsetZ, speed, null);
	}

	/**
	 * Displays a particle effect which is only visible for the specified players
	 * 
	 * @param offsetX Maximum distance particles can fly away from the center on the x-axis
	 * @param offsetY Maximum distance particles can fly away from the center on the y-axis
	 * @param offsetZ Maximum distance particles can fly away from the center on the z-axis
	 * @param speed Display speed of the particles
	 * @param amount Amount of particles
	 * @param center Center location of the effect
	 * @param players Receivers of the effect
	 * @throws ParticleVersionException If the particle effect is not supported by the server version
	 * @throws ParticleDataException If the particle effect requires additional data
	 * @throws IllegalArgumentException If the particle effect requires water and none is at the center location
	 * @see #display(float, float, float, float, int, Location, List)
	 */
	public void display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, Player... players) throws IllegalArgumentException {
			display(offsetX, offsetY, offsetZ, speed, amount, center, Arrays.asList(players));
	}


	/**
	 * Represents the property of a particle effect
	 * <p>
	 * This class is part of the <b>ParticleEffect Library</b> and follows the same usage conditions
	 * 
	 * @author DarkBlade12
	 * @since 1.7
	 */
	public static enum ParticleProperty {
		DELETED_IN_1_13,DELETED_IN_1_18;
	}

	/**
	 * Get the name of the particle in a readable format
	 * Capitalize the first letter of any word in a string and replace "_" with " " in it.
	 * Example: FALLING_OBSIDIAN_TEAR -> Falling Obsidian Tear
	 * @param string The string to transform so all words begin with an uppercase letter
	 * @return The modified String
	 */
	public String getReadableName() {
		  char[] chars = getName().toLowerCase().replace("_", " ").toCharArray();
		  boolean found = false;
		  for (int i = 0; i < chars.length; i++) {
		    if (!found && Character.isLetter(chars[i])) {
		      chars[i] = Character.toUpperCase(chars[i]);
		      found = true;
		    } else if (Character.isWhitespace(chars[i])) { // You can add other chars here
		      found = false;
		    }
		  }
		 return (String.valueOf(chars));
	}
	
	/**
	 * Revert the process of the function above
	 * @param sting
	 * @return
	 */
	public static String getUnreadableName(String sting) {
		return sting.toUpperCase().replace(" ", "_");
	}
	


	


	
}