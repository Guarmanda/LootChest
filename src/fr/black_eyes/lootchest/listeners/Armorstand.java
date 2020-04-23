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
	
/*	@EventHandler
	public void invincible(PlayerInteractEvent e)
	{
		PotionEffect pe = new PotionEffect(PotionEffectType.LEVITATION, 20, 5);
		if(e.getAction() == Action.LEFT_CLICK_AIR) {
			e.getPlayer().addPotionEffect(pe);
		}
		else if(e.getAction() == Action.RIGHT_CLICK_AIR) {
			e.getPlayer().removePotionEffect(PotionEffectType.LEVITATION);
		}
	}*/
	
}
