package fr.black_eyes.api;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.block.Block;

import fr.black_eyes.lootchest.Lootchest;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Mat;
import fr.black_eyes.lootchest.Utils;

public class LootChestAPI {

    /**
     * Generates a random name for a lootchest
     * @return
     */
    public static String generateName(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * Get all lootchests loaded within lootchest plugin
     * @return HashMap<String, Lootchest> containing all lootchests with thei name as key
     */
    public static HashMap<String, Lootchest> getAllLootChests() {
        return Main.getInstance().getLootChest();
    }

    /**
     * Get a lootchest by its name
     * @param name The name of the lootchest
     * @return The lootchest
     */
    public static Lootchest getLootChest(String name) {
        return getAllLootChests().get(name);
    }

    /**
     * Creates a lootchest from a chest block, but does not add it to the lootchest plugin.
     * @param chest A chest/barrel/trapped chest block containing some items
     * @param name The name of the lootchest
     * @return The lootchest
     */
    public static Lootchest createLootChest(Block chest, String name) {
        if (!Mat.isALootChestBlock(chest)) {
            Main.getInstance().getLogger().warning("The block is not a chest! No chest will be created.");
            return null;
        }
        if (!checkNameAvalability(name)) {
            Main.getInstance().getLogger().warning("The name is already taken! A random name will be generated.");
            name = generateName();
        }
        return new Lootchest(chest,name);
    }

    /**
     * Creates a lootchest from a chest block and adds it to the lootchest plugin.
     * @param chest A chest/barrel/trapped chest block containing some items
     * @param name The name of the lootchest
     * @return The lootchest
     */
    public static Lootchest createSavedLootChest(Block chest, String name) {
        Lootchest lc = createLootChest(chest, name);
        addLootChest(name, lc);
        return lc;
    }    

    /**
     * Add a lootchest to the lootchest plugin.
     * 
     * @param name The name of the lootchest
     * @param lc The lootchest to add
     */
    public static void addLootChest(String name, Lootchest lc) {
        getAllLootChests().put(name, lc);
    }

    /**
     * Despawns the chest and removes it from data file
     * @param name The name of the lootchest
     */
    public static void removeLootChest(String name) {
        Lootchest lc = getLootChest(name);
        lc.deleteChest();
    }

    /**
     * Despawns the chest and removes it from data file
     * @param lc The lootchest
     */
    public static void removeLootChest(Lootchest lc) {
        removeLootChest(lc.getName());
    }

    /**
     * Copies an existing lootchest to another existing lootchest
     * @param lc
     * @param newName
     */
    public static void copyToExistingChest(Lootchest lc, Lootchest secondLc) {
        Utils.copychest(lc, secondLc);
    }

    /**
     * Copies an existing lootchest to a new lootchest, specifying the name of this chest
     * @param lc
     * @param newName
     * @return
     */
    public static Lootchest copyToNewChest(Lootchest lc, String newName) {
        if (!checkNameAvalability(newName)) {
            Main.getInstance().getLogger().warning("The name is already taken! A random name will be generated.");
            newName = generateName();
        }
        return new Lootchest(lc, newName);
    }

    /**
     * Saves the lootchest in data file, but it is already done automatically on shutdown, except if the lootchest wasn't added to the lootchest plugin
     * @param lc The lootchest
     */
    public static void saveLootChest(Lootchest lc) {
        lc.updateData();
    }

    /**
     * Saves all the lootchests in data file, but it is already done automatically on shutdown
     */
    public static void saveAllLootChests() {
        Utils.saveAllChests();
    }

    private static boolean checkNameAvalability(String name) {
        return !getAllLootChests().containsKey(name);
    }
}
