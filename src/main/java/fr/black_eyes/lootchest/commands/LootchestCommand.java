package fr.black_eyes.lootchest.commands;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Menu;
import fr.black_eyes.lootchest.Utils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;


public class LootchestCommand {
	
	public static Map<org.bukkit.entity.Player, String> editinv = new HashMap<>();
	public static Map<org.bukkit.entity.Player, String> menuName = new HashMap<>();
	
	private Menu menu;
	
	public LootchestCommand() {
		menu = Main.getInstance().getMenu();
	}
	
	public void openMenu(Player player, String menuName) {
		editinv.put(player, menuName);
		LootchestCommand.menuName.put(player, Utils.getMsg("Menu.main.name", Constants.cheststr, menuName));
		menu.mainInv(player, menuName);
	}
	
	public static String getCardinalDirection(Player player) {
		float rotation = Utils.normalizeYaw(player.getLocation().getYaw());
		if (rotation > 135.0 || rotation <= -135.0) {
			return "NORTH";
		} else if (rotation > -135.0 && rotation < -45.0) {
			return "EAST";
		} else if (rotation >= -45.0 && rotation < 45.0) {
			return "SOUTH";
		} else if (rotation >= 45.0 && rotation <= 135.0) {
			return "WEST";
		}
		return null;
	}
}
