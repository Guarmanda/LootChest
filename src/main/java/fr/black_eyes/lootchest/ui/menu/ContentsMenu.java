package fr.black_eyes.lootchest.ui.menu;

import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Utils;
import fr.black_eyes.lootchest.ui.ChestUi;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ContentsMenu extends ChestUi {

	private final Lootchest chest;

	public ContentsMenu(Lootchest chest) {
		super(3, Utils.getMenuName("items", chest.getName()));
		this.chest = chest;
	}

	@Override
	public ChestUi open(Player player) {
		setContents(chest.getInv().getContents());
		return super.open(player);
	}

	@Override
	public boolean onClickSlot(Player player, int slot, ClickType type) {
		return false;
	}

	@Override
	public void onClose() {
		Inventory inv = Bukkit.createInventory(null, 27, "");
		inv.setContents(getContents());
		chest.setInventory(inv);
		chest.updateData();
	}
}
