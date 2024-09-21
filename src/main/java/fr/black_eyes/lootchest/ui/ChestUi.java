package fr.black_eyes.lootchest.ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;

/**
 * A custom inventory UI that allows for actions to be assigned to each slot.
 */
public class ChestUi {

	@Getter
	private final int rows;
	private final Inventory inventory;
	private final Map<Integer, Consumer<Player>> clickActions;
	private final Map<Integer, Consumer<Player>> rightClickActions;

	public ChestUi(int rows, String title) {
		this.rows = rows;
		inventory = Bukkit.createInventory(null, rows * 9, title);
		clickActions = new HashMap<>();
		rightClickActions = new HashMap<>();
	}

	/**
	 * Sets the item in the specified slot and assigns actions to be executed when the item is clicked.
	 * @param leftClickAction the action to be executed when the item is left clicked
	 * @param rightClickAction the action to be executed when the item is right clicked
	 */
	public void setItem(int slot, ItemStack item, Consumer<Player> leftClickAction, Consumer<Player> rightClickAction) {
		if (slot < 0 || slot >= rows * 9) {
			throw new IllegalArgumentException(String.format("%d is outside 0 and %d inventory slots", slot, rows * 9 - 1));
		}
		inventory.setItem(slot, item);
		clickActions.put(slot, leftClickAction);
		rightClickActions.put(slot, rightClickAction);
	}

	/**
	 * Sets the item in the specified slot and assigns an action to be executed when the item is clicked with left or right
	 * @param clickAction the action to be executed when the item is clicked
	 */
	public void setItem(int slot, ItemStack item, Consumer<Player> clickAction) {
		setItem(slot, item, clickAction, null);
	}

	protected ItemStack getItem(int slot) {
		if (slot < 0 || slot >= rows * 9) {
			throw new IllegalArgumentException(String.format("%d is outside 0 and %d inventory slots", slot, rows * 9 - 1));
		}
		return inventory.getItem(slot);
	}

	/**
	 * Changes the item in the specified slot without changing the click actions.
	 */
	public void changeItem(int slot, ItemStack item) {
		inventory.setItem(slot, item);
	}

	protected ItemStack[] getContents() {
		return inventory.getContents();
	}

	protected void setContents(ItemStack[] contents) {
		inventory.setContents(contents);
	}

	/**
	 * Called when the player clicks on a slot in the inventory to execute the action assigned to that slot.
	 * @return true if the click event should be cancelled
	 */
	public boolean onClickSlot(Player player, int slot, ClickType type) {
		//execute right click action if explicitly listed
		if (rightClickActions.containsKey(slot)) {
			if (type == ClickType.RIGHT) {
				rightClickActions.get(slot).accept(player);
			} else if (type == ClickType.LEFT) {
				clickActions.get(slot).accept(player);
			}
			//otherwise do action with any mouse click
		} else if (clickActions.containsKey(slot)) {
			clickActions.get(slot).accept(player);
		}
		return true;
	}

	/**
	 * Called when the player closes the inventory.
	 */
	public void onClose(Player player) {}

	public ChestUi open(Player player) {
		player.openInventory(inventory);
		return this;
	}

	public void clear() {
		inventory.clear();
		clickActions.clear();
	}

	protected ItemStack nameItem(Material mat, String name) {
		return nameItem(mat, name, 1, "");
	}

	protected ItemStack nameItem(Material mat, String name, int amount) {
		return nameItem(mat, name, amount, "");
	}

	protected ItemStack nameItem(Material mat, String name, int amount, String lore) {
		return renameItem(new ItemStack(mat, amount), name, lore);
	}

	protected ItemStack renameItem(ItemStack item, String name, String lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);

		if (!lore.isEmpty()) {
			List<String> loreList = Arrays.asList(lore.split("\\|\\|"));
			meta.setLore(loreList);
		}
		item.setItemMeta(meta);
		return item;
	}
}
