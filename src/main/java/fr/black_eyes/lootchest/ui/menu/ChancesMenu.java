package fr.black_eyes.lootchest.ui.menu;

import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Utils;
import fr.black_eyes.lootchest.ui.ChestUi;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ChancesMenu extends ChestUi {

	private final Lootchest chest;

	public ChancesMenu(Lootchest chest) {
		super(3, Utils.getMenuName("chances", chest.getName()));
		this.chest = chest;
	}

	@Override
	public ChestUi open(Player player) {
		loadChestItems();
		return super.open(player);
	}

	private void loadChestItems() {
		Inventory chestInv = chest.getInv();
		String lore = Utils.getMsg("Menu.chances.lore", "[Chest]", chest.getName());

		for (int i = 0; i < chestInv.getSize(); i++) {
			if (chestInv.getItem(i) == null || chestInv.getItem(i).getType() == Material.AIR) {
				continue;
			}
			ItemStack item = chestInv.getItem(i).clone();
			renameItem(item, item.getItemMeta().getDisplayName(), lore + "||" + chest.getChances()[i] + "%");
			setItem(i, item, null);
		}
	}

	@Override
	public void onClickSlot(Player player, int slot, ClickType type) {
		ItemStack item = getItem(slot);

		if (item == null || item.getType() == Material.AIR) {
			return;
		}
		ItemMeta meta = item.getItemMeta();
		if (!meta.hasLore()) {
			return;
		}
		Integer chance = chest.getChances()[slot];
		switch (type) {
			case MIDDLE:
				chance /= 2;
				break;
			case SHIFT_LEFT:
				chance += 10;
				break;
			case SHIFT_RIGHT:
				chance -= 10;
				break;
			case LEFT:
				chance += 1;
				break;
			case RIGHT:
				chance -= 1;
				break;
		}
		chance = Math.max(1, Math.min(100, chance));
		List<String> lore = meta.getLore();
		lore.set(lore.size() - 1, chance + "%");
		meta.setLore(lore);
		item.setItemMeta(meta);
		chest.setChance(slot, chance);
	}
}
