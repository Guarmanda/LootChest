package fr.black_eyes.lootchest.listeners;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
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
	
	
	private long isProtected(Block b) {
		if(Main.getInstance().getProtection().get(b.getLocation()) != null) {
			//get current time
			long currentTime = (new Timestamp(System.currentTimeMillis())).getTime();
			//get time of protection
			long time = Main.getInstance().getProtection().get(b.getLocation());
			//if time is not over
			if (currentTime < time) {
				return time - currentTime;
			} else {
				//remove protection
				Main.getInstance().getProtection().remove(b.getLocation());
				return 0;
			}
		}
		return 0;
	}


    @EventHandler
    public void clickblock(PlayerInteractEvent e) {
        Player p = e.getPlayer();
		//if the guy didn't click on it, let's skip it
		if(e.getAction() != Action.LEFT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		Block b = e.getClickedBlock();
		if(b == null)
			return;
		// check if is a lootChest
		Lootchest chest = LootChestUtils.isLootChest(b.getLocation());
		if(chest == null) return;

		// if chest has a protection time
		long protectionTime = isProtected(e.getClickedBlock());
		if(protectionTime > 0) {
			//cancel event
			e.setCancelled(true);
			//send message
			Utils.msg(p, "CantBreakBlockBecauseProtected", "[Time]", ""+protectionTime/1000);
			return;
		}

		//if player has to fight monsters first to get the chest
		if(Main.configs.radiusWithoutMonstersForOpeningChest >0) {
			// count monster number around the player
			int cpt = 0;
			List<Entity> entities = p.getNearbyEntities(Main.configs.radiusWithoutMonstersForOpeningChest, Main.configs.radiusWithoutMonstersForOpeningChest, Main.configs.radiusWithoutMonstersForOpeningChest);
			for(Entity ent: entities) {
				if(ent instanceof Monster) {
					cpt++;
				}
			}
			if(cpt !=0) {
				e.setCancelled(true);
				Utils.msg(p, "CantOpenLootchestBecauseMonster", "[Number]", cpt+"");
				return;
			}
		}

		if (e.getAction() == Action.RIGHT_CLICK_BLOCK ){
			if(openInvs.containsKey(p)) {
				openInvs.remove(p);
			} else {
				openInvs.put(p, b.getLocation());
			}
		}


    }
   
    
    @EventHandler
    public void oncloseInventory(InventoryCloseEvent e) {
    	Inventory inv = e.getInventory();
    	Player p = (Player) e.getPlayer();
		// if current player has openned a lootchest inv
    	if(openInvs.containsKey(p)) {
			Location loc = openInvs.get(p).clone();
			// we still check if the inv is a lootchest
    		Lootchest key = LootChestUtils.isLootChest(loc);
			openInvs.remove(p);
			if(key == null) return;
			if((Main.configs.removeEmptyChests && LootChestUtils.isEmpty(inv)) || Main.configs.removeChestAfterFirstOpening) {
				// if we should break chest naturally, drop an item of key.getType() at the location of the chest
				if(Main.configs.destroyNaturallyInsteadOfRemovingChest)
					loc.getWorld().dropItemNaturally(loc, new ItemStack(key.getType()));
				key.despawn();
				key.spawn( false);
			}
			if(LootChestUtils.isEmpty(inv)) {
				sendChestTakeMessageIfEnabled(key, p);
			}

    	}
    }
    
    @EventHandler
    public void onChestBreak(BlockBreakEvent e) {
		Block block = e.getBlock();
    	if(Mat.isALootChestBlock(block)) {
			Lootchest key = LootChestUtils.isLootChest(block.getLocation());
	    	if(e.isCancelled() || key == null) {
	    		return;
	    	}
			if(isProtected(block) > 0) {
				e.setCancelled(true);
				return;
			}
			e.setCancelled(true);
			Container container = (Container) block.getState();
			// drop all items on the ground
			for(ItemStack item : container.getInventory().getContents()) {
				if(item != null && item.getType() != Material.AIR) {
					block.getLocation().getWorld().dropItemNaturally(block.getLocation(), item);
				}
			}
			if(Main.configs.destroyNaturallyInsteadOfRemovingChest) {
				block.getLocation().getWorld().dropItemNaturally(block.getLocation(), new ItemStack(key.getType()));
			}
			key.despawn();
			key.spawn( false);

			Player p = e.getPlayer();
			sendChestTakeMessageIfEnabled(key, p);
    	}
    }

	private void sendChestTakeMessageIfEnabled(Lootchest keys, Player p) {
		if(keys.isTakeMsgEnabled() && !keys.isTaken()){
			keys.setTaken(true);
			String msg = Utils.color(Objects.requireNonNull(Main.getInstance().getConfigFiles().getLang().getString("playerTookChest")).replace("[Player]", p.getName()).replace("[Chest]", keys.getHolo()));
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
    public void chestExploded(EntityExplodeEvent e) {
		if(e.isCancelled())
			return;
    	for(Block chest : e.blockList()) {
    		if(Mat.isALootChestBlock(chest)) {
    			Lootchest key = LootChestUtils.isLootChest(chest.getLocation());
				if(key != null) {
					e.blockList().remove(chest);
					if (Main.configs.protectFromExplosions) {
						return;
					}
					if (Main.configs.destroyNaturallyInsteadOfRemovingChest) {
						chest.getLocation().getWorld().dropItemNaturally(chest.getLocation(), new ItemStack(key.getType()));
					}
					key.despawn();
					key.spawn(false);
				}
				return;
			}
		}
	}


    

    
    @EventHandler
    public void hopperPlacing(BlockPlaceEvent e) {
    	Block block = e.getBlock();
    	if(block.getType() == Material.HOPPER) {
    		for(Block blockAround : getBlocksInRadius(e.getBlock(), 1)) {
	    		if(LootChestUtils.isLootChest(blockAround.getLocation()) != null && Main.configs.preventHopperPlacingUnderLootChest) {
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
