package fr.black_eyes.lootchest.listeners;

import fr.black_eyes.lootchest.ui.UiHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class UiListener implements Listener {

	private final UiHandler uiHandler;

	public UiListener(UiHandler uiHandler) {
		this.uiHandler = uiHandler;
	}

	/**
	 * Delegates the click event to the UiHandler to take care of the player's
	 * interaction with the UI
	 */
	@EventHandler
	public void onUiClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (uiHandler.hasPlayerOpenUi(player)) {
			boolean doCancel = uiHandler.handleClick(player, event.getSlot(), event.getClick());
			event.setCancelled(doCancel);
		}
	}

	/**
	 * Inform the UiHandler that the player has closed the UI
	 */
	@EventHandler
	public void onUiClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		if (uiHandler.hasPlayerOpenUi(player)) {
			uiHandler.handleClose(player);
		}
	}
}
