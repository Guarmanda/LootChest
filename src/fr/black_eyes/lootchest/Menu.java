package fr.black_eyes.lootchest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.black_eyes.lootchest.commands.LootchestCommand;

public class Menu extends Utils{
	//Inventaires
	public  void invChances(Player p, Lootchest name) {
		final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 27, getMsg("Menu.chances.name", "[Chest]", name.name));
		for(int i = 0; i < name.inv.getSize(); i++) {
			if(name.inv.getItem(i) != null && name.inv.getItem(i).getType()!= Material.AIR) {
				ItemStack item = name.inv.getItem(i).clone();
				String lore = getMsg("Menu.chances.lore", "[Chest]", name.name);
				List<String> lore2 = new ArrayList<String>(Arrays.asList(lore.split("\\|\\|")));
				lore2.add(name.chances[i]+ "%");
				ItemMeta im = item.getItemMeta();
				
				im.setLore(lore2);
				ItemStack item2 = new ItemStack(item.getType(), item.getAmount());
				item2.setItemMeta(im);
				
				inv.setItem(i, item2);
			}
		}
		LootchestCommand.editinv.put(p, name.name);
		p.openInventory(inv);
	}
	
	public  void invTime(Player p, Lootchest name) {
		final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 27, getMsg("Menu.time.name", "[Chest]", name.name));
		if(name.getTime() != -1) {
			inv.setItem(4, getItem(Mat.TOTEM_OF_UNDYING, getMsg("Menu.time.infinite", " ", " ")));
		}else {
			inv.setItem(4, getItem(Mat.TOTEM_OF_UNDYING, getMsg("Menu.time.notInfinite", " ", " ")));
		}
		
		long temps = name.getTime();
		long jours = temps/1440;
		long heures = (temps-jours*1440)/60;
		long minutes = temps - (jours*1440+heures*60);
		
		
		//Initialisation du menu selon le temps du coffre
		if (jours/10 == 0) {
			inv.setItem(9, getItem(Mat.BARRIER, getMsg("Menu.time.days", " ", " ")));
		} else {
			inv.setItem(9, getItem(Mat.GOLD_BLOCK, getMsg("Menu.time.days", " ", " ")));
			inv.getItem(9).setAmount((int) (jours/10));
		} if (jours/10*10 == jours) {
			inv.setItem(10, getItem(Mat.BARRIER, getMsg("Menu.time.days", " ", " ")));
		}else {
			inv.setItem(10, getItem(Mat.GOLD_BLOCK, getMsg("Menu.time.days", " ", " ")));
			inv.getItem(10).setAmount((int) (jours-jours/10*10));
		}if (heures/10 == 0) {
			inv.setItem(12, getItem(Mat.BARRIER, getMsg("Menu.time.hours", " ", " ")));
		}else {
			inv.setItem(12, getItem(Mat.GOLD_INGOT, getMsg("Menu.time.hours", " ", " ")));
			inv.getItem(12).setAmount((int) (heures/10));
		}if (heures/10*10 == heures) {
			inv.setItem(13, getItem(Mat.BARRIER, getMsg("Menu.time.hours", " ", " ")));
		}else {
			inv.setItem(13, getItem(Mat.GOLD_INGOT, getMsg("Menu.time.hours", " ", " ")));
			inv.getItem(13).setAmount((int) (heures-heures/10*10));
		}if (minutes/10 == 0) {
			inv.setItem(15, getItem(Mat.BARRIER, getMsg("Menu.time.minutes", " ", " ")));
		}else {
			inv.setItem(15, getItem(Mat.GOLD_NUGGET, getMsg("Menu.time.minutes", " ", " ")));
			inv.getItem(15).setAmount((int) (minutes/10));
		}if (minutes/10*10 == minutes || minutes == -1) {
			inv.setItem(16, getItem(Mat.BARRIER, getMsg("Menu.time.minutes", " ", " ")));
		}else {
			inv.setItem(16, getItem(Mat.GOLD_NUGGET, getMsg("Menu.time.minutes", " ", " ")));
			inv.getItem(16).setAmount((int) (minutes-minutes/10*10));
		}
        inv.setItem(14, getItem(Mat.STICK, ""));
        inv.setItem(11, getItem(Mat.STICK, ""));
		LootchestCommand.editinv.put(p, name.name);
		ItemStack sign = new ItemStack(Mat.SIGN, 1);
		ItemMeta meta = sign.getItemMeta(); 
		if(minutes != -1) {
			meta.setDisplayName("Respawn time: " + jours+" days, " + heures + " hours, " + minutes + " minutes.");
		}
		else {
			meta.setDisplayName("Respawn time: infinite");
		}
		sign.setItemMeta(meta);
    	inv.setItem(22, sign);
		p.openInventory(inv);
	}
	
	public  void invEdit(Player p, Lootchest name) {
		final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 27, getMsg("Menu.items.name", "[Chest]", name.name));
		inv.setContents(name.inv.getContents());;
		LootchestCommand.editinv.put(p, name.name);
		p.openInventory(inv);
	}
	
	public  void invcopy(Player p, Lootchest chest, int j) {
		int i = 0;
		int nbBoxes = 0;
		final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, getMsg("Menu.copy.name", " ", " "));
		Set<String> boxes = Main.getInstance().getLootChest().keySet();

		for(String keys : boxes) {
			if(j== 2 && nbBoxes < 53) nbBoxes++;
			else if(j> 2 && nbBoxes < (j*54-(2*(j-1)))-52) nbBoxes++;
			//exempter le coffre actuel de la liste, et si il y a plus de 54 coffres, stopper i à 53 si on doit faire deux pages
			
			else if(!keys.equals(chest.name) && (i!=45 || j==1) && (i!=53 || (boxes.size() -1)<=(j*52+1) ) ){
				String name = Main.getInstance().getLootChest().get(keys).holo.replace("&", "§");
				String effect = configFiles.getData().getString("chests." + keys + ".particle");
				String world;
				if(Bukkit.getWorld(Main.getInstance().getLootChest().get(keys).world) == null) {
					world = "Unloaded world";
				}
				else {
					world = Bukkit.getWorld(Main.getInstance().getLootChest().get(keys).world).getName();
				}
				ItemStack item = getItemWithLore(Material.CHEST, "§6" +keys, "§bHologram: §6" + name + "||§bWorld: §6"+ world + "||§bEffect: §6" + effect);
				inv.setItem(i++, item);
			}
			else if (!keys.equals(chest.name) && i==45) {
				String name = getMsg("Menu.copy.page", "[Number]", j-1+"");
				ItemStack item = getItem(Material.PAPER,  name );
				inv.setItem(i++, item);
				String world;
				if(Bukkit.getWorld(Main.getInstance().getLootChest().get(keys).world) == null) {
					world = "Unloaded world";
				}
				else {
					world = Bukkit.getWorld(Main.getInstance().getLootChest().get(keys).world).getName();
				}
				String name2 = Main.getInstance().getLootChest().get(keys).holo.replace("&", "§");
				String effect = Main.getInstance().getLootChest().get(keys).particle;

				ItemStack item2 = getItemWithLore(Material.CHEST, "§6" +keys, "§bHologram: §6" + name2 + "||§bWorld: §6"+ world + "||§bEffect: §6" + effect);
				inv.setItem(i++, item2);
			}
			else if (!keys.equals(chest.name) && i==53){
				String name = getMsg("Menu.copy.page", "[Number]", (j+1)+"");

				ItemStack item = getItem(Material.PAPER,  name );
				inv.setItem(i, item);
				break;

			}
		}			

		p.openInventory(inv);
        LootchestCommand.editinv.put(p, chest.name);
	}
	
	
	public void invType(Player p, Lootchest name) {
		final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 9, getMsg("Menu.time.name", "[Chest]", name.name));
		inv.setItem(0, new ItemStack(Mat.CHEST, 1));
		inv.setItem(1, new ItemStack(Mat.TRAPPED_CHEST, 1));
		if(Mat.BARREL!=Mat.CHEST) {
			inv.setItem(3,  new ItemStack(Mat.BARREL));
		}
		p.openInventory(inv);
		LootchestCommand.editinv.put(p, name.name);
	}
	
	public  void mainInv(Player p, String name) {
        final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 36, getMsg("Menu.main.name", " ", " "));
        inv.setItem(4, getItem(Mat.ENDER_CHEST, getMsg("Menu.main.copychest", " ", " ")));
        if(Main.configs.PART_enable) {
        	inv.setItem(11, getItem(Mat.ENDER_EYE, getMsg("Menu.main.particles", " ", " ")));
        }
        inv.setItem(9, getItem(Mat.NOTE_BLOCK, getMsg("Menu.main.type", " ", " ")));
        inv.setItem(13, getItem(Mat.CHEST, getMsg("Menu.main.content", " ", " ")));
        inv.setItem(15, getItem(Mat.CLOCK, getMsg("Menu.main.respawnTime", " ", " ")));
        inv.setItem(22, getItem(Mat.DIAMOND, getMsg("Menu.main.chances", " ", " ")));
        Lootchest lc = Main.getInstance().getLootChest().get(name);
        inv.setItem(28, getEnabled("fall", lc.fall));
        inv.setItem(30, getEnabled("respawn_cmd", lc.respawn_cmd));
        inv.setItem(32, getEnabled("respawn_natural", lc.respawn_natural));
        inv.setItem(34, getEnabled("take_message", lc.take_msg));
        
        new BukkitRunnable() {       
            @Override
            public void run() {
            	p.openInventory(inv);
                LootchestCommand.editinv.put(p, name);
            }                
        }.runTaskLater(Main.getInstance(), 2);
    }
	
	public ItemStack getEnabled(String path, Boolean name){
		if(name) {
			return getItem(Mat.EMERALD_BLOCK, getMsg("Menu.main.disable_" + path, " ", " "));
		}else {
			return getItem(Mat.REDSTONE_BLOCK, getMsg("Menu.main.enable_" + path, " ", " "));
		}
	}
	
	

	

	
	// /!\ Certains items ne sont pas les m§mes selont que l'on est en 1.12 ou 1.13, § v§rifier pour particules
	public  void particleInv(Player p, String name) {
        final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, getMsg("Menu.particles.name", " ", " "));
        String[] part = {"Huge Explosion", "Large Explosion", "Normal Explosion",  "Fireworks Sparks", "Bubble Pop", "Suspended",
        		"Town Aura", "Crit", "Magic Crit", "Normal Smoke", "Large Smoke", "Mob Spell", "Mob Spell Ambient", "Spell",
        		"Instant Spell", "Witch Spell", "Note", "Portal", "Enchantment Table", "Flame","Lava", "Footstep", "Water Splash",
        		"Water Wake",  "Cloud", "Snowball", "Drip Water", "Drip Lava", "Snow Shovel", "Slime", "Heart", "Angry Villager",
        		 "Happy Villager", "Barrier"};
        Material[] mat = {Mat.TNT,Mat.TNT,Mat.TNT,Mat.FIREWORK,Mat.PRISMARINE,Mat.STONE,Mat.MYCELIUM,Mat.IRON_SWORD,Mat.DIAMOND_SWORD,Mat.FURNACE,Mat.FURNACE,Mat.ENCHANTED_BOOK,Mat.ENCHANTED_BOOK,Mat.ENCHANTED_BOOK,Mat.ENCHANTED_BOOK,Mat.ENCHANTED_BOOK,
        		Mat.NOTE_BLOCK,Mat.END_PORTAL_FRAME,Mat.ENCHANTING_TABLE,Mat.BLAZE_POWDER,Mat.LAVA_BUCKET,Mat.STONE,Mat.WATER_BUCKET,Mat.WATER_BUCKET,Mat.QUARTZ,
        		Mat.SNOW_BALL,Mat.WATER_BUCKET,Mat.LAVA_BUCKET,Mat.IRON_SHOVEL,Mat.SLIME_BALL,Mat.ROSE_RED,Mat.REDSTONE_BLOCK,Mat.EMERALD,Mat.BARRIER
        };
        for(int i=0; i<34; i++) {
        	 inv.setItem(i, getItem(mat[i], part[i]));
        }
        inv.setItem(49, getItem(Mat.BARRIER, "Disable particles"));
        p.openInventory(inv);
        LootchestCommand.editinv.put(p, name);
    }


	public  ItemStack getItem(final Material enderChest, final String customName) {
		final ItemStack A = new ItemStack(enderChest, 1);
		final ItemMeta B = A.getItemMeta();
		if (customName != null) {
			B.setDisplayName(customName);
		}
		A.setItemMeta(B);
		return A;
	}
	
	public  ItemStack getItemWithLore(final Material material, final String customName,  String lore) {
		final ItemStack A = new ItemStack(material);
		final ItemMeta B = A.getItemMeta();
		if (customName != null) {
			B.setDisplayName(customName);
			List<String> lore2 = new ArrayList<String>(Arrays.asList(lore.split("\\|\\|")));
			B.setLore(lore2);
		}
		A.setItemMeta(B);
		return A;
	}
}
