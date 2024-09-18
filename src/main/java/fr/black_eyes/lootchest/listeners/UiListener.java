package fr.black_eyes.lootchest.listeners;

import fr.black_eyes.lootchest.ui.UiHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class UiListener implements Listener {

	private final UiHandler uiHandler;

	public UiListener(UiHandler uiHandler) {
		this.uiHandler = uiHandler;
	}

	@EventHandler
	public void onUiClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (uiHandler.hasPlayerOpenUi(player)) {
			event.setCancelled(true);
			uiHandler.handleClick(player, event.getSlot(), event.getClick());
		}
	}
}
