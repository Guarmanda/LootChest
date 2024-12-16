package fr.black_eyes.lootchest;

public abstract class Constants {
	/**
	 * Chest name placeholder in config messages
	 */
	public static final String CHEST_PLACEHOLDER = "[Chest]";
	public static final String DATA_CHEST_PATH = "chests.";

	private Constants() {
		throw new IllegalStateException("Utility class");
	}
}
