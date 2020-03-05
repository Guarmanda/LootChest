package fr.black_eyes.lootchest.listeners;

import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.black_eyes.lootchest.Config;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Mat;
import fr.black_eyes.lootchest.Utils;
import fr.black_eyes.lootchest.commands.LootchestCommand;

public class InventoryListeners extends Utils implements Listener {
	

	Config config = Main.getConfigFiles();
	 FileConfiguration data = Main.getConfigFiles().getData();
	 FileConfiguration lang = Main.getConfigFiles().getLang();
	public static HashMap<Player, String> particles = new HashMap<Player, String>();
	
	//gère la modification des coffres
	@EventHandler
    public void oncloseInventory(InventoryCloseEvent e) {
    	Player p = Bukkit.getPlayer(e.getPlayer().getName());
    	if (!LootchestCommand.editinv.containsKey(p)) {
    		return;
    	}
    	String chest = LootchestCommand.editinv.get(p);
    	LootchestCommand.editinv.remove(p);
        if (LootchestCommand.menuName.get(p).equals(getMsg("Menu.items.name", "[Chest]", chest))) {
        	data.set("chests." +chest+ ".inventory", null);
			for(int i = 0 ; i < e.getInventory().getSize() ; i++) {
				if(e.getInventory().getItem(i) != null) {
					data.set("chests." +chest + ".inventory." + i, e.getInventory().getItem(i));
					if(!data.isSet("chests." + chest + ".chance." + i)){
						data.set("chests." + chest + ".chance." + i, Main.getInstance().getConfig().getInt("default_item_chance"));
					}
				}
			}
			LootchestCommand.menuName.put(p, getMsg("Menu.main.name", "[Chest]", chest));
			mainInv(p, chest);
        }
        
        else if (LootchestCommand.menuName.get(p).equals(getMsg("Menu.particles.name", "[Chest]", chest))) {
        	LootchestCommand.menuName.put(p, getMsg("Menu.main.name", "[Chest]", chest));
        	mainInv(p, chest);
        }
        
       /* else if (Lootchest.menuName.get(p).equals(getMsg("Menu.copy.name", "[Chest]", chest))) {
        	Lootchest.menuName.put(p, getMsg("Menu.main.name", "[Chest]", chest));
        	mainInv(p, chest);
        }*/

        else if (LootchestCommand.menuName.get(p).equals(getMsg("Menu.chances.name", "[Chest]", chest))) {
			for(int i = 0 ; i < e.getInventory().getSize() ; i++) {
				if(e.getInventory().getItem(i) != null) {
					List<String> lore = e.getInventory().getItem(i).getItemMeta().getLore();
					data.set("chests." + chest + ".chance." + i, Integer.parseInt(lore.get(1).replace("%", "")));
				}
			}
			LootchestCommand.menuName.put(p, getMsg("Menu.main.name", "[Chest]", chest));
			mainInv(p, chest);
        }
        
        
        else if (LootchestCommand.menuName.get(p).equals(getMsg("Menu.time.name", "[Chest]", chest))) {
			Inventory inv = e.getInventory();
			int valeurs[] = {0,0,0,0,0,0,0,0};
			for(int i=9; i<17; i++) {
				if(!inv.getItem(i).getType().equals(Material.BARRIER)&& !inv.getItem(i).getType().equals(Material.STICK)) {
					valeurs[i-9] = inv.getItem(i).getAmount();
				}
			}
			int jours = Integer.parseInt(valeurs[0] + "" + valeurs[1]);
			int heures = Integer.parseInt(valeurs[3] + "" + valeurs[4]);
			int minutes = Integer.parseInt(valeurs[6] + "" + valeurs[7]);
			data.set("chests." + chest + ".time", jours*24*60 + heures*60 + minutes);
			if(inv.getItem(22).getItemMeta().getDisplayName().equals("Respawn time: infinite")) {
				data.set("chests." + chest + ".time", -1);
			}
			LootchestCommand.menuName.put(p, getMsg("Menu.main.name", "[Chest]", chest));
			mainInv(p, chest);
			
        }
        else return;
        config.reloadData();
    }
	
	
	//gère le menu de création du coffre
    @EventHandler
    public void onClick(final InventoryClickEvent e) {
        if (e.getInventory() == null) {
            return;
        }
        if (e.getWhoClicked() == null) {
            return;
        }
        if (e.getCurrentItem() == null) {
            return;
        }
        if (!(e.getWhoClicked() instanceof Player)) {
        	return;
        }
        if (data.getConfigurationSection("chests").getKeys(false).size() == 0) {  	
        	return;
        }
        final Player player =(Player)e.getWhoClicked();
        if(!LootchestCommand.editinv.containsKey(player)) {
        	return;
        }
        //particles menu
        if (LootchestCommand.menuName.get(player).equals(getMsg("Menu.particles.name", "[Chest]", LootchestCommand.editinv.get(player)))) {
        	e.setCancelled(true);
    		String particules[] = {"EXPLOSION_HUGE", "EXPLOSION_LARGE", "EXPLOSION_NORMAL", "FIREWORKS_SPARK", "WATER_BUBBLE", "SUSPENDED", "TOWN_AURA", "CRIT", "CRIT_MAGIC", "SMOKE_NORMAL", "SMOKE_LARGE", "SPELL_MOB", "SPELL_MOB_AMBIENT", "SPELL", "SPELL_INSTANT", "SPELL_WITCH", "NOTE", "PORTAL", "ENCHANTMENT_TABLE", "FLAME", "LAVA", "LAVA", "WATER_SPLASH", "WATER_WAKE", "CLOUD", "SNOWBALL", "DRIP_WATER", "DRIP_LAVA", "SNOW_SHOVEL", "SLIME", "HEART", "VILLAGER_ANGRY", "VILLAGER_HAPPY", "BARRIER"};
        	if(!Bukkit.getVersion().contains("1.13") && !Bukkit.getVersion().contains("1.14") && !Bukkit.getVersion().contains("1.15")) {
        		particules[21] = "FOOTSTEP";
        	}
        	data.set("chests." + LootchestCommand.editinv.get(player) + ".particle", particules[e.getSlot()]);
        	Location loc = getPosition(LootchestCommand.editinv.get(player));
        	final Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
    		loc2.setX(loc.getX()+0.5);
    		loc2.setY(loc.getY()+0.5);
    		loc2.setZ(loc.getZ()+0.5);
    		if(!Bukkit.getVersion().contains("1.8")) {
    			for(Object part : Main.particules) {
    				if((""+part).contains(particules[e.getSlot()])) 
    					Main.part.put(loc2, (org.bukkit.Particle) part);
    			}
    		}
    		config.reloadData();
    		msg(player, "editedParticle", "[Chest]", LootchestCommand.editinv.get(player));
        }
        
        //copy menu
        else if (LootchestCommand.menuName.get(player).equals(getMsg("Menu.copy.name", " ", " "))) {
        	String chest = LootchestCommand.editinv.get(player);
        	if (e.getCurrentItem().getType().equals(Material.AIR)) {return;}
        	if (e.getCurrentItem().getType().equals(Material.PAPER)) {
        		int j = Integer.parseInt(org.bukkit.ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).replaceAll("\\D+",""));
        		player.closeInventory();
        		invcopy(player, chest,j);
        		return;
        	}
        	String copyChest = e.getCurrentItem().getItemMeta().getDisplayName().replace("§6", "");
        	
        	copychest(copyChest, chest);
        	player.closeInventory();
        	player.sendMessage(getMsg("copiedChest", "[Chest1]", copyChest).replace("[Chest2]", chest));
        }
        //main menu
        else if(LootchestCommand.menuName.get(player).equals(getMsg("Menu.main.name", "[chest]", LootchestCommand.editinv.get(player)))) {
        	e.setCancelled(true);
        	String chest = LootchestCommand.editinv.get(player);
        	switch(e.getSlot()) {
        		case 4:
        			player.closeInventory();
    				LootchestCommand.menuName.put(player, getMsg("Menu.copy.name", "[Chest]", chest));
        			invcopy(player, chest, 1);
        			break;
        		case 11:
        			if(Main.getInstance().getConfig().getBoolean("Particles.enable")) {
        				player.closeInventory();
        				LootchestCommand.menuName.put(player, getMsg("Menu.particles.name", "[Chest]", chest));
        				particleInv(player, chest);
        			}
        			break;
        		case 13:
        			player.closeInventory();
    				LootchestCommand.menuName.put(player, getMsg("Menu.items.name", "[Chest]", chest));
        			invEdit(player, chest);
        			break;
        		case 15:
        			player.closeInventory();
    				LootchestCommand.menuName.put(player, getMsg("Menu.time.name", "[Chest]", chest));
        			invTime(player, chest);
        			break;	
        		case 22:
        			player.closeInventory();
    				LootchestCommand.menuName.put(player, getMsg("Menu.chances.name", "[Chest]", chest));
        			invChances(player, chest);
        			break;			
        	}
        }
        else if(LootchestCommand.menuName.get(player).equals(getMsg("Menu.chances.name", "[Chest]", LootchestCommand.editinv.get(player)))) {
        	e.setCancelled(true);
        	ItemStack item = e.getCurrentItem();
        	ItemMeta meta = item.getItemMeta();
        	if(item.getType() != null && item.getType() != Material.AIR) {
	        	if(meta.hasLore()) {
	        		List<String> lore = meta.getLore();
	        		Integer chance = Integer.parseInt(lore.get(1).replaceAll("%", ""));
	        			if(e.getClick() == ClickType.LEFT && chance <100) {
	        				chance++;
	        			}
	        			else if(e.getClick() == ClickType.RIGHT && chance >1) {
	        				chance--;
	        			}
	        			
	        			lore.set(1, chance + "%");
	        			meta.setLore(lore);
	        			item.setItemMeta(meta);
	        		
	        	}
        	}
        }
        else if(LootchestCommand.menuName.get(player).equals(getMsg("Menu.time.name", "[Chest]", LootchestCommand.editinv.get(player)))) {
        	e.setCancelled(true);
        	ItemStack item = e.getCurrentItem();
        	switch(e.getSlot()) {
        	case 4:
        		if(e.getClick() == ClickType.LEFT) {
        			data.set("chests."+ LootchestCommand.editinv.get(player)+".time", -1);
        			e.getInventory().setItem(9, new ItemStack(Material.BARRIER));
        			e.getInventory().setItem(10, new ItemStack(Material.BARRIER));
        			e.getInventory().setItem(12, new ItemStack(Material.BARRIER));
        			e.getInventory().setItem(13, new ItemStack(Material.BARRIER));
        			e.getInventory().setItem(15, new ItemStack(Material.BARRIER));
        			e.getInventory().setItem(16, new ItemStack(Material.BARRIER));
        		}
        		break;
    		case 9:
    			if(e.getClick() == ClickType.LEFT && item.getAmount() <3) {
    				if(!item.getType().equals(Material.BARRIER))
    					item.setAmount(item.getAmount()+1);
    				else
    					item.setType(Material.GOLD_BLOCK);
    			}
    			else if(e.getClick() == ClickType.RIGHT && item.getAmount() > 0) {
    				if(item.getAmount()-1 == 0)
    					item.setType(Material.BARRIER);
    				else
    					item.setAmount(item.getAmount()-1);
    			}
    			break;
    		case 10:
    			if(e.getClick() == ClickType.LEFT && item.getAmount() <9  && Integer.parseInt(e.getInventory().getItem(9).getAmount() +""+ item.getAmount() ) +1 <= 30) {
    				if(!item.getType().equals(Material.BARRIER))
    					item.setAmount(item.getAmount()+1);
    				else
    					item.setType(Material.GOLD_BLOCK);
    			}
    			else if(e.getClick() == ClickType.RIGHT && item.getAmount() > 0) {
    				if(item.getAmount()-1 == 0)
    					item.setType(Material.BARRIER);
    				else
    					item.setAmount(item.getAmount()-1);
    			}
    			break;
    		case 12:
    			if(e.getClick() == ClickType.LEFT && item.getAmount() <2) {
    				if(!item.getType().equals(Material.BARRIER))
    					item.setAmount(item.getAmount()+1);
    				else
    					item.setType(Material.GOLD_INGOT);
    			}
    			else if(e.getClick() == ClickType.RIGHT && item.getAmount() > 0) {
    				if(item.getAmount()-1 == 0)
    					item.setType(Material.BARRIER);
    				else
    					item.setAmount(item.getAmount()-1);
    			}
    			break;	
    		case 13:
    			if(e.getClick() == ClickType.LEFT && item.getAmount() <9 && Integer.parseInt(e.getInventory().getItem(12).getAmount()+""+item.getAmount() )+1  <=24) {
    				if(!item.getType().equals(Material.BARRIER))
    					item.setAmount(item.getAmount()+1);
    				else
    					item.setType(Material.GOLD_INGOT);
    			}
    			else if(e.getClick() == ClickType.RIGHT && item.getAmount() > 0) {
    				if(item.getAmount()-1 == 0)
    					item.setType(Material.BARRIER);
    				else
    					item.setAmount(item.getAmount()-1);
    			}
    			break;	
    		case 15:
    			if(e.getClick() == ClickType.LEFT && item.getAmount() <6) {
    				if(!item.getType().equals(Material.BARRIER))
    					item.setAmount(item.getAmount()+1);
    				else
    					item.setType(Material.GOLD_NUGGET);
    			}
    			else if(e.getClick() == ClickType.RIGHT && item.getAmount() > 0) {
    				if(item.getAmount()-1 == 0)
    					item.setType(Material.BARRIER);
    				else
    					item.setAmount(item.getAmount()-1);
    			}
    			break;	
    		case 16:
    			if(e.getClick() == ClickType.LEFT && item.getAmount() <9 && Integer.parseInt(e.getInventory().getItem(15).getAmount()+""+item.getAmount() ) +1 <=60) {
    				if(!item.getType().equals(Material.BARRIER))
    					item.setAmount(item.getAmount()+1);
    				else
    					item.setType(Material.GOLD_NUGGET);
    			}
    			else if(e.getClick() == ClickType.RIGHT && item.getAmount() > 0) {
    				if(item.getAmount()-1 <1)
    					item.setType(Material.BARRIER);
    				else
    				item.setAmount(item.getAmount()-1);
    			}
    			break;	
        	}
        	Inventory inv = e.getInventory();
        	int valeurs[] = {0,0,0,0,0,0,0,0};
			for(int i=9; i<17; i++) {
				if(!inv.getItem(i).getType().equals(Material.BARRIER)&& !inv.getItem(i).getType().equals(Material.STICK)) {
					valeurs[i-9] = inv.getItem(i).getAmount();
				}
			}
			int jours = Integer.parseInt(valeurs[0] + "" + valeurs[1]);
			int heures = Integer.parseInt(valeurs[3] + "" + valeurs[4]);
			int minutes = Integer.parseInt(valeurs[6] + "" + valeurs[7]);
			ItemStack sign = new ItemStack(Mat.SIGN, 1);
			ItemMeta meta = sign.getItemMeta(); 
			if(data.getInt("chests."+ LootchestCommand.editinv.get(player)+".time") != -1) {
				meta.setDisplayName("Respawn time: " + jours+" days, " + heures + " hours, " + minutes + " minutes.");
			}
			else {
				meta.setDisplayName("Respawn time: infinite");
			}
			sign.setItemMeta(meta);
        	inv.setItem(22, sign);
        }

    }
	
	
	
	
	
}
