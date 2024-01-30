package com.github.unldenis.hologram.line;

import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface ILine<T> {

  Plugin getPlugin();

  Type getType();

  int getEntityId();

  Location getLocation();

  void setLocation(Location location);

  T getObj();

  void setObj(T obj);

  void hide(Player player);

  void teleport(Player player);

  void show(Player player);

  void update(Player player);

  default void update(Collection<Player> seeingPlayers) {
    for (Player player : seeingPlayers) {
      update(player);
    }
  }

  enum Type {
    TEXT_LINE
    ;

  }
}
