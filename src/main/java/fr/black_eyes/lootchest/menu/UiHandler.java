package fr.black_eyes.lootchest.menu;

import fr.black_eyes.lootchest.Lootchest;
import org.bukkit.entity.Player;

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
	
	public void handleClick(Player player, int slot) {
		UUID playerId = player.getUniqueId();
		playerUis.get(playerId).onClickSlot(player, slot);
	}
	
	public void openUi(Player player, UiType type, Lootchest chest) {
		switch (type) {
			case MAIN:
				playerUis.put(player.getUniqueId(), new MainMenu(chest, this).open(player));
				break;
			case COPY:
				break;
			case TYPE:
				break;
			case PARTICLE:
				playerUis.put(player.getUniqueId(), new ParticleMenu(chest).open(player));
				break;
			case EDIT:
				break;
			case TIME:
				break;
		}
	}
	
	public void remove(UUID playerId) {
		playerUis.remove(playerId);
	}
}
