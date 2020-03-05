package fr.black_eyes.lootchest.listeners;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import fr.black_eyes.lootchest.Config;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;



public class DeleteListener extends Utils implements Listener  {
	
	Config config = Main.getConfigFiles();
	 FileConfiguration data = Main.getConfigFiles().getData();
	 FileConfiguration lang = Main.getConfigFiles().getLang();
	public static HashMap<Player, Location> openInvs = new HashMap<Player, Location>();
	//gère la destruction d'un coffre au niveau des hologrames
	
	
	
	
    /*public void executeRespawn(String key) {
    	data.set("chests." + key + ".lastreset", new Timestamp(System.currentTimeMillis()).getTime());
		config.reloadData();
		long minutes = data.getLong("chests." + key + ".time")*60*20;
		new BukkitRunnable() {       
            @Override
            public void run() {
            	restoreChest(key, false);
            }                
        }.runTaskLater(Main.getInstance(), minutes+1L);
    }*/
	
    @EventHandler
    public void clickblock(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Block b = e.getClickedBlock();
                if (b.getType().equals(Material.CHEST)){

                    if(openInvs.containsKey(p)) {
                    	openInvs.remove(p);
                    	return;
                    }
                    else {
                    	openInvs.put(p, b.getLocation());
                    }
                    
                }
        }
    }
    
    @EventHandler
    public void oncloseInventory(InventoryCloseEvent e) {
    	Inventory inv = e.getInventory();
    	Player p = Bukkit.getPlayer(e.getPlayer().getName());
    	if((isEmpty(inv) || config.getConfig().getBoolean("RemoveChestAfterFirstOpenning")) && openInvs.containsKey(p)) {
    		String keys = isLootChest(openInvs.get(p));
    		if(!keys.equals(" ")) {
    			Location loc = openInvs.get(p);
    			if((config.getConfig().getBoolean("RemoveEmptyChests") && isEmpty(inv)) || config.getConfig().getBoolean("RemoveChestAfterFirstOpenning")) {
    				inv.clear();
    				loc.getBlock().setType(Material.AIR);
    				if(data.getInt("chests."+keys+".time")==0) {
    					restoreChest(keys, false);
    				}
    			}
    			deleteholo(loc);
    			final Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
    	    	loc2.setX(loc.getX()+0.5);
    	    	loc2.setY(loc.getY()+0.5);
    	    	loc2.setZ(loc.getZ()+0.5);
    	    	if(!Bukkit.getVersion().contains("1.8")) {
    	    		Main.part.remove(loc2);
    	    	}
    	    	//executeRespawn(keys);
    		}
    	}
    	openInvs.remove(p);
    }
    
    @EventHandler
    public void onchestbreak(BlockBreakEvent e) {
    	if(e.getBlock().getType().equals(Material.CHEST)) {
    		String keys = isLootChest(e.getBlock().getLocation());
    		if(!keys.equals(" ")) {
    			Location loc = e.getBlock().getLocation();
    			deleteholo(loc);
    	        final Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
    	    	loc2.setX(loc.getX()+0.5);
    	    	loc2.setY(loc.getY()+0.5);
    	    	loc2.setZ(loc.getZ()+0.5);
    	    	if(!Bukkit.getVersion().contains("1.8")) {
    	    		Main.part.remove(loc2);
    	    	}
    	    	//executeRespawn(keys);
    			return;
    		}
    	}
    }
    
    @EventHandler
    public void chestexploded(EntityExplodeEvent e) {
    	for(Block chest : e.blockList()) {
    		if(chest.getType().equals(Material.CHEST)) {
    			String keys = isLootChest(chest.getLocation());
        		if(!keys.equals(" ")) {
        			Location loc = chest.getLocation();
        			deleteholo(loc);
        	        final Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
        	    	loc2.setX(loc.getX()+0.5);
        	    	loc2.setY(loc.getY()+0.5);
        	    	loc2.setZ(loc.getZ()+0.5);
        	    	if(!Bukkit.getVersion().contains("1.8")) {
        	    		Main.part.remove(loc2);
        	    	}
        			//executeRespawn(keys);
        			
        			return;
        		}
    		}
    	}

    }
    

    
    @EventHandler
    public void hopperPlacing(BlockPlaceEvent e) {
    	Block block = e.getBlock();
    	Block[] blocksabove = {block.getWorld().getBlockAt(block.getX(), block.getY()+1, block.getZ()), block.getWorld().getBlockAt(block.getX()+1, block.getY()+1, block.getZ()), block.getWorld().getBlockAt(block.getX()-1, block.getY()+1, block.getZ()), block.getWorld().getBlockAt(block.getX(), block.getY()+1, block.getZ()+1), block.getWorld().getBlockAt(block.getX(), block.getY()+1, block.getZ()-1)};

    	if(block.getType() == Material.HOPPER) {
    		for(Block blockabove : blocksabove) {
	    		if(!isLootChest(blockabove.getLocation()).equals(" ")) {
	    			if(config.getConfig().getBoolean("PreventHopperPlacingUnderLootChest")) {
	    				e.setCancelled(true);
	    			}
	    		}
    		}
    	}
    }
    
    
    @EventHandler
    public void hopperPistonPush(BlockPistonExtendEvent e) {
    	for(Block block : e.getBlocks()) {
    		if(block.getType() == Material.HOPPER) {
    			if(config.getConfig().getBoolean("PreventHopperPlacingUnderLootChest")) {
    				e.setCancelled(true);
    			}    			
    		}
    	}
    }
    
    @EventHandler
    public void hopperPistonGrab(BlockPistonRetractEvent e) {
    	for(Block block : e.getBlocks()) {
    		if(block.getType() == Material.HOPPER) {
    			if(Main.getConfigFiles().getConfig().getBoolean("PreventHopperPlacingUnderLootChest")) {
    				e.setCancelled(true);
    			}    			
    		}
    	}
    }

}
