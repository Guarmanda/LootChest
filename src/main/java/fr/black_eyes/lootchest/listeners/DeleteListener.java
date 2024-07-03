package fr.black_eyes.lootchest.listeners;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
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
	

	private static HashMap<Player, Location> openInvs = new HashMap<>();
	//g�re la destruction d'un coffre au niveau des hologrames
	
	
	

	private Main main ;
	 
	 public DeleteListener() {
			 main = Main.getInstance();
	 }


    @EventHandler
    public void clickblock(PlayerInteractEvent e) {
        Player p = e.getPlayer();
		
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Block b = e.getClickedBlock();
                if (Mat.isALootChestBlock(b)){
					Lootchest chest = Utils.isLootChest(e.getClickedBlock().getLocation());
					if(chest != null) {
						if(openInvs.containsKey(p)) {
							openInvs.remove(p);
						}
						else {
							openInvs.put(p, b.getLocation());
						}
					}
                }
        }
        if(e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			// if chest has a protection time
			if(Main.getInstance().getProtection().get(e.getClickedBlock().getLocation()) != null) {
				//get current time
				long currentTime = (new Timestamp(System.currentTimeMillis())).getTime();
				//get time of protection
				long time = Main.getInstance().getProtection().get(e.getClickedBlock().getLocation());
				//if time is not over
				if(currentTime < time) {
					//cancel event
					e.setCancelled(true);
					//send message
					Utils.msg(p, "CantBreakBlockBecauseProtected", "[Time]", ""+(time - currentTime)/1000);

				}else{
					//remove protection
					Main.getInstance().getProtection().remove(e.getClickedBlock().getLocation());
				}
			}
			//if player has to fight monsters first to get the chest
			Lootchest chest = Utils.isLootChest(e.getClickedBlock().getLocation());
	        if(chest!=null && Main.configs.Radius_Without_Monsters_For_Opening_Chest >0) {
	        		int cpt = 0;
	        		List<Entity> entities = p.getNearbyEntities(Main.configs.Radius_Without_Monsters_For_Opening_Chest, Main.configs.Radius_Without_Monsters_For_Opening_Chest, Main.configs.Radius_Without_Monsters_For_Opening_Chest);
	        		for(Entity ent: entities) {
	        			if(ent instanceof Monster) cpt++;
	        		}
	        		if(cpt !=0) {
	        			e.setCancelled(true);
	        			Utils.msg(p, "CantOpenLootchestBecauseMonster", "[Number]", cpt+"");
	        		
	        		}
	        }
        }
    }
   
    
    @EventHandler
    public void oncloseInventory(InventoryCloseEvent e) {
    	Inventory inv = e.getInventory();
    	Player p = Bukkit.getPlayer(e.getPlayer().getName());
    	if((Utils.isEmpty(inv) || Main.configs.RemoveChestAfterFirstOpenning) && openInvs.containsKey(p)) {
    		Lootchest keys = Utils.isLootChest(openInvs.get(p));
    		if(keys != null) {
    			Location loc = openInvs.get(p);
    			if((Main.configs.RemoveEmptyChests && Utils.isEmpty(inv)) || Main.configs.RemoveChestAfterFirstOpenning) {
    				inv.clear();
    				keys.getHologram().remove();
    	
    				if(Main.configs.Destroy_Naturally_Instead_Of_Removing_Chest)
    					loc.getBlock().breakNaturally();
    				else
    					loc.getBlock().setType(Material.AIR);
    				if(keys.getTime()==0) {
    					keys.spawn( false);
    				}
    			}
    			if(keys.getTake_msg()&&!keys.getTaken()){
    				keys.setTaken(true);
	    			String msg = Main.getInstance().getConfigFiles().getLang().getString("playerTookChest").replace("[Player]", p.getName()).replace("[Chest]", keys.getHolo()).replace("&", "�");
	    			if(Main.configs.NOTE_bungee_broadcast) {
						BungeeChannel.bungeeBroadcast(msg);
					}
					else 
	    			if(!Main.configs.NOTE_per_world_message) {
						Utils.broadcast(msg);							
					}else {
						for(Player pl : p.getWorld().getPlayers()){
							pl.sendMessage(msg);							
							
						}
					}
    			}
    			if(!Main.configs.TIMER_Show_Timer) {
    				keys.getHologram().remove();
    		
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
			if(Main.getInstance().getProtection().get(e.getBlock().getLocation()) != null) {
				//get current time
				long currentTime = (new Timestamp(System.currentTimeMillis())).getTime();
				//get time of protection
				long time = Main.getInstance().getProtection().get(e.getBlock().getLocation());
				//if time is not over
				if(currentTime < time) {
					//cancel event
					e.setCancelled(true);
				}else{
					//remove protection
					Main.getInstance().getProtection().remove(e.getBlock().getLocation());
				}
			}
    		Lootchest keys = Utils.isLootChest(e.getBlock().getLocation());

    		if(keys!=null) {
    			if(!Main.configs.Destroy_Naturally_Instead_Of_Removing_Chest) {
    				e.setCancelled(true);
    				e.getBlock().setType(Material.AIR);
    			}
    				
    			Player p = e.getPlayer();
    			if(keys.getTake_msg() && !keys.getTaken()){
    				keys.setTaken(true);
	    			String msg = Main.getInstance().getConfigFiles().getLang().getString("playerTookChest").replace("[Player]", p.getName()).replace("[Chest]", keys.getHolo()).replace("&", "�");
	    			if(Main.configs.NOTE_bungee_broadcast) {
						BungeeChannel.bungeeBroadcast(msg);
					}
					else if(!Main.configs.NOTE_per_world_message) {
						Utils.broadcast(msg);							
					}else {
						for(Player pl : p.getWorld().getPlayers()){
							pl.sendMessage(msg);								
						}
					}
    			}
    			keys.getHologram().remove();
    	        final Location loc2 = keys.getParticleLocation();
 
    	    	main.getPart().remove(loc2);
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
    			Lootchest keys = Utils.isLootChest(chest.getLocation());
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
        			if(!Main.configs.Destroy_Naturally_Instead_Of_Removing_Chest) {
        				chest.setType(Material.AIR);
        			}
        			keys.getHologram().remove();
        	        final Location loc2 = keys.getParticleLocation();
    
        	    	main.getPart().remove(loc2);
        			
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
	    		if(Utils.isLootChest(blockabove.getLocation()) != null && Main.configs.PreventHopperPlacingUnderLootChest) {
	    				e.setCancelled(true);
	    			
	    		}
    		}
    	}
    }
    
    
    @EventHandler
    public void hopperPistonPush(BlockPistonExtendEvent e) {
    	for(Block block : e.getBlocks()) {
    		if(block.getType() == Material.HOPPER && Main.configs.PreventHopperPlacingUnderLootChest) {
    			e.setCancelled(true);			
    		}
    	}
    }
    
    @EventHandler
    public void hopperPistonGrab(BlockPistonRetractEvent e) {
    	if(Main.getVersion()>7){
	    	for(Block block : e.getBlocks()) {
	    		if(block.getType() == Material.HOPPER && Main.configs.PreventHopperPlacingUnderLootChest) {
	    				e.setCancelled(true);
	    			   			
	    		}
	    	}
    	}
    }

	// if a chest is placed around a lootchest, the event has to be canceled
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Block block = e.getBlock();
		if (Mat.isALootChestBlock(block)) {
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					for (int z = -1; z <= 1; z++) {
						Block b = block.getRelative(x, y, z);
						Lootchest lc = Utils.isLootChest(b.getLocation());
						if (lc != null && !(x==0 && z==0)) {
							if(lc.getProtectionTime() != 0) {
								e.setCancelled(true);
								return;
							}
						}
					}
				}
			}
		}
	}


}
