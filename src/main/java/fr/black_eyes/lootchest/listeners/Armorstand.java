package fr.black_eyes.lootchest.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import fr.black_eyes.lootchest.Utils;

public class Armorstand implements Listener {
	//if an armorstand isn't visible, let's cancel all manipulations on it
	@EventHandler
	public void manipulate(org.bukkit.event.player.PlayerArmorStandManipulateEvent e)
	{
	        if(!e.getRightClicked().isVisible())
	        {
				//get block below the armorstand
				Block blockbelow = e.getRightClicked().getLocation().add(0, -1, 0).getBlock();
				if(Utils.isLootChest(blockbelow.getLocation()) !=null) e.setCancelled(true);
	        }
	}
	
	@EventHandler
	public void invincible(EntityDamageEvent e)
	{
		if(e.getEntity() instanceof org.bukkit.entity.ArmorStand && !((org.bukkit.entity.ArmorStand) e.getEntity()).isVisible()){
	            //get block below the armorstand
				Block blockbelow = e.getEntity().getLocation().add(0, -1, 0).getBlock();
				if(Utils.isLootChest(blockbelow.getLocation()) !=null) e.setCancelled(true);
	        
		}
	}
	
}
