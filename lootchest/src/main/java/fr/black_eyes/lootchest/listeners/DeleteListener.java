package fr.black_eyes.lootchest.listeners;

import java.sql.Timestamp;
import java.util.ArrayList;
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
import fr.black_eyes.simpleJavaPlugin.Utils;
import fr.black_eyes.lootchest.LootChestUtils;


public class DeleteListener implements Listener  {
	

	private static final HashMap<Player, Location> openInvs = new HashMap<>();
	//gère la destruction d'un coffre au niveau des hologrames
	
	
	

	private final Main main ;
	 
	 public DeleteListener() {
			 main = Main.getInstance();
	 }


    @EventHandler
    public void clickblock(PlayerInteractEvent e) {
        Player p = e.getPlayer();
		Block b = e.getClickedBlock();
		if(b == null)
			return;
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && Mat.isALootChestBlock(b)){
			Lootchest chest = LootChestUtils.isLootChest(b.getLocation());
			if(chest != null) {
				if(openInvs.containsKey(p)) {
					openInvs.remove(p);
				} else {
					openInvs.put(p, b.getLocation());
				}
			}
		}

		if(e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			// if chest has a protection time
			if(Main.getInstance().getProtection().get(b.getLocation()) != null) {
				//get current time
				long currentTime = (new Timestamp(System.currentTimeMillis())).getTime();
				//get time of protection
				long time = Main.getInstance().getProtection().get(b.getLocation());
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
			Lootchest chest = LootChestUtils.isLootChest(e.getClickedBlock().getLocation());
	        if(chest!=null && Main.configs.radiusWithoutMonstersForOpeningChest >0) {
	        		int cpt = 0;
	        		List<Entity> entities = p.getNearbyEntities(Main.configs.radiusWithoutMonstersForOpeningChest, Main.configs.radiusWithoutMonstersForOpeningChest, Main.configs.radiusWithoutMonstersForOpeningChest);
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
    	if((LootChestUtils.isEmpty(inv) || Main.configs.removeChestAfterFirstOpening) && openInvs.containsKey(p)) {
    		Lootchest keys = LootChestUtils.isLootChest(openInvs.get(p));
    		if(keys != null) {
    			Location loc = openInvs.get(p);
    			if((Main.configs.removeEmptyChests && LootChestUtils.isEmpty(inv)) || Main.configs.removeChestAfterFirstOpening) {
    				inv.clear();
    				keys.getHologram().remove();
    	
    				if(Main.configs.destroyNaturallyInsteadOfRemovingChest)
    					loc.getBlock().breakNaturally();
    				else
    					loc.getBlock().setType(Material.AIR);
    				if(keys.getTime()==0) {
    					keys.spawn( false);
    				}
    			}
				sendChestTakeMessageIfEnabled(keys, p);
				if(!Main.configs.timerShowTimer) {
    				keys.getHologram().remove();
    			}
    			final Location loc2 = loc.clone();
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
    		Lootchest keys = LootChestUtils.isLootChest(e.getBlock().getLocation());

    		if(keys!=null) {
    			if(!Main.configs.destroyNaturallyInsteadOfRemovingChest) {
    				e.setCancelled(true);
    				e.getBlock().setType(Material.AIR);
    			}
    				
    			Player p = e.getPlayer();
				sendChestTakeMessageIfEnabled(keys, p);
				keys.getHologram().remove();
    	        final Location loc2 = keys.getParticleLocation();
 
    	    	main.getPart().remove(loc2);
    		}
    	}
    }

	private void sendChestTakeMessageIfEnabled(Lootchest keys, Player p) {
		if(keys.isTakeMsgEnabled() && !keys.isTaken()){
			keys.setTaken(true);
			String msg = Utils.color(Main.getInstance().getConfigFiles().getLang().getString("playerTookChest").replace("[Player]", p.getName()).replace("[Chest]", keys.getHolo()));
			if(Main.configs.noteBungeeBroadcast) {
				BungeeChannel.bungeeBroadcast(msg);
			}
			else if(!Main.configs.notePerWorldMessage) {
				LootChestUtils.broadcast(msg);
			}else {
				for(Player pl : p.getWorld().getPlayers()){
					pl.sendMessage(msg);
				}
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
    			Lootchest keys = LootChestUtils.isLootChest(chest.getLocation());
        		if(keys != null) {

        			if(Main.configs.protectFromExplosions) {
        				
        				BlockState state = chest.getState();
        				
        				ItemStack[] content = ((InventoryHolder)state).getInventory().getContents();
        				((InventoryHolder)state).getInventory().clear();
        				chest.setType(Material.AIR); //stop item drops
        		        
        	            int delay = 2; 
        	            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                            state.update(true, false);
                            ((InventoryHolder)state).getInventory().setContents(content);
                        }, delay);
        				return;
        			}
        			if(!Main.configs.destroyNaturallyInsteadOfRemovingChest) {
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
	    		if(LootChestUtils.isLootChest(blockabove.getLocation()) != null && Main.configs.preventHopperPlacingUnderLootChest) {
	    				e.setCancelled(true);
	    			
	    		}
    		}
    	}
    }
    
    
    @EventHandler
    public void hopperPistonPush(BlockPistonExtendEvent e) {
    	for(Block block : e.getBlocks()) {
    		if(block.getType() == Material.HOPPER && Main.configs.preventHopperPlacingUnderLootChest) {
    			e.setCancelled(true);			
    		}
    	}
    }
    
    @EventHandler
    public void hopperPistonGrab(BlockPistonRetractEvent e) {
    	if(Main.getCompleteVersion()>=1080){
	    	for(Block block : e.getBlocks()) {
	    		if(block.getType() == Material.HOPPER && Main.configs.preventHopperPlacingUnderLootChest) {
	    				e.setCancelled(true);
	    			   			
	    		}
	    	}
    	}
    }

	// if a chest is placed around a lootchest, the event has to be canceled
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Block block = e.getBlock();
		if (Mat.isALootChestBlock(block)){
			for(Block b : getBlocksInRadius(e.getBlock(), 1)) {
				if (LootChestUtils.isLootChest(b.getLocation()) != null) {
					e.setCancelled(true);
					return;
				}
			}
		}
	}

	public List<Block> getBlocksInRadius(Block start, int radius){
		ArrayList<Block> blocks = new ArrayList<>();
		for(double x = start.getLocation().getX() - radius; x <= start.getLocation().getX() + radius; x++){
			for(double y = start.getLocation().getY() - radius; y <= start.getLocation().getY() + radius; y++){
				for(double z = start.getLocation().getZ() - radius; z <= start.getLocation().getZ() + radius; z++){
					Location loc = new Location(start.getWorld(), x, y, z);
					blocks.add(loc.getBlock());
				}
			}
		}
		return blocks;
	}


}
