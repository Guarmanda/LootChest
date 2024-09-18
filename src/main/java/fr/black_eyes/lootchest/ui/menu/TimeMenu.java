package fr.black_eyes.lootchest.ui.menu;

import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Mat;
import fr.black_eyes.lootchest.Utils;
import fr.black_eyes.lootchest.ui.ChestUi;
import fr.black_eyes.lootchest.ui.UiHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TimeMenu extends ChestUi {
	
	private final Lootchest chest;
	private final UiHandler uiHandler;

	public TimeMenu(Lootchest chest, UiHandler uiHandler) {
		super(3, Utils.getMenuName("time", chest.getName()));
		this.chest = chest;
		this.uiHandler = uiHandler;

		setItem(4, new ItemStack(Mat.TOTEM_OF_UNDYING), p -> toggleTime());
		setItem(9, new ItemStack(Mat.BARRIER), p -> addTime(10, 0, 0), p -> addTime(-10, 0, 0));
		setItem(10, new ItemStack(Mat.BARRIER), p -> addTime(1, 0, 0), p -> addTime(-1, 0, 0));
		setItem(11, nameItem(Mat.STICK, ""), null);
		setItem(12, new ItemStack(Mat.BARRIER), p -> addTime(0, 10, 0), p -> addTime(0, -10, 0));
		setItem(13, new ItemStack(Mat.BARRIER), p -> addTime(0, 1, 0), p -> addTime(0, -1, 0));
		setItem(14, nameItem(Mat.STICK, ""), null);
		setItem(15, new ItemStack(Mat.BARRIER), p -> addTime(0, 0, 10), p -> addTime(0, 0, -10));
		setItem(16, new ItemStack(Mat.BARRIER), p -> addTime(0, 0, 1), p -> addTime(0, 0, -1));
		
		updateTimeDial();
	}
	
	private void toggleTime() {
		chest.setTime(chest.getTime() < 0 ? 10 : -1);
		updateTimeDial();
	}
	
	/**
	 * Adds a delta time to the chest time
	 */
	private void addTime(int dDays, int dHours, int dMinutes) {
		long time = chest.getTime();
		long days = time / 1440;
		long hours = time % 1440 / 60;
		long minutes = time % 60;
		
		days = clamp(days + dDays, 0, 99);
		hours = clamp(hours + dHours, 0, 23);
		minutes = clamp(minutes + dMinutes, 0, 60);
		chest.setTime(days * 1440 + hours * 60 + minutes);
		updateTimeDial();
	}
	
	private long clamp(long value, long min, long max) {
		return Math.max(min, Math.min(max, value));
	}
	
	/**
	 * Displays the chest's time in days, hours & minutes withe items as digits
	 */
	private void updateTimeDial() {
		long time = chest.getTime();
		changeItem(4, nameItem(Mat.TOTEM_OF_UNDYING, Utils.getMsg(time < 0 ? "Menu.time.notInfinite" : "Menu.time.infinite")));
		
		long days = time / 1440;
		long hours = time % 1440 / 60;
		long minutes = time % 60;
		displayNumber((int) days, 9, Mat.GOLD_BLOCK, "Menu.time.days");
		displayNumber((int) hours, 12, Mat.GOLD_INGOT, "Menu.time.hours");
		displayNumber((int) minutes, 15, Mat.GOLD_NUGGET, "Menu.time.minutes");
		String timeInfo = minutes < 0 ?
				"Respawn time: infinite" :
				String.format("Respawn time: %d days, %d hours, %d minutes.", days, hours, minutes);
		setItem(22, nameItem(Mat.SIGN, timeInfo), null);
	}
	
	private void displayNumber(int number, int startSlot, Material mat, String langKey) {
		int digit1 = number / 10 % 10;
		int digit2 = number % 10;
		String name = Utils.getMsg(langKey);
		changeItem(startSlot, digit1 > 0 ? nameItem(mat, name, digit1) : nameItem(Mat.BARRIER, name));
		changeItem(startSlot + 1, digit2 > 0 ? nameItem(mat, name, digit2) : nameItem(Mat.BARRIER, name));
	}

	@Override
	public void onClose(Player player) {
		uiHandler.openUi(player, UiHandler.UiType.MAIN, chest, 2);
	}   
}
