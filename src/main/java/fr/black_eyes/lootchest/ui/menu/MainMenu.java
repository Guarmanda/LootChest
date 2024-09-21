package fr.black_eyes.lootchest.ui.menu;

import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Mat;
import fr.black_eyes.lootchest.LootChestUtils;
import fr.black_eyes.lootchest.ui.ChestUi;
import fr.black_eyes.lootchest.ui.UiHandler;
import fr.black_eyes.simpleJavaPlugin.Utils;

import org.bukkit.inventory.ItemStack;

/**
 * The main menu for a loot chest with items to open other menus and toggle settings
 */
public class MainMenu extends ChestUi {
	
	public MainMenu(Lootchest chest, UiHandler uiHandler) {
		super(4, LootChestUtils.getMenuName("main", chest.getName()));
		setItem(4, nameItem(Mat.ENDER_CHEST, Utils.getMsg("Menu.main.copychest")), p -> uiHandler.openUi(p, UiHandler.UiType.COPY, chest));
		
		if (Main.getInstance().getConfig().getBoolean("Particles.enable")) {
			setItem(11, nameItem(Mat.ENDER_EYE, Utils.getMsg("Menu.main.particles")), p -> uiHandler.openUi(p, UiHandler.UiType.PARTICLE, chest));
		}
		setItem(9, nameItem(Mat.NOTE_BLOCK, Utils.getMsg("Menu.main.type")), p -> uiHandler.openUi(p, UiHandler.UiType.TYPE, chest));
		setItem(13, nameItem(Mat.CHEST, Utils.getMsg("Menu.main.content")), p -> uiHandler.openUi(p, UiHandler.UiType.EDIT, chest));
		setItem(15, nameItem(Mat.CLOCK, Utils.getMsg("Menu.main.respawnTime")), p -> uiHandler.openUi(p, UiHandler.UiType.TIME, chest));
		setItem(22, nameItem(Mat.DIAMOND, Utils.getMsg("Menu.main.chances")), p -> uiHandler.openUi(p, UiHandler.UiType.CHANCES, chest));
		
		setItem(28, getToggleItem("fall", chest.getFall()), p -> toggleFall(chest));
		setItem(30, getToggleItem("respawn_cmd", chest.getRespawn_cmd()), p -> toggleRespawnCmd(chest));
		setItem(32, getToggleItem("respawn_natural", chest.getRespawn_natural()), p -> toggleRespawnNatural(chest));
		setItem(34, getToggleItem("take_message", chest.getTake_msg()), p -> toggleTakeMsg(chest));
	}
	
	private void toggleFall(Lootchest lc) {
		lc.setFall(!lc.getFall());
		changeItem(28, getToggleItem("fall", lc.getFall()));
		lc.updateData();
	}
	
	private void toggleRespawnCmd(Lootchest lc) {
		lc.setRespawn_cmd(!lc.getRespawn_cmd());
		changeItem(30, getToggleItem("respawn_cmd", lc.getRespawn_cmd()));
		lc.updateData();
	}
	
	private void toggleRespawnNatural(Lootchest lc) {
		lc.setRespawn_natural(!lc.getRespawn_natural());
		changeItem(32, getToggleItem("respawn_natural", lc.getRespawn_natural()));
		lc.updateData();
	}
	
	private void toggleTakeMsg(Lootchest lc) {
		lc.setTake_msg(!lc.getTake_msg());
		setItem(34, getToggleItem("take_message", lc.getTake_msg()), p -> toggleTakeMsg(lc));
		lc.updateData();
	}

	/**
	 * Returns an "enabled" or "disabled" item based on the state
	 */
	protected ItemStack getToggleItem(String path, Boolean state) {
		if (state) {
			return nameItem(Mat.EMERALD_BLOCK, Utils.getMsg("Menu.main.disable_" + path));
		} else {
			return nameItem(Mat.REDSTONE_BLOCK, Utils.getMsg("Menu.main.enable_" + path));
		}
	}
}
