package fr.black_eyes.lootchest.listeners;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;
import fr.black_eyes.lootchest.commands.Lootchest;

public class InventoryListeners implements Listener {
	
	
	public static HashMap<Player, String> particles = new HashMap<Player, String>();
	
	//gère la modification des coffres
	@EventHandler
    public void oncloseInventory(InventoryCloseEvent e) {
    	Player p = Bukkit.getPlayer(e.getPlayer().getName());
    	if (!Lootchest.editinv.containsKey(p)) {
    		return;
    	}
    	String chest = Lootchest.editinv.get(p);
    	Lootchest.editinv.remove(p);
        if (e.getInventory().getName().equals(Utils.getMsg("Menu.items.name", "[Chest]", chest))) {
        	Main.getInstance().getData().set("chests." +chest+ ".inventory", null);
			for(int i = 0 ; i < e.getInventory().getSize() ; i++) {
				if(e.getInventory().getItem(i) != null) {
					Main.getInstance().getData().set("chests." +chest + ".inventory." + i, e.getInventory().getItem(i));
					if(!Main.getInstance().getData().isSet("chests." + chest + ".chance." + i)){
						Main.getInstance().getData().set("chests." + chest + ".chance." + i, Main.getInstance().getConfig().getInt("default_item_chance"));
					}
				}
			}
			Utils.mainInv(p, chest);
        }
        
        else if (e.getInventory().getName().equals(Utils.getMsg("Menu.particles.name", "[Chest]", chest))) {
        	Utils.mainInv(p, chest);
        }

        else if (e.getInventory().getName().equals(Utils.getMsg("Menu.chances.name", "[Chest]", chest))) {
			for(int i = 0 ; i < e.getInventory().getSize() ; i++) {
				if(e.getInventory().getItem(i) != null) {
					List<String> lore = e.getInventory().getItem(i).getItemMeta().getLore();
					Main.getInstance().getData().set("chests." + chest + ".chance." + i, Integer.parseInt(lore.get(1).replace("%", "")));
				}
			}
			Utils.mainInv(p, chest);
        }
        
        
        else if (e.getInventory().getName().equals(Utils.getMsg("Menu.time.name", "[Chest]", chest))) {
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
			Main.getInstance().getData().set("chests." + chest + ".time", jours*24*60 + heures*60 + minutes);
			Utils.mainInv(p, chest);
			
        }
        try {
			Main.getInstance().getData().save(Main.getInstance().getDataF());
			Main.getInstance().getData().load(Main.getInstance().getDataF());
		} catch (IOException | InvalidConfigurationException ex) {
			ex.printStackTrace();
		}
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
        if (Main.getInstance().getData().getConfigurationSection("chests").getKeys(false).size() == 0) {  	
        	return;
        }
        final Player player =(Player)e.getWhoClicked();
        if(!Lootchest.editinv.containsKey(player)) {
        	return;
        }
        //particles menu
        if (e.getInventory().getName().equals(Utils.getMsg("Menu.particles.name", "[Chest]", Lootchest.editinv.get(player)))) {
        	e.setCancelled(true);
        	String particules[] = {"EXPLOSION_HUGE", "EXPLOSION_LARGE", "EXPLOSION_NORMAL", "FIREWORKS_SPARK", "WATER_BUBBLE", "SUSPENDED", "TOWN_AURA", "CRIT", "CRIT_MAGIC", "SMOKE_NORMAL", "SMOKE_LARGE", "SPELL_MOB", "SPELL_MOB_AMBIENT", "SPELL", "SPELL_INSTANT", "SPELL_WITCH", "NOTE", "PORTAL", "ENCHANTMENT_TABLE", "FLAME", "LAVA", "FOOTSTEP", "WATER_SPLASH", "WATER_WAKE", "CLOUD", "REDSTONE", "SNOWBALL", "DRIP_WATER", "DRIP_LAVA", "SNOW_SHOVEL", "SLIME", "HEART", "VILLAGER_ANGRY", "VILLAGER_HAPPY", "BARRIER"};
        	Main.getInstance().getData().set("chests." + Lootchest.editinv.get(player) + ".particle", particules[e.getSlot()]);
        	Location loc = (Location)Main.getInstance().getData().get("chests." + Lootchest.editinv.get(player) + ".location");
        	final Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
    		loc2.setX(loc.getX()+0.5);
    		loc2.setY(loc.getY()+0.5);
    		loc2.setZ(loc.getZ()+0.5);
        	for(Particle part : Main.particules) {
    			if((""+part).contains(particules[e.getSlot()])) 
    				Main.part.put(loc2, part);
    		}
    		try {
    			Main.getInstance().getData().save(Main.getInstance().getDataF());
    			Main.getInstance().getData().load(Main.getInstance().getDataF());
    		} catch (IOException | InvalidConfigurationException e1) {
    			e1.printStackTrace();
    		}
    		Utils.msg(player, "editedParticle", "[Chest]", Lootchest.editinv.get(player));
        }
        
        //main menu
        else if(e.getInventory().getName().equals(Utils.getMsg("Menu.main.name", "[chest]", Lootchest.editinv.get(player)))) {
        	e.setCancelled(true);
        	String chest = Lootchest.editinv.get(player);
        	switch(e.getSlot()) {
        		case 11:
        			if(Main.getInstance().getConfig().getBoolean("Particles.enable")) {
        				player.closeInventory();
        				Utils.particleInv(player, chest);
        			}
        			break;
        		case 13:
        			player.closeInventory();
        			Utils.invEdit(player, chest);
        			break;
        		case 15:
        			player.closeInventory();
        			Utils.invTime(player, chest);
        			break;	
        		case 22:
        			player.closeInventory();
        			Utils.invChances(player, chest);
        			break;			
        	}
        }
        else if(e.getInventory().getName().equals(Utils.getMsg("Menu.chances.name", "[Chest]", Lootchest.editinv.get(player)))) {
        	e.setCancelled(true);
        	ItemStack item = e.getCurrentItem();
        	ItemMeta meta = item.getItemMeta();
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
        else if(e.getInventory().getName().equals(Utils.getMsg("Menu.time.name", "[Chest]", Lootchest.editinv.get(player)))) {
        	e.setCancelled(true);
        	ItemStack item = e.getCurrentItem();
        	switch(e.getSlot()) {
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
    				if(item.getAmount()-1 == 0)
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
			ItemStack sign = new ItemStack(Material.SIGN, 1);
			ItemMeta meta = sign.getItemMeta(); 
			meta.setDisplayName("Respawn time: " + jours+" days, " + heures + " hours, " + minutes + " minutes.");
			sign.setItemMeta(meta);
        	inv.setItem(22, sign);
        }

    }
	
	
	
	
	
}
