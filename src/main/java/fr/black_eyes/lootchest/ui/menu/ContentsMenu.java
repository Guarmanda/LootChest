package fr.black_eyes.lootchest.ui.menu;

import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Utils;
import fr.black_eyes.lootchest.ui.ChestUi;
import fr.black_eyes.lootchest.ui.UiHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

/**
 * A menu to add or remove items from a loot chest
 */
public class ContentsMenu extends ChestUi {

	private final Lootchest chest;
	private final UiHandler uiHandler;

	public ContentsMenu(Lootchest chest, UiHandler uiHandler) {
		super(3, Utils.getMenuName("items", chest.getName()));
		this.chest = chest;
		this.uiHandler = uiHandler;
	}

	/**
	 * Displays the chest contents in the UI on open
	 */
	@Override
	public ChestUi open(Player player) {
		setContents(chest.getInv().getContents());
		return super.open(player);
	}

	/**
	 * Lets player move items around in the chest
	 */
	@Override
	public boolean onClickSlot(Player player, int slot, ClickType type) {
		return false;
	}

	@Override
	public void onClose(Player player) {
		Inventory inv = Bukkit.createInventory(null, 27, "");
		inv.setContents(getContents());
		chest.setInventory(inv);
		chest.updateData();
		uiHandler.openUi(player, UiHandler.UiType.MAIN, chest, 2);
	}
}
