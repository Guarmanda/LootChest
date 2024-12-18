package fr.black_eyes.lootchest.commands;

import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.simpleJavaPlugin.Utils;
import fr.black_eyes.lootchest.LootChestUtils;
import org.bukkit.generator.WorldInfo;

public enum ArgType {
    INTEGER, PLAYER, LOOTCHEST, STRING, WORLD;

    public String getName () {
        switch (this) {
            case INTEGER:
                return "integer";
            case PLAYER:
                return "player";
            case LOOTCHEST:
                return "chestName";
            case STRING:
                return "text";
            case WORLD:
                return "world";
            default:
                return "Unknown";
        }
    }   

    public boolean isValid (String arg, CommandSender sender) {
        switch (this) {
            case INTEGER:
                try {
                    Integer.valueOf(arg);
                    return true;
                } catch (NumberFormatException e) {
                    Utils.msg(sender, "notAnInteger", "[Number]", arg);
                    return false;
                }
            case PLAYER:
                if(!LootChestUtils.getPlayersOnline().stream().map(Player::getName).collect(LinkedList::new, LinkedList::add, LinkedList::addAll).contains(arg)){
                    Utils.msg(sender, "PlayerIsNotOnline", "[Player]", arg);
                    return false;
                }
                break;
            case LOOTCHEST:
                if (!Main.getInstance().getLootChest().containsKey(arg)) {
                    Utils.msg(sender, "chestDoesntExist", Constants.CHEST_PLACEHOLDER, arg);
                    return false;
                }
                break;
            case WORLD:
                if(!Bukkit.getWorlds().stream().map(WorldInfo::getName).collect(LinkedList::new, LinkedList::add, LinkedList::addAll).contains(arg)){
                    Utils.msg(sender, "worldDoesntExist", "[World]", arg);
                    return false;
                }
                break;
            default:
                return true;
        }
        return true;
    }
}
