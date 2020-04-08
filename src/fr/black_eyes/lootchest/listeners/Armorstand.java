package fr.black_eyes.lootchest.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class Armorstand implements Listener {
	//if an armorstand isn't visible, let's cancel all manipulations on it
	@EventHandler
	public void manipulate(org.bukkit.event.player.PlayerArmorStandManipulateEvent e)
	{
	        if(!e.getRightClicked().isVisible())
	        {
	            e.setCancelled(true);
	        }
	}
	
	@EventHandler
	public void invincible(EntityDamageEvent e)
	{
		if(e.getEntity() instanceof org.bukkit.entity.ArmorStand) {
	        if(!((org.bukkit.entity.ArmorStand) e.getEntity()).isVisible())
	        {
	            e.setCancelled(true);
	        }
		}
	}
	
}
