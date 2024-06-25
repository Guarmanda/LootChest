package fr.black_eyes.lootchest.listeners;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Mat;
import fr.black_eyes.lootchest.Menu;
import fr.black_eyes.lootchest.Utils;
import fr.black_eyes.lootchest.commands.LootchestCommand;
import fr.black_eyes.lootchest.particles.Particle;
import net.md_5.bungee.api.ChatColor;

public class InventoryListeners implements Listener {
	



	private  Menu menu;
	 private Main main;
	 public InventoryListeners() {
		main = Main.getInstance();
		menu = main.getMenu();
	 }

	//gère la modification des coffres
	int getLoreLine(List<String> lore) {
		for(int i =0; i<lore.size(); i++) {
			if(lore.get(i).contains("%")) {
				return i;
			}
		}
		return 1;
	}
	
	@EventHandler
    public void oncloseInventory(InventoryCloseEvent e) {
		
    	Player p = Bukkit.getPlayer(e.getPlayer().getName());
    	if (!LootchestCommand.editinv.containsKey(p)) {
    		return;
    	}
    	String chest = LootchestCommand.editinv.get(p);
    	LootchestCommand.editinv.remove(p);
		String menuName = LootchestCommand.menuName.get(p);
		LootchestCommand.menuName.remove(p);


        if (menuName.equals(Utils.getMenuName("items", chest))) {
        	Lootchest lc = Main.getInstance().getLootChest().get(chest);
			lc.setInventory(e.getInventory());
			lc.updateData();
			menu.mainInv(p, chest);
        }

        else if (menuName.equals(Utils.getMenuName("chances", chest))) {
        	Lootchest lc = Main.getInstance().getLootChest().get(chest);
			for(int i = 0 ; i < e.getInventory().getSize() ; i++) {
				if(e.getInventory().getItem(i) != null) {
					List<String> lore = e.getInventory().getItem(i).getItemMeta().getLore();
					 lc.setChance(i, Integer.parseInt(lore.get(getLoreLine(lore)).replace("%", "")));
				}
			}
			lc.updateData();
			menu.mainInv(p, chest);
        }
        
        
        else if (menuName.equals( Utils.getMenuName("time", chest))) {
        	Lootchest lc = Main.getInstance().getLootChest().get(chest);
			Inventory inv = e.getInventory();
			int[] valeurs = {0,0,0,0,0,0,0,0};
			for(int i=9; i<17; i++) {
				if(!inv.getItem(i).getType().equals(Mat.BARRIER)&& !inv.getItem(i).getType().equals(Material.STICK)) {
					valeurs[i-9] = inv.getItem(i).getAmount();
				}
			}
			int jours = Integer.parseInt(valeurs[0] + "" + valeurs[1]);
			int heures = Integer.parseInt(valeurs[3] + "" + valeurs[4]);
			int minutes = Integer.parseInt(valeurs[6] + "" + valeurs[7]);
			lc.setTime(jours*24*60 + heures*60 + minutes);
			if(inv.getItem(22).getItemMeta().getDisplayName().equals("Respawn time: infinite")) {
				lc.setTime( -1);
			}
			lc.updateData();
			menu.mainInv(p, chest);
        }else if( menuName.equals(Utils.getMenuName("type", chest))) {
			menu.mainInv(p, chest);
		}	 
        else {
			return;
		}
        //Main.getInstance().getLootChest().get(chest).spawn(true);
    }
	
	
	//g�re le menu de cr�ation du coffre
    @EventHandler
    public void onClick(final InventoryClickEvent e) {
        if (e.getInventory() == null || e.getWhoClicked() == null || e.getCurrentItem() == null || !(e.getWhoClicked() instanceof Player)) {
            return;
        }
        if (Main.getInstance().getLootChest().size() == 0) {  	
        	return;
        }
        final Player player =(Player)e.getWhoClicked();
        if(!LootchestCommand.editinv.containsKey(player)) {
        	return;
        }
		if (e.getCurrentItem().getType().equals(Material.AIR)) {return;}    

        String chestName = LootchestCommand.editinv.get(player);
		String menuName = LootchestCommand.menuName.get(player);
        //particles menu
        if (menuName.equals( Utils.getMenuName("particles", chestName))) {
			e.setCancelled(true);
        	Lootchest lc = Main.getInstance().getLootChest().get(LootchestCommand.editinv.get(player));
        	 	
        	if (e.getCurrentItem().getType().equals(Material.PAPER)) {
        		int j = Integer.parseInt(org.bukkit.ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).replaceAll("\\D+",""));
        		menu.particleInv(player, lc, j);
        		return;
        	}
        	Particle part = null;
        	if(e.getSlot() == 4 && e.getCurrentItem().getItemMeta().getDisplayName().contains("Disable")) {
        		lc.setParticle(null);
        	}
        	else {
        		String name = org.bukkit.ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
        		part = main.getParticles().get(Particle.getUnreadableName(name));
        		lc.setParticle(part);
        	}
        	Location loc = lc.getParticleLocation();
			main.getPart().put(loc, part);
			lc.updateData();
			Utils.msg(player, "editedParticle", "[Chest]", chestName);
        }
        
        
        //type menu
        else if(menuName.equals( Utils.getMenuName("type", chestName))) {
			e.setCancelled(true);
			Lootchest lc = Main.getInstance().getLootChest().get(chestName);
			lc.setType(e.getCurrentItem().getType());
			lc.updateData();
			lc.spawn( true);
			Utils.msg(player, "editedChestType", "[Chest]", chestName);
        }
        
        //copy menu
        else if (menuName.equals( Utils.getMenuName("copy", chestName))) {
			e.setCancelled(true);
        	Lootchest lc = Main.getInstance().getLootChest().get(chestName);
        	if (e.getCurrentItem().getType().equals(Material.PAPER)) {
        		int j = Integer.parseInt(org.bukkit.ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).replaceAll("\\D+",""));
        		menu.invcopy(player, lc,j);
        		return;
        	}
        	String copyChest = e.getCurrentItem().getItemMeta().getDisplayName().replace(ChatColor.GOLD.toString(), "");
        	
        	Utils.copychest(Main.getInstance().getLootChest().get(copyChest), Main.getInstance().getLootChest().get(chestName));
			Main.getInstance().getLootChest().get(chestName).updateData();
        	player.closeInventory();

        	Utils.msg(player, "copiedChest", "[Chest1]", copyChest, "[Chest2]", chestName);
        }
        //main menu
        else if(menuName.equals( Utils.getMenuName("main", chestName))) {
        	e.setCancelled(true);
        	Lootchest lc = Main.getInstance().getLootChest().get(chestName);
        	switch(e.getSlot()) {
        		case 4:
    				menu.invcopy(player, lc, 1);
        			break;
        		case 9:
        			menu.invType(player, lc);
        			break;
        		case 11:
        			if(Main.getInstance().getConfig().getBoolean("Particles.enable")) {
        				menu.particleInv(player, lc, 1);
        			}
        			break;
        		case 13:
    				menu.invEdit(player, lc);
        			break;
        		case 15:
    				menu.invTime(player, lc);
        			break;	
        		case 22:
    				menu.invChances(player, lc);
        			break;
        		case 28:
					lc.setFall(!lc.getFall());
        			e.getInventory().setItem(28, menu.getEnabled("fall", lc.getFall()));
        			lc.updateData();
        			break;
        		case 30:
					lc.setRespawn_cmd(!lc.getRespawn_cmd());
        			e.getInventory().setItem(30, menu.getEnabled("respawn_cmd", lc.getRespawn_cmd()));
        			lc.updateData();
        			break;
        		case 32:
					lc.setRespawn_natural(!lc.getRespawn_natural());
        			e.getInventory().setItem(32, menu.getEnabled("respawn_natural",lc.getRespawn_natural()));
        			lc.updateData();
        			break;
        		case 34:
					lc.setTake_msg(!lc.getTake_msg());
        			e.getInventory().setItem(34, menu.getEnabled("take_message", lc.getTake_msg()));
        			lc.updateData();
        			break;
        	}
        }
        
        else if(menuName.equals( Utils.getMenuName("chances", chestName))) {
        	e.setCancelled(true);
        	ItemStack item = e.getCurrentItem();
        	ItemMeta meta = item.getItemMeta();
        	if(item.getType() != null && item.getType() != Material.AIR) {
	        	if(meta.hasLore()) {
	        		List<String> lore = meta.getLore();
	        		Integer chance = Integer.parseInt(lore.get(getLoreLine(lore)).replaceAll("%", ""));
	        			if(e.getClick() == ClickType.MIDDLE && chance >50) {
	        				chance/=2;
	        			}
	        			else if(e.getClick() == ClickType.SHIFT_LEFT && chance <91) {
	        				chance+= 10;
	        			}
		        		else if(e.getClick() == ClickType.SHIFT_RIGHT && chance >10) {
	        				chance-= 10;
	        			}
		        		else if(e.getClick() == ClickType.LEFT && chance <100) {
	        				chance++;
	        			}
	        			else if(e.getClick() == ClickType.RIGHT && chance >1) {
	        				chance--;
	        			}
	        			
	        			lore.set(getLoreLine(lore), chance + "%");
	        			meta.setLore(lore);
	        			item.setItemMeta(meta);
	        		
	        	}
        	}
        }
        else if(menuName.equals( Utils.getMenuName("time", chestName))) {
        	e.setCancelled(true);
        	Lootchest lc = Main.getInstance().getLootChest().get(chestName);
        	ItemStack item = e.getCurrentItem();
        	switch(e.getSlot()) {
        	case 4:
        		if(e.getClick() == ClickType.LEFT) {
        			if(lc.getTime() == -1) {
        				lc.setTime(10);
        				e.getInventory().setItem(15, new ItemStack(Mat.GOLD_NUGGET));
        				e.getInventory().setItem(4, menu.getItem(Mat.TOTEM_OF_UNDYING, Utils.getMsg("Menu.time.infinite", " ", " ")));
        			}
        			else{
        				lc.setTime( -1);
	        			e.getInventory().setItem(9, new ItemStack(Mat.BARRIER));
	        			e.getInventory().setItem(10, new ItemStack(Mat.BARRIER));
	        			e.getInventory().setItem(12, new ItemStack(Mat.BARRIER));
	        			e.getInventory().setItem(13, new ItemStack(Mat.BARRIER));
	        			e.getInventory().setItem(15, new ItemStack(Mat.BARRIER));
	        			e.getInventory().setItem(16, new ItemStack(Mat.BARRIER));
	        			e.getInventory().setItem(4, menu.getItem(Mat.TOTEM_OF_UNDYING, Utils.getMsg("Menu.time.notInfinite", " ", " ")));
	        			
        			}
        		}
        		break;
    		case 9:
    			if(e.getClick() == ClickType.LEFT && item.getAmount() <3) {
    				if(!item.getType().equals(Mat.BARRIER))
    					item.setAmount(item.getAmount()+1);
    				else
    					item.setType(Material.GOLD_BLOCK);
    			}
    			else if(e.getClick() == ClickType.RIGHT && item.getAmount() > 0) {
    				if(item.getAmount()-1 == 0)
    					item.setType(Mat.BARRIER);
    				else
    					item.setAmount(item.getAmount()-1);
    			}
    			break;
    		case 10:
    			if(e.getClick() == ClickType.LEFT && item.getAmount() <9  && Integer.parseInt(e.getInventory().getItem(9).getAmount() +""+ item.getAmount() ) +1 <= 30) {
    				if(!item.getType().equals(Mat.BARRIER))
    					item.setAmount(item.getAmount()+1);
    				else
    					item.setType(Material.GOLD_BLOCK);
    			}
    			else if(e.getClick() == ClickType.RIGHT && item.getAmount() > 0) {
    				if(item.getAmount()-1 == 0)
    					item.setType(Mat.BARRIER);
    				else
    					item.setAmount(item.getAmount()-1);
    			}
    			break;
    		case 12:
    			if(e.getClick() == ClickType.LEFT && item.getAmount() <2) {
    				if(!item.getType().equals(Mat.BARRIER))
    					item.setAmount(item.getAmount()+1);
    				else
    					item.setType(Material.GOLD_INGOT);
    			}
    			else if(e.getClick() == ClickType.RIGHT && item.getAmount() > 0) {
    				if(item.getAmount()-1 == 0)
    					item.setType(Mat.BARRIER);
    				else
    					item.setAmount(item.getAmount()-1);
    			}
    			break;	
    		case 13:
    			if(e.getClick() == ClickType.LEFT && item.getAmount() <9 && Integer.parseInt(e.getInventory().getItem(12).getAmount()+""+item.getAmount() )+1  <=24) {
    				if(!item.getType().equals(Mat.BARRIER))
    					item.setAmount(item.getAmount()+1);
    				else
    					item.setType(Material.GOLD_INGOT);
    			}
    			else if(e.getClick() == ClickType.RIGHT && item.getAmount() > 0) {
    				if(item.getAmount()-1 == 0)
    					item.setType(Mat.BARRIER);
    				else
    					item.setAmount(item.getAmount()-1);
    			}
    			break;	
    		case 15:
    			if(e.getClick() == ClickType.LEFT && item.getAmount() <6) {
    				if(!item.getType().equals(Mat.BARRIER))
    					item.setAmount(item.getAmount()+1);
    				else
    					item.setType(Material.GOLD_NUGGET);
    			}
    			else if(e.getClick() == ClickType.RIGHT && item.getAmount() > 0) {
    				if(item.getAmount()-1 == 0)
    					item.setType(Mat.BARRIER);
    				else
    					item.setAmount(item.getAmount()-1);
    			}
    			break;	
    		case 16:
    			if(e.getClick() == ClickType.LEFT && item.getAmount() <9 && Integer.parseInt(e.getInventory().getItem(15).getAmount()+""+item.getAmount() ) +1 <=60) {
    				if(!item.getType().equals(Mat.BARRIER))
    					item.setAmount(item.getAmount()+1);
    				else
    					item.setType(Material.GOLD_NUGGET);
    			}
    			else if(e.getClick() == ClickType.RIGHT && item.getAmount() > 0) {
    				if(item.getAmount()-1 <1)
    					item.setType(Mat.BARRIER);
    				else
    					item.setAmount(item.getAmount()-1);
    			}
    			break;	
        	}
        	Inventory inv = e.getInventory();
        	int[] valeurs = {0,0,0,0,0,0,0,0};
			for(int i=9; i<17; i++) {
				if(!inv.getItem(i).getType().equals(Mat.BARRIER)&& !inv.getItem(i).getType().equals(Material.STICK)) {
					valeurs[i-9] = inv.getItem(i).getAmount();
				}
			}
			int jours = Integer.parseInt(valeurs[0] + "" + valeurs[1]);
			int heures = Integer.parseInt(valeurs[3] + "" + valeurs[4]);
			int minutes = Integer.parseInt(valeurs[6] + "" + valeurs[7]);
			ItemStack sign = new ItemStack(Mat.SIGN, 1);
			ItemMeta meta = sign.getItemMeta(); 
			if(lc.getTime() != -1) {
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
