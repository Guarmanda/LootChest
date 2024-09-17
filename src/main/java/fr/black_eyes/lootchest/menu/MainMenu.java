package fr.black_eyes.lootchest.menu;

import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Mat;
import fr.black_eyes.lootchest.Utils;

public class MainMenu extends ChestUi {
	
	public MainMenu(Lootchest lc, UiHandler uiHandler) {
		super(4, "--- pls insert chestname? ---");
		setItem(4, nameItem(Mat.ENDER_CHEST, Utils.getMsg("Menu.main.copychest")), p -> uiHandler.openUi(p, UiHandler.UiType.COPY, lc));
		
		if (Main.getInstance().getConfig().getBoolean("Particles.enable")) {
			setItem(11, nameItem(Mat.ENDER_EYE, Utils.getMsg("Menu.main.particles")), p -> uiHandler.openUi(p, UiHandler.UiType.PARTICLE, lc));
		}
		setItem(9, nameItem(Mat.NOTE_BLOCK, Utils.getMsg("Menu.main.type")), p -> uiHandler.openUi(p, UiHandler.UiType.TYPE, lc));
		setItem(13, nameItem(Mat.CHEST, Utils.getMsg("Menu.main.content")), p -> uiHandler.openUi(p, UiHandler.UiType.EDIT, lc));
		setItem(15, nameItem(Mat.CLOCK, Utils.getMsg("Menu.main.respawnTime")), p -> uiHandler.openUi(p, UiHandler.UiType.TIME, lc));
		setItem(22, nameItem(Mat.DIAMOND, Utils.getMsg("Menu.main.chances")), p -> uiHandler.openUi(p, UiHandler.UiType.CHANCES, lc));
		
		setItem(28, getToggleItem("fall", lc.getFall()), p -> toggleFall(lc));
		setItem(30, getToggleItem("respawn_cmd", lc.getRespawn_cmd()), p -> toggleRespawnNatural(lc));
		setItem(32, getToggleItem("respawn_natural", lc.getRespawn_natural()), p -> toggleRespawnNatural(lc));
		setItem(34, getToggleItem("take_message", lc.getTake_msg()), p -> toggleTakeMsg(lc));
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
		lc.setRespawn_cmd(!lc.getRespawn_cmd());
		changeItem(32, getToggleItem("respawn_natural", lc.getRespawn_natural()));
		lc.updateData();
	}
	
	private void toggleTakeMsg(Lootchest lc) {
		lc.setTake_msg(!lc.getTake_msg());
		setItem(34, getToggleItem("take_message", lc.getTake_msg()), p -> toggleTakeMsg(lc));
		lc.updateData();
	}
}
