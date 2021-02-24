package fr.black_eyes.lootchest.listeners;

import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
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

public class InventoryListeners implements Listener {
	



	private  Menu menu;
	private  Utils utils;
	 private Main main;
	 public InventoryListeners() {
			 main = Main.getInstance();


			menu = main.getMenu();
			utils = main.getUtils();
	 }


	
	public static HashMap<Player, String> particles = new HashMap<Player, String>();
	
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

        if (LootchestCommand.menuName.get(p).equals(utils.getMsg("Menu.items.name", "[Chest]", chest))) {
        	Lootchest lc = Main.getInstance().getLootChest().get(chest);
			lc.setInventory(e.getInventory());
			LootchestCommand.menuName.put(p, utils.getMsg("Menu.main.name", "[Chest]", chest));
			utils.updateData(lc);
			menu.mainInv(p, chest);
        }
        
        else if (LootchestCommand.menuName.get(p).equals(utils.getMsg("Menu.particles.name", "[Chest]", chest))) {
        	LootchestCommand.menuName.put(p, utils.getMsg("Menu.main.name", "[Chest]", chest));
        	
        	menu.mainInv(p, chest);
        }
        
       /* else if (Lootchest.menuName.get(p).equals( utils.getMsg("Menu.copy.name", "[Chest]", chest))) {
        	Lootchest.menuName.put(p, utils.getMsg("Menu.main.name", "[Chest]", chest));
        	mainInv(p, chest);
        }*/

        else if (LootchestCommand.menuName.get(p).equals(utils.getMsg("Menu.chances.name", "[Chest]", chest))) {
        	Lootchest lc = Main.getInstance().getLootChest().get(chest);
			for(int i = 0 ; i < e.getInventory().getSize() ; i++) {
				if(e.getInventory().getItem(i) != null) {
					List<String> lore = e.getInventory().getItem(i).getItemMeta().getLore();
					 lc.setChance(i, Integer.parseInt(lore.get(getLoreLine(lore)).replace("%", "")));
				}
			}
			utils.updateData(lc);
			LootchestCommand.menuName.put(p, utils.getMsg("Menu.main.name", "[Chest]", chest));
			menu.mainInv(p, chest);
        }
        
        
        else if (LootchestCommand.menuName.get(p).equals( utils.getMsg("Menu.time.name", "[Chest]", chest))) {
        	Lootchest lc = Main.getInstance().getLootChest().get(chest);
			Inventory inv = e.getInventory();
			int valeurs[] = {0,0,0,0,0,0,0,0};
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
			utils.updateData(lc);
			LootchestCommand.menuName.put(p, utils.getMsg("Menu.main.name", "[Chest]", chest));
			menu.mainInv(p, chest);
			
        }
        	 
        else return;
        utils.restoreChest(Main.getInstance().getLootChest().get(chest), true);
    }
	
	
	//g§re le menu de cr§ation du coffre
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
        if (Main.getInstance().getLootChest().size() == 0) {  	
        	return;
        }
        final Player player =(Player)e.getWhoClicked();
        if(!LootchestCommand.editinv.containsKey(player)) {
        	return;
        }
        //particles menu
        if (LootchestCommand.menuName.get(player).equals( utils.getMsg("Menu.particles.name", "[Chest]", LootchestCommand.editinv.get(player)))) {
        	Lootchest lc = Main.getInstance().getLootChest().get(LootchestCommand.editinv.get(player));
        	e.setCancelled(true);
    		String particules[] = {"EXPLOSION_HUGE", "EXPLOSION_LARGE", "EXPLOSION_NORMAL", "FIREWORKS_SPARK", "WATER_BUBBLE", "SUSPENDED", "TOWN_AURA", "CRIT", "CRIT_MAGIC", "SMOKE_NORMAL", "SMOKE_LARGE", "SPELL_MOB", "SPELL_MOB_AMBIENT", "SPELL", "SPELL_INSTANT", "SPELL_WITCH", "NOTE", "PORTAL", "ENCHANTMENT_TABLE", "FLAME", "LAVA", "LAVA", "WATER_SPLASH", "WATER_WAKE", "CLOUD", "SNOWBALL", "DRIP_WATER", "DRIP_LAVA", "SNOW_SHOVEL", "SLIME", "HEART", "VILLAGER_ANGRY", "VILLAGER_HAPPY", "BARRIER"};
        	if(!Bukkit.getVersion().contains("1.13") && !Bukkit.getVersion().contains("1.14") && !Bukkit.getVersion().contains("1.15")) {
        		particules[21] = "FOOTSTEP";
        	}
        	if(e.getSlot()<33) {
        		lc.setParticle(particules[e.getSlot()]);
        	}
        	else {
        		lc.setParticle("Disabled");
        	}
        	Location loc = lc.getActualLocation();
        	loc.add(0.5,0.5,0.5);
			for(Object part :main.getParticules()) {
				if(e.getSlot() < 33 && (""+part).contains(particules[e.getSlot()])) 
					main.getPart().put(loc, part);
			}
			utils.updateData(lc);
			utils.msg(player, "editedParticle", "[Chest]", LootchestCommand.editinv.get(player));
        }
        
        
        //type menu
        else if(LootchestCommand.menuName.get(player).equals( utils.getMsg("Menu.type.name", " ", " "))) {
        	if (e.getCurrentItem().getType().equals(Material.AIR)) {return;}
        	else {
        		String chest = LootchestCommand.editinv.get(player);
            	Lootchest lc = Main.getInstance().getLootChest().get(chest);
            	lc.setType(e.getCurrentItem().getType());
            	utils.updateData(lc);
            	player.closeInventory();
            	utils.restoreChest(lc, true);
            	player.sendMessage( utils.getMsg("editedChestType", "[Chest]", chest));
            	return;
        	}
        }
        
        //copy menu
        else if (LootchestCommand.menuName.get(player).equals( utils.getMsg("Menu.copy.name", " ", " "))) {
        	String chest = LootchestCommand.editinv.get(player);
        	Lootchest lc = Main.getInstance().getLootChest().get(chest);
        	if (e.getCurrentItem().getType().equals(Material.AIR)) {return;}
        	if (e.getCurrentItem().getType().equals(Material.PAPER)) {
        		int j = Integer.parseInt(org.bukkit.ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).replaceAll("\\D+",""));
        		player.closeInventory();
        		menu.invcopy(player, lc,j);
        		return;
        	}
        	String copyChest = e.getCurrentItem().getItemMeta().getDisplayName().replace("§6", "");
        	
        	utils.copychest(Main.getInstance().getLootChest().get(copyChest), Main.getInstance().getLootChest().get(chest));
        	player.closeInventory();
        	player.sendMessage( utils.getMsg("copiedChest", "[Chest1]", copyChest).replace("[Chest2]", chest));
        }
        //main menu
        else if(LootchestCommand.menuName.get(player).equals( utils.getMsg("Menu.main.name", "[chest]", LootchestCommand.editinv.get(player)))) {
        	e.setCancelled(true);
        	String chest = LootchestCommand.editinv.get(player);
        	Lootchest lc = Main.getInstance().getLootChest().get(chest);
        	switch(e.getSlot()) {
        		case 4:
        			player.closeInventory();
    				LootchestCommand.menuName.put(player, utils.getMsg("Menu.copy.name", "[Chest]", chest));
    				menu.invcopy(player, lc, 1);
        			break;
        		case 9:
        			player.closeInventory();
        			LootchestCommand.menuName.put(player, utils.getMsg("Menu.type.name", "[Chest]", chest));
        			menu.invType(player, lc);
        			break;
        		case 11:
        			if(Main.getInstance().getConfig().getBoolean("Particles.enable")) {
        				player.closeInventory();
        				LootchestCommand.menuName.put(player, utils.getMsg("Menu.particles.name", "[Chest]", chest));
        				menu.particleInv(player, chest);
        			}
        			break;
        		case 13:
        			player.closeInventory();
    				LootchestCommand.menuName.put(player, utils.getMsg("Menu.items.name", "[Chest]", chest));
    				menu.invEdit(player, lc);
        			break;
        		case 15:
        			player.closeInventory();
    				LootchestCommand.menuName.put(player, utils.getMsg("Menu.time.name", "[Chest]", chest));
    				menu.invTime(player, lc);
        			break;	
        		case 22:
        			player.closeInventory();
    				LootchestCommand.menuName.put(player, utils.getMsg("Menu.chances.name", "[Chest]", chest));
    				menu.invChances(player, lc);
        			break;
        		case 28:
        			if(lc.getFall()) {
        				lc.setFall(false);
        			}else {
        				lc.setFall(true);
        			}
        			e.getInventory().setItem(28, menu.getEnabled("fall", lc.getFall()));
        			utils.updateData(lc);
        			break;
        		case 30:
        			if(lc.getRespawn_cmd()) {
        				lc.setRespawn_cmd(false);
        			}
        			else {
        				lc.setRespawn_cmd(true);
        			}
        			e.getInventory().setItem(30, menu.getEnabled("respawn_cmd", lc.getRespawn_cmd()));
        			utils.updateData(lc);
        			break;
        		case 32:
        			if(lc.getRespawn_natural()) {
        				lc.setRespawn_natural(false);
        			}else {
        				lc.setRespawn_natural(true);
        			}
        			e.getInventory().setItem(32, menu.getEnabled("respawn_natural",lc.getRespawn_natural()));
        			utils.updateData(lc);
        			break;
        		case 34:
        			if(lc.getTake_msg()) {
        				lc.setTake_msg(false);
        			}else {
        				lc.setTake_msg(true);
        			}
        			e.getInventory().setItem(34, menu.getEnabled("take_message", lc.getTake_msg()));
        			utils.updateData(lc);
        			break;
        	}
        }
        
        else if(LootchestCommand.menuName.get(player).equals( utils.getMsg("Menu.chances.name", "[Chest]", LootchestCommand.editinv.get(player)))) {
        	e.setCancelled(true);
        	ItemStack item = e.getCurrentItem();
        	ItemMeta meta = item.getItemMeta();
        	if(item.getType() != null && item.getType() != Material.AIR) {
	        	if(meta.hasLore()) {
	        		List<String> lore = meta.getLore();
	        		Integer chance = Integer.parseInt(lore.get(getLoreLine(lore)).replaceAll("%", ""));
	        			if(e.getAction() == InventoryAction.PICKUP_HALF && chance >50) {
	        				chance-= 50;
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
        else if(LootchestCommand.menuName.get(player).equals( utils.getMsg("Menu.time.name", "[Chest]", LootchestCommand.editinv.get(player)))) {
        	e.setCancelled(true);
        	Lootchest lc = Main.getInstance().getLootChest().get(LootchestCommand.editinv.get(player));
        	ItemStack item = e.getCurrentItem();
        	switch(e.getSlot()) {
        	case 4:
        		if(e.getClick() == ClickType.LEFT) {
        			if(lc.getTime() == -1) {
        				lc.setTime(10);
        				e.getInventory().setItem(15, new ItemStack(Mat.GOLD_NUGGET));
        				e.getInventory().setItem(4, menu.getItem(Mat.TOTEM_OF_UNDYING, utils.getMsg("Menu.time.infinite", " ", " ")));
        			}
        			else{
        				lc.setTime( -1);
	        			e.getInventory().setItem(9, new ItemStack(Mat.BARRIER));
	        			e.getInventory().setItem(10, new ItemStack(Mat.BARRIER));
	        			e.getInventory().setItem(12, new ItemStack(Mat.BARRIER));
	        			e.getInventory().setItem(13, new ItemStack(Mat.BARRIER));
	        			e.getInventory().setItem(15, new ItemStack(Mat.BARRIER));
	        			e.getInventory().setItem(16, new ItemStack(Mat.BARRIER));
	        			e.getInventory().setItem(4, menu.getItem(Mat.TOTEM_OF_UNDYING, utils.getMsg("Menu.time.notInfinite", " ", " ")));
	        			
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
        	int valeurs[] = {0,0,0,0,0,0,0,0};
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
