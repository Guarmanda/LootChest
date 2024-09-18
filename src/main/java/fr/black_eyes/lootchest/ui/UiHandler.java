package fr.black_eyes.lootchest.ui;

import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.ui.menu.ChancesMenu;
import fr.black_eyes.lootchest.ui.menu.CopyMenu;
import fr.black_eyes.lootchest.ui.menu.MainMenu;
import fr.black_eyes.lootchest.ui.menu.ParticleMenu;
import fr.black_eyes.lootchest.ui.menu.TimeMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Class that creates and manages command UI that can be opened by right-clicking the maze wand.
 */
public class UiHandler {
	
	private final Map<UUID, ChestUi> playerUis;
	
	public boolean hasPlayerOpenUi(Player player) {
		return playerUis.containsKey(player.getUniqueId());
	}
	
	public enum UiType {
		MAIN, COPY, TYPE, PARTICLE, EDIT, TIME, CHANCES
	}
	
	public UiHandler() {
		this.playerUis = new HashMap<>();
		//create template ui to clone
	}
	
	public void handleClick(Player player, int slot, ClickType type) {
		UUID playerId = player.getUniqueId();
		playerUis.get(playerId).onClickSlot(player, slot, type);
	}
	
	public void openUi(Player player, UiType type, Lootchest chest) {
		UUID playerId = player.getUniqueId();
		switch (type) {
			case MAIN:
				playerUis.put(playerId, new MainMenu(chest, this).open(player));
				break;
			case COPY:
				playerUis.put(playerId, new CopyMenu(chest).open(player));
				break;
			case TYPE:
				break;
			case PARTICLE:
				playerUis.put(playerId, new ParticleMenu(chest).open(player));
				break;
			case EDIT:
				break;
			case TIME:
				playerUis.put(playerId, new TimeMenu(chest).open(player));
				break;
			case CHANCES:
				playerUis.put(playerId, new ChancesMenu(chest).open(player));
		}
	}
	
	public void remove(UUID playerId) {
		playerUis.remove(playerId);
	}
}
