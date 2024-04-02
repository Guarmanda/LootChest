package fr.black_eyes.lootchest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.black_eyes.lootchest.commands.LootchestCommand;
import fr.black_eyes.lootchest.particles.Particle;

public class Menu {
	
	private Main main;
	private Files configFiles;

	public Menu() {
		main = Main.getInstance();
		configFiles = main.getConfigFiles();
	}

	

	private Inventory createInventory(String name, int size) {
		return Bukkit.createInventory((InventoryHolder)null, size, name);
	}
	
	//Inventaires
	public  void invChances(Player p, Lootchest name) {
		p.closeInventory();
		LootchestCommand.editinv.put(p, name.getName());
		String menuName = Utils.getMenuName("chances", name.getName());
		LootchestCommand.menuName.put(p, menuName);
		final Inventory inv = createInventory(menuName, 27);
		for(int i = 0; i < name.getInv().getSize(); i++) {
			if(name.getInv().getItem(i) != null && name.getInv().getItem(i).getType()!= Material.AIR) {
				ItemStack item = name.getInv().getItem(i).clone();
				String lore = Utils.getMsg("Menu.chances.lore", "[Chest]", name.getName());
				List<String> lore2 = new ArrayList<String>(Arrays.asList(lore.split("\\|\\|")));
				lore2.add(name.chances[i]+ "%");
				ItemMeta im = item.getItemMeta();
				
				im.setLore(lore2);
				ItemStack item2 = new ItemStack(item.getType(), item.getAmount());
				item2.setItemMeta(im);
				
				inv.setItem(i, item2);
			}
		}
		LootchestCommand.editinv.put(p, name.getName());
		p.openInventory(inv);
	}
	
	
	
	public  void invTime(Player p, Lootchest name) {
		p.closeInventory();
		LootchestCommand.editinv.put(p, name.getName());
		String menuName = Utils.getMenuName("time", name.getName());
		LootchestCommand.menuName.put(p, menuName);
		final Inventory inv = createInventory(menuName, 27);
		if(name.getTime() != -1) {
			inv.setItem(4, getItem(Mat.TOTEM_OF_UNDYING, Utils.getMsg("Menu.time.infinite", " ", " ")));
		}else {
			inv.setItem(4, getItem(Mat.TOTEM_OF_UNDYING, Utils.getMsg("Menu.time.notInfinite", " ", " ")));
		}
		
		long temps = name.getTime();
		long jours = temps/1440;
		long heures = (temps-jours*1440)/60;
		long minutes = temps - (jours*1440+heures*60);
		
		String menustr = "Menu.time.";
		String hourstr = menustr + "hours";
		String daystr = menustr+"days";
		String minstr = menustr+"minutes";
		long[] timings = {jours, heures, minutes};
		String[] timingsstr = {daystr, hourstr, minstr};
		Material[] mats = {Mat.GOLD_BLOCK, Mat.GOLD_INGOT, Mat.GOLD_NUGGET};
		int startingSlot = 9;
		for(int k = 0; k < timings.length; k++) {
			if(timings[k]/10 == 0) {
				inv.setItem(startingSlot, getItem(Mat.BARRIER, Utils.getMsg(timingsstr[k])));
			}else {
				inv.setItem(startingSlot, getItem(mats[k], Utils.getMsg(timingsstr[k])));
				inv.getItem(startingSlot).setAmount((int) (timings[k]/10));
			} 
			if(timings[k]/10*10 == timings[k] || timings[k] == -1 && k == 2) {
				inv.setItem(startingSlot+1, getItem(Mat.BARRIER, Utils.getMsg(timingsstr[k])));
			}else {
				inv.setItem(startingSlot+1, getItem(mats[k], Utils.getMsg(timingsstr[k])));
				inv.getItem(startingSlot+1).setAmount((int) (timings[k]-timings[k]/10*10));
			}
			startingSlot+=3;
		}

		
        inv.setItem(14, getItem(Mat.STICK, ""));
        inv.setItem(11, getItem(Mat.STICK, ""));
		LootchestCommand.editinv.put(p, name.getName());
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
		p.closeInventory();
		LootchestCommand.editinv.put(p, name.getName());
		String menuName = Utils.getMenuName("items", name.getName());
		LootchestCommand.menuName.put(p, menuName);
		final Inventory inv = createInventory(menuName, 27);
		inv.setContents(name.getInv().getContents());;
		LootchestCommand.editinv.put(p, name.getName());
		p.openInventory(inv);
	}
	
	public  void invcopy(Player p, Lootchest chest, int j) {
		p.closeInventory();
		LootchestCommand.editinv.put(p, chest.getName());
		String menuName = Utils.getMenuName("copy", chest.getName());
		LootchestCommand.menuName.put(p, menuName);
		int i = 0;
		int nbBoxes = 0;
		final Inventory inv = createInventory(menuName, 54);
		Set<String> unsortedBoxes = Main.getInstance().getLootChest().keySet();
		SortedSet<String> boxes = new TreeSet<String>();
		boxes.addAll(unsortedBoxes);

		for(String keys : boxes) {
			if(j== 2 && nbBoxes < 53) nbBoxes++;
			else if(j> 2 && nbBoxes < (j*54-(2*(j-1)))-52) nbBoxes++;
			//exempter le coffre actuel de la liste, et si il y a plus de 54 coffres, stopper i à 53 si on doit faire deux pages
			
			else if(!keys.equals(chest.getName()) && (i!=45 || j==1) && (i!=53 || (boxes.size() -1)<=(j*52+1) ) ){
				String name = Utils.color(Main.getInstance().getLootChest().get(keys).getHolo());
				String effect = configFiles.getData().getString("chests." + keys + ".particle");
				String world;
				if(Bukkit.getWorld(Main.getInstance().getLootChest().get(keys).getWorld()) == null) {
					world = "Unloaded world";
				}
				else {
					world = Bukkit.getWorld(Main.getInstance().getLootChest().get(keys).getWorld()).getName();
				}
				ItemStack item = getItemWithLore(Mat.CHEST, Utils.color("&6" +keys), Utils.color("&bHologram: &6" + name + "||&bWorld: &6"+ world + "||&bEffect: &6" + effect));
				inv.setItem(i++, item);
			}
			else if (!keys.equals(chest.getName()) && i==45) {
				String name = Utils.getMsg("Menu.copy.page", "[Number]", j-1+"");
				ItemStack item = getItem(Material.PAPER,  name );
				inv.setItem(i++, item);
				String world;
				if(Bukkit.getWorld(Main.getInstance().getLootChest().get(keys).getWorld()) == null) {
					world = "Unloaded world";
				}
				else {
					world = Bukkit.getWorld(Main.getInstance().getLootChest().get(keys).getWorld()).getName();
				}
				String name2 = Utils.color(Main.getInstance().getLootChest().get(keys).getHolo());
				String effect = (Main.getInstance().getLootChest().get(keys).getParticle()!=null)?Main.getInstance().getLootChest().get(keys).getParticle().getName():"Disabled";

				ItemStack item2 = getItemWithLore(Material.CHEST, Utils.color("&6" +keys), Utils.color("&bHologram: &6" + name2 + "||&bWorld: &6"+ world + "||&bEffect: &6" + effect));
				inv.setItem(i++, item2);
			}
			else if (!keys.equals(chest.getName()) && i==53){
				String name = Utils.getMsg("Menu.copy.page", "[Number]", (j+1)+"");

				ItemStack item = getItem(Material.PAPER,  name );
				inv.setItem(i, item);
				break;

			}
		}			

		p.openInventory(inv);
        LootchestCommand.editinv.put(p, chest.getName());
	}
	
	
	public void invType(Player p, Lootchest name) {
		p.closeInventory();
		LootchestCommand.editinv.put(p, name.getName());
		String menuName = Utils.getMenuName("type", name.getName());
		LootchestCommand.menuName.put(p, menuName);
		final Inventory inv = createInventory(menuName, 9);
		inv.setItem(0, new ItemStack(Mat.CHEST, 1));
		inv.setItem(1, new ItemStack(Mat.TRAPPED_CHEST, 1));
		if(Mat.BARREL!=Mat.CHEST) {
			inv.setItem(3,  new ItemStack(Mat.BARREL));
		}
		p.openInventory(inv);
		LootchestCommand.editinv.put(p, name.getName());
	}
	
	public  void mainInv(Player p, String name) {
		p.closeInventory();
		LootchestCommand.editinv.put(p, name);
		String menuName = Utils.getMenuName("main", name);
		LootchestCommand.menuName.put(p, menuName);
        final Inventory inv = createInventory(menuName, 36);
        inv.setItem(4, getItem(Mat.ENDER_CHEST, Utils.getMsg("Menu.main.copychest", " ", " ")));
        if(Main.configs.PART_enable) {
        	inv.setItem(11, getItem(Mat.ENDER_EYE, Utils.getMsg("Menu.main.particles", " ", " ")));
        }
        inv.setItem(9, getItem(Mat.NOTE_BLOCK, Utils.getMsg("Menu.main.type", " ", " ")));
        inv.setItem(13, getItem(Mat.CHEST, Utils.getMsg("Menu.main.content", " ", " ")));
        inv.setItem(15, getItem(Mat.CLOCK, Utils.getMsg("Menu.main.respawnTime", " ", " ")));
        inv.setItem(22, getItem(Mat.DIAMOND, Utils.getMsg("Menu.main.chances", " ", " ")));
        Lootchest lc = Main.getInstance().getLootChest().get(name);
        inv.setItem(28, getEnabled("fall", lc.getFall()));
        inv.setItem(30, getEnabled("respawn_cmd", lc.getRespawn_cmd()));
        inv.setItem(32, getEnabled("respawn_natural", lc.getRespawn_natural()));
        inv.setItem(34, getEnabled("take_message", lc.getTake_msg()));
        
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
			return getItem(Mat.EMERALD_BLOCK, Utils.getMsg("Menu.main.disable_" + path, " ", " "));
		}else {
			return getItem(Mat.REDSTONE_BLOCK, Utils.getMsg("Menu.main.enable_" + path, " ", " "));
		}
	}
	
	

	

	
	
	public  void particleInv(Player p, Lootchest chest, int j) {
		p.closeInventory();
		LootchestCommand.editinv.put(p, chest.getName());
		String menuName = Utils.getMenuName("particles", chest.getName());
		LootchestCommand.menuName.put(p, menuName);
		int i = 0;
		int nbBoxes = 0;
		final Inventory inv = createInventory(menuName, 54);
		Collection<Particle> boxes = Main.getInstance().getParticles().values();
		//on laisse une ligne vide pour d§sactiver les particules
		if(j==1) {
			inv.setItem(4, getItem(Mat.BARRIER, "Disable particles"));
			i=9;
		}
		else {
			String name = Utils.getMsg("Menu.particles.page", "[Number]", j-1+"");
			ItemStack item = getItem(Material.PAPER,  name );
			inv.setItem(45, item);
		}
		for(Particle keys : boxes) {

			//si on est § la page j>1 et qu'on a moins de ((j-1)*52)-10) particules, on continue 
			if(j> 1 && nbBoxes < ((j-1)*52)-8) nbBoxes++;
			
			// si case 45 (bouton retour) on fait rien. sur la page 1, pas de bouton retour donc on peux quand m§me faire qq chose
			// si case 53 (bouton suivant) on fait rien. Sur la derni§re page, pas de bouton suivant.
			else if( (i!=45 || j==1) && (i!=53 || (boxes.size()-1)<=(j*52+1) ) ){

				inv.setItem(i++, getItem(keys.getMat(), keys.getReadableName()));
				if(inv.getItem(i-1) == null) {
					inv.setItem(i-1, getItem(Material.STONE, keys.getReadableName()));
				}
			}
			//bouton suivant et retour
			else if (i==45) {
				String name = Utils.getMsg("Menu.particles.page", "[Number]", j-1+"");
				ItemStack item = getItem(Material.PAPER,  name );
				inv.setItem(i++, item);
				//on peux afficher l'item suivant maintenant qu'on a fait le bouton retour
				inv.setItem(i++, getItem(keys.getMat(), keys.getReadableName()));
			}
			else if (i==53){
				String name = Utils.getMsg("Menu.particles.page", "[Number]", (j+1)+"");

				ItemStack item = getItem(Material.PAPER,  name );
				inv.setItem(i, item);
				break;

			}
		}			

		p.openInventory(inv);
        LootchestCommand.editinv.put(p, chest.getName());
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
