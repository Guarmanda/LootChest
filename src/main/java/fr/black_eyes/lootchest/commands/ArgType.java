package fr.black_eyes.lootchest.commands;

import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.black_eyes.lootchest.Constants;
import fr.black_eyes.lootchest.Main;
import fr.black_eyes.lootchest.Utils;

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
                    Integer.parseInt(arg);
                    return true;
                } catch (NumberFormatException e) {
                    Utils.msg(sender, "notAnInteger", "[Number]", arg);
                    return false;
                }
            case PLAYER:
                if(!Utils.getPlayersOnline().stream().map(Player::getName).collect(LinkedList::new, LinkedList::add, LinkedList::addAll).contains(arg)){
                    Utils.msg(sender, "PlayerIsNotOnline", "[Player]", arg);
                    return false;
                }
            case LOOTCHEST:
                if (!Main.getInstance().getLootChest().keySet().contains(arg)) {
                    Utils.msg(sender, "chestDoesntExist", Constants.cheststr, arg);
                    return false;
                }
            case STRING:
                return true;
            case WORLD:
                if(!Bukkit.getWorlds().stream().map(w -> w.getName()).collect(LinkedList::new, LinkedList::add, LinkedList::addAll).contains(arg)){
                    Utils.msg(sender, "worldDoesntExist", "[World]", arg);
                    return false;
                }
            default:
                return true;
        }
    }
}
