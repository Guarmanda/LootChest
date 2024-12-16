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
		
		setItem(28, getToggleItem("fall", chest.isFallEnabled()), p -> toggleFall(chest));
		setItem(30, getToggleItem("respawn_cmd", chest.isRespawnCmdMsgEnabled()), p -> toggleRespawnCmd(chest));
		setItem(32, getToggleItem("respawn_natural", chest.isRespawnNaturalMsgEnabled()), p -> toggleRespawnNatural(chest));
		setItem(34, getToggleItem("take_message", chest.isTakeMsgEnabled()), p -> toggleTakeMsg(chest));
	}
	
	private void toggleFall(Lootchest lc) {
		lc.setFallEnabled(!lc.isFallEnabled());
		changeItem(28, getToggleItem("fall", lc.isFallEnabled()));
		lc.updateData();
	}
	
	private void toggleRespawnCmd(Lootchest lc) {
		lc.setRespawnCmdMsgEnabled(!lc.isRespawnCmdMsgEnabled());
		changeItem(30, getToggleItem("respawn_cmd", lc.isRespawnCmdMsgEnabled()));
		lc.updateData();
	}
	
	private void toggleRespawnNatural(Lootchest lc) {
		lc.setRespawnNaturalMsgEnabled(!lc.isRespawnNaturalMsgEnabled());
		changeItem(32, getToggleItem("respawn_natural", lc.isRespawnNaturalMsgEnabled()));
		lc.updateData();
	}
	
	private void toggleTakeMsg(Lootchest lc) {
		lc.setTakeMsgEnabled(!lc.isTakeMsgEnabled());
		setItem(34, getToggleItem("take_message", lc.isTakeMsgEnabled()), p -> toggleTakeMsg(lc));
		lc.updateData();
	}

	/**
	 * Returns an "enabled" or "disabled" item based on the state
	 */
	protected ItemStack getToggleItem(String path, boolean state) {
		if (state) {
			return nameItem(Mat.EMERALD_BLOCK, Utils.getMsg("Menu.main.disable_" + path));
		} else {
			return nameItem(Mat.REDSTONE_BLOCK, Utils.getMsg("Menu.main.enable_" + path));
		}
	}
}
