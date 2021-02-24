package fr.black_eyes.lootchest.listeners;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
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
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import fr.black_eyes.lootchest.BungeeChannel;
import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Mat;
import fr.black_eyes.lootchest.Utils;



public class DeleteListener implements Listener  {
	

	public static HashMap<Player, Location> openInvs;
	//g§re la destruction d'un coffre au niveau des hologrames
	
	
	

	private Main main ;
	private Utils utils;
	 
	 public DeleteListener() {
			 main = Main.getInstance();


			utils = main.getUtils();
			openInvs= new HashMap<Player, Location>();
	 }


    @EventHandler
    public void clickblock(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Block b = e.getClickedBlock();
                if (Mat.isALootChestBlock(b)){

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
    /*
    @EventHandler
    public void openInventory(InventoryOpenEvent e) {
    	Player p = (Player) e.getPlayer();
    	if(openInvs.containsKey(p)) {
    		Lootchest keys = isLootChest(openInvs.get(p));
    		if(keys != null) {
                Main.getInstance().getServer().getScheduler().runTaskLater((Plugin)Main.getInstance(), (Runnable)new Runnable() {
                    @Override
                    public void run() {
                    	
                        
                    }
                },  20L);
    			
    		}
    	}
    }*/
    
    @EventHandler
    public void oncloseInventory(InventoryCloseEvent e) {
    	Inventory inv = e.getInventory();
    	Player p = Bukkit.getPlayer(e.getPlayer().getName());
    	if((utils.isEmpty(inv) || Main.configs.RemoveChestAfterFirstOpenning) && openInvs.containsKey(p)) {
    		Lootchest keys = utils.isLootChest(openInvs.get(p));
    		if(keys != null) {
    			Location loc = openInvs.get(p);
    			if((Main.configs.RemoveEmptyChests && utils.isEmpty(inv)) || Main.configs.RemoveChestAfterFirstOpenning) {
    				inv.clear();
    				utils.deleteholo(loc);
    				loc.getBlock().breakNaturally();
    				if(keys.getTime()==0) {
    					utils.restoreChest(keys, false);
    				}
    			}
    			if(keys.getTake_msg()&&!keys.getTaken()){
    				keys.setTaken(true);
	    			String msg = Main.getInstance().getConfigFiles().getLang().getString("playerTookChest").replace("[Player]", p.getName()).replace("[Chest]", keys.getHolo()).replace("&", "§");
	    			if(Main.configs.NOTE_bungee_broadcast) {
						BungeeChannel.bungeeBroadcast(msg);
					}
					else 
	    			if(!Main.configs.NOTE_per_world_message) {
						Bukkit.broadcastMessage(msg);							
					}else {
						for(Player pl : p.getWorld().getPlayers()){
							pl.sendMessage(msg);							
							
						}
					}
    			}
    			if(!Main.configs.Show_Timer_On_Hologram) {
    				utils.deleteholo(loc);
    			}
    			final Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
    	    	loc2.setX(loc.getX()+0.5);
    	    	loc2.setY(loc.getY()+0.5);
    	    	loc2.setZ(loc.getZ()+0.5);

    	    	main.getPart().remove(loc2);



    		}
    	}
    	openInvs.remove(p);
    }
    
    @EventHandler
    public void onchestbreak(BlockBreakEvent e) {
    	if(Mat.isALootChestBlock(e.getBlock())) {
	    	if(e.isCancelled()) {
	    		return;
	    	}
    		Lootchest keys = utils.isLootChest(e.getBlock().getLocation());

    		if(keys!=null) {
    			Player p = e.getPlayer();
    			if(keys.getTake_msg() && !keys.getTaken()){
    				keys.setTaken(true);
	    			String msg = Main.getInstance().getConfigFiles().getLang().getString("playerTookChest").replace("[Player]", p.getName()).replace("[Chest]", keys.getHolo()).replace("&", "§");
	    			if(Main.configs.NOTE_bungee_broadcast) {
						BungeeChannel.bungeeBroadcast(msg);
					}
					else if(!Main.configs.NOTE_per_world_message) {
						Bukkit.broadcastMessage(msg);							
					}else {
						for(Player pl : p.getWorld().getPlayers()){
							pl.sendMessage(msg);							
							
						}
					}
    			}
    			Location loc = e.getBlock().getLocation();
    			utils.deleteholo(loc);
    	        final Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
    	    	loc2.setX(loc.getX()+0.5);
    	    	loc2.setY(loc.getY()+0.5);
    	    	loc2.setZ(loc.getZ()+0.5);
 
    	    	main.getPart().remove(loc2);

    	    	//executeRespawn(keys);
    			return;
    		}
    	}
    }
    
    @EventHandler
    public void chestexploded(EntityExplodeEvent e) {

    	for(Block chest : e.blockList()) {
    		if(Mat.isALootChestBlock(chest)) {

    	    	if(e.isCancelled()) {
    	    		return;
    	    	}
    			Lootchest keys = utils.isLootChest(chest.getLocation());
        		if(keys != null) {
        			if(Main.configs.Protect_From_Explosions) {
        				
        				BlockState state = chest.getState();
        				
        				ItemStack[] content = ((InventoryHolder)state).getInventory().getContents();
        				((InventoryHolder)state).getInventory().clear();
        				chest.setType(Material.AIR); //stop item drops
        		        
        	            int delay = 2; 
        	            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
        	                public void run() {
        	                    state.update(true, false);
        	                    ((InventoryHolder)state).getInventory().setContents(content);
        	                }
        	            }, delay);
        				return;
        			}
        			Location loc = chest.getLocation();
        			utils.deleteholo(loc);
        	        final Location loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
        	    	loc2.setX(loc.getX()+0.5);
        	    	loc2.setY(loc.getY()+0.5);
        	    	loc2.setZ(loc.getZ()+0.5);
    
        	    	main.getPart().remove(loc2);
        	    	
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
	    		if(utils.isLootChest(blockabove.getLocation()) != null) {
	    			if(Main.configs.PreventHopperPlacingUnderLootChest) {
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
    			if(Main.configs.PreventHopperPlacingUnderLootChest) {
    				e.setCancelled(true);
    			}    			
    		}
    	}
    }
    
    @EventHandler
    public void hopperPistonGrab(BlockPistonRetractEvent e) {
    	if(!org.bukkit.Bukkit.getVersion().contains("1.7")){
	    	for(Block block : e.getBlocks()) {
	    		if(block.getType() == Material.HOPPER) {
	    			if(Main.getInstance().getConfigFiles().getConfig().getBoolean("PreventHopperPlacingUnderLootChest")) {
	    				e.setCancelled(true);
	    			}    			
	    		}
	    	}
    	}
    }

}
