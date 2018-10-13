package fr.black_eyes.lootchest;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.black_eyes.lootchest.commands.Lootchest;

public class Utils implements Listener {

	
	public static void msg(Player p, String path, String replacer, String replacement) {
		p.sendMessage(getMsg(path, replacer, replacement));
	}
	
	public static String getMsg(String path, String replacer, String replacement) {
		return Main.getInstance().getLang().getString(path).replace(replacer, replacement).replace("&", "§");
	}
	//créer le coffe et enregistrer les infos
	public static void saveChest(Block chest, String name) {
		Inventory inv = ((Chest) chest.getState()).getInventory();
		for(int i = 0 ; i < inv.getSize() ; i++) {
			if(inv.getItem(i) != null) {
				Main.getInstance().getData().set("chests." + name + ".inventory." + i, inv.getItem(i));
				Main.getInstance().getData().set("chests." + name + ".chance." + i, Main.getInstance().getConfig().getInt("default_item_chance"));
			}
		}
		Main.getInstance().getData().set("chests." + name + ".holo", name);
		Main.getInstance().getData().set("chests." + name + ".time", Main.getInstance().getConfig().getInt("default_reset_time"));
		Main.getInstance().getData().set("chests." + name + ".location", chest.getLocation());
		Main.getInstance().getData().set("chests." + name + ".lastreset", new Timestamp(System.currentTimeMillis()).getTime());
		Location loc = chest.getLocation();
	    final Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
	   	loc2.setX(loc.getX()+0.5);
	    loc2.setY(loc.getY()+0.5);
	    loc2.setZ(loc.getZ()+0.5);
	   	Main.getInstance().getData().set("chests." +name+ ".particle", Main.getInstance().getConfig().getString("Particles.default_particle"));
		try {
			Main.getInstance().getData().save(Main.getInstance().getDataF());
			Main.getInstance().getData().load(Main.getInstance().getDataF());
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		((Chest) chest.getLocation().getBlock().getState()).getInventory().clear();
		chest.getLocation().getBlock().setType(Material.AIR);
		restoreChest(name);
	}
	
	public static void deleteChest(String name) {
		final Location loc = (Location) Main.getInstance().getData().get("chests." + name + ".location");
		Block chest = loc.getBlock();
		if(chest.getType().equals(Material.CHEST)) {
			((Chest) chest.getState()).getInventory().clear();
		}
		Main.getInstance().getData().set("chests." + name, null);
		try {
			Main.getInstance().getData().save(Main.getInstance().getDataF());
			Main.getInstance().getData().load(Main.getInstance().getDataF());
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		chest.getLocation().getBlock().setType(Material.AIR);
		final Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		loc2.setX(loc.getX()+0.5);
		loc2.setY(loc.getY()+0.5);
		loc2.setZ(loc.getZ()+0.5);
		Main.part.remove(loc2);
		deleteholo(chest.getLocation());
		
	}
	
	//se sert du data.yml pour set le coffre et remplir son inventaire, créer l'holo en fonction du nom 
	public static void restoreChest(String name) {
		final Location loc = (Location) Main.getInstance().getData().get("chests." + name + ".location");
		if(loc.getBlock().getType().equals(Material.CHEST)) {
			if(!isEmpty(((Chest) loc.getBlock().getState()).getInventory())) {
				deleteholo(loc);
				makeHolo(loc, name);
				return;
			}
		}
		makeHolo(loc, name);
		loc.getBlock().setType(Material.CHEST);
		for(String keys : Main.getInstance().getData().getConfigurationSection("chests." + name + ".inventory").getKeys(false)) {
			ItemStack item = Main.getInstance().getData().getItemStack("chests." + name + ".inventory." + keys);
			int slot = Integer.parseInt(keys);
			int percent = ThreadLocalRandom.current().nextInt(0, 100 + 1);
			if(percent <= Main.getInstance().getData().getInt("chests." + name + ".chance." + keys)){
				((Chest) loc.getBlock().getState()).getInventory().setItem(slot, item);
			}
		}
		final Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		loc2.setX(loc.getX()+0.5);
		loc2.setY(loc.getY()+0.5);
		loc2.setZ(loc.getZ()+0.5);
		Particle particules[] = {Particle.EXPLOSION_HUGE, Particle.EXPLOSION_LARGE, Particle.EXPLOSION_NORMAL, Particle.FIREWORKS_SPARK, Particle.WATER_BUBBLE, Particle.SUSPENDED, Particle.TOWN_AURA, Particle.CRIT, Particle.CRIT_MAGIC, Particle.SMOKE_NORMAL, Particle.SMOKE_LARGE, Particle.SPELL_MOB, Particle.SPELL_MOB_AMBIENT, Particle.SPELL, Particle.SPELL_INSTANT, Particle.SPELL_WITCH, Particle.NOTE, Particle.PORTAL, Particle.ENCHANTMENT_TABLE, Particle.FLAME, Particle.LAVA, Particle.FOOTSTEP, Particle.WATER_SPLASH, Particle.WATER_WAKE, Particle.CLOUD, Particle.REDSTONE, Particle.SNOWBALL, Particle.DRIP_WATER, Particle.DRIP_LAVA, Particle.SNOW_SHOVEL, Particle.SLIME, Particle.HEART, Particle.VILLAGER_ANGRY, Particle.VILLAGER_HAPPY, Particle.BARRIER};
		for(Particle part : particules) {
			if((""+part).contains(Main.getInstance().getData().getString("chests." + name + ".particle")))
				Main.part.put(loc2, part);
		}
	}
	
	
	public static boolean isEmpty(Inventory inv) {
	     for (ItemStack item : inv.getContents() ) {
	       if ( item != null ) {
	         return false;
	       }
	     }
	     return true;
	}

	public static String isLootChest(Location loc) {
		for(String keys : Main.getInstance().getData().getConfigurationSection("chests").getKeys(false)) {
			Location loc2 = (Location) Main.getInstance().getData().get("chests." + keys + ".location");
			if(loc2.equals(loc)) {
				return keys;
			}
		}
		return " ";
	}
	
	
	//Pour faire des hologrames au dessus des coffres
	
	public static void deleteholo(Location loc) {
		final Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		loc2.setX(loc.getX()+0.5);
		loc2.setY(loc.getY()+1);
		loc2.setZ(loc.getZ()+0.5);
		//the coordinates of a block are at the corner of the block
		for(Entity ent : loc2.getWorld().getNearbyEntities(loc2, 1.0,1.0,1.0)) {
			if(ent instanceof ArmorStand) {
				ent.remove();
			}
		}
	}
	//the below function was created using some spigot forums code with some modifications
	public static void makeHolo(Location loc, String text) {
		final Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		loc2.setX(loc.getX()+0.5);
		loc2.setY(loc.getY()+1);
		loc2.setZ(loc.getZ()+0.5);
		//the coordinates of a block are at the corner of the block
		ArmorStand as = (ArmorStand) loc2.getWorld().spawnEntity(loc2, EntityType.ARMOR_STAND); //Spawn the ArmorStand

		as.setGravity(false); //Make sure it doesn't fall
		as.setCanPickupItems(false); //I'm not sure what happens if you leave this as it is, but you might as well disable it
		as.setCustomName(Main.getInstance().getData().getString("chests." + text + ".holo").replace("&", "§")); //Set this to the text you want
		as.setCustomNameVisible(true); //This makes the text appear no matter if your looking at the entity or not
		as.setVisible(false); //Makes the ArmorStand invisible
	 	as.setArms(false);
	 	as.setBasePlate(false);
	 	as.setMarker(true);
	 	as.setSmall(true);
	}	
	
	@EventHandler
	public void manipulate(PlayerArmorStandManipulateEvent e)
	{
	        if(!e.getRightClicked().isVisible())
	        {
	            e.setCancelled(true);
	        }
	}
	
	
	
	
	
	
	//Inventaires
	
	
	public static void invChances(Player p, String name) {
		final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 27, Utils.getMsg("Menu.chances.name", "[Chest]", name));
		for(String keys : Main.getInstance().getData().getConfigurationSection("chests." + name + ".inventory").getKeys(false)) {
			ItemStack item = Main.getInstance().getData().getItemStack("chests." + name + ".inventory." + keys);
			List<String> lore = new ArrayList<String>();
			lore.add(getMsg("Menu.chances.lore", "[Chest]", name));
			lore.add(Main.getInstance().getData().getString("chests." + name + ".chance." + keys) + "%");
			ItemMeta im = item.getItemMeta();
			im.setLore(lore);
			ItemStack item2 = new ItemStack(item.getType(), item.getAmount());
			item2.setItemMeta(im);
			int slot = Integer.parseInt(keys);
			inv.setItem(slot, item2);
		}
		Lootchest.editinv.put(p, name);
		p.openInventory(inv);
	}
	
	public static void invTime(Player p, String name) {
		final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 27, Utils.getMsg("Menu.time.name", "[Chest]", name));
        inv.setItem(16, getItem(Material.GOLD_NUGGET, getMsg("Menu.time.minutes", " ", " ")));
        inv.setItem(15, getItem(Material.GOLD_NUGGET, getMsg("Menu.time.minutes", " ", " ")));
        inv.setItem(14, getItem(Material.STICK, ""));
        inv.setItem(13, getItem(Material.GOLD_INGOT, getMsg("Menu.time.hours", " ", " ")));
        inv.setItem(12, getItem(Material.GOLD_INGOT, getMsg("Menu.time.hours", " ", " ")));
        inv.setItem(11, getItem(Material.STICK, ""));
        inv.setItem(10, getItem(Material.GOLD_BLOCK, getMsg("Menu.time.days", " ", " ")));
        inv.setItem(9, getItem(Material.GOLD_BLOCK, getMsg("Menu.time.days", " ", " ")));
		Lootchest.editinv.put(p, name);
		p.openInventory(inv);
	}
	
	public static void invEdit(Player p, String name) {
		final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 27, Utils.getMsg("Menu.items.name", "[Chest]", name));
		for(String keys : Main.getInstance().getData().getConfigurationSection("chests." + name + ".inventory").getKeys(false)) {
			ItemStack item = Main.getInstance().getData().getItemStack("chests." + name + ".inventory." + keys);
			int slot = Integer.parseInt(keys);
			inv.setItem(slot, item);
		}
		Lootchest.editinv.put(p, name);
		p.openInventory(inv);
	}
	
	
	public static void mainInv(Player p, String name) {
        final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 27, getMsg("Menu.main.name", " ", " "));
        inv.setItem(11, getItem(Material.EYE_OF_ENDER, getMsg("Menu.main.particles", " ", " ")));
        inv.setItem(13, getItem(Material.CHEST, getMsg("Menu.main.content", " ", " ")));
        inv.setItem(15, getItem(Material.WATCH, getMsg("Menu.main.respawnTime", " ", " ")));
        inv.setItem(22, getItem(Material.DIAMOND, getMsg("Menu.main.chances", " ", " ")));
        new BukkitRunnable() {       
            @Override
            public void run() {
            	p.openInventory(inv);
                Lootchest.editinv.put(p, name);
            }                
        }.runTaskLater(Main.getInstance(), 2);
    }
	
	
	
	// /!\ Certains items ne sont pas les mêmes selont que l'on est en 1.12 ou 1.13, à vérifier pour particules
	public static void particleInv(Player p, String name) {
        final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, getMsg("Menu.particles.name", " ", " "));
        inv.setItem(0, getItem(Material.TNT, "Huge Explosion"));
        inv.setItem(1, getItem(Material.TNT, "Large Explosion"));
        inv.setItem(2, getItem(Material.TNT, "Normal Explosion"));
        inv.setItem(3, getItem(Material.FIREWORK, "Fireworks Sparks"));
        inv.setItem(4, getItem(Material.PRISMARINE_CRYSTALS, "Bubble Pop"));
        inv.setItem(5, getItem(Material.STONE, "Suspended"));
        inv.setItem(6, getItem(Material.MYCEL, "Town Aura"));
        inv.setItem(7, getItem(Material.IRON_SWORD, "Crit"));
        inv.setItem(8, getItem(Material.DIAMOND_SWORD, "Magic Crit"));
        inv.setItem(9, getItem(Material.FURNACE, "Normal Smoke"));
        inv.setItem(10, getItem(Material.FURNACE, "Large Smoke"));
        inv.setItem(11, getItem(Material.ENCHANTED_BOOK, "Mob Spell"));
        inv.setItem(12, getItem(Material.ENCHANTED_BOOK, "Mob Spell Ambient"));
        inv.setItem(13, getItem(Material.ENCHANTED_BOOK, "Spell"));
        inv.setItem(14, getItem(Material.ENCHANTED_BOOK, "Instant Spell"));
        inv.setItem(15, getItem(Material.ENCHANTED_BOOK, "Witch Spell"));
        inv.setItem(16, getItem(Material.NOTE_BLOCK, "Note"));
        inv.setItem(17, getItem(Material.ENDER_PORTAL_FRAME, "Portal"));
        inv.setItem(18, getItem(Material.ENCHANTMENT_TABLE , "Enchantment Table"));
        inv.setItem(19, getItem(Material.BLAZE_POWDER, "Flame"));
        inv.setItem(20, getItem(Material.LAVA_BUCKET, "Lava"));
        inv.setItem(21, getItem(Material.STONE, "Footstep"));
        inv.setItem(22, getItem(Material.WATER_BUCKET, "Water Splash"));
        inv.setItem(23, getItem(Material.WATER_BUCKET, "Water Wake"));
        inv.setItem(24, getItem(Material.QUARTZ, "Cloud"));
        inv.setItem(25, getItem(Material.REDSTONE, "Redstone"));
        inv.setItem(26, getItem(Material.SNOW_BALL, "Snowball"));
        inv.setItem(27, getItem(Material.WATER_BUCKET, "Drip Water"));
        inv.setItem(28, getItem(Material.LAVA_BUCKET, "Drip Lava"));
        inv.setItem(29, getItem(Material.IRON_SPADE, "Snow Shovel"));
        inv.setItem(30, getItem(Material.SLIME_BALL, "Slime"));
        inv.setItem(31, getItem(Material.RED_ROSE, "Heart"));
        inv.setItem(32, getItem(Material.REDSTONE_BLOCK, "Angry Villager"));
        inv.setItem(33, getItem(Material.EMERALD, "Happy Villager"));
        inv.setItem(34, getItem(Material.BARRIER, "Barrier"));
        p.openInventory(inv);
        Lootchest.editinv.put(p, name);
    }


	public static ItemStack getItem(final Material material, final String customName) {
		final ItemStack A = new ItemStack(material, 1);
		final ItemMeta B = A.getItemMeta();
		if (customName != null) {
			B.setDisplayName(customName);
		}
		A.setItemMeta(B);
		return A;
	}
	
	
	
	
	
}
