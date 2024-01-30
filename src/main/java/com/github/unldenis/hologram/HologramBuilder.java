package com.github.unldenis.hologram;

import com.github.unldenis.hologram.line.ILine;
import com.github.unldenis.hologram.line.ITextLine;
import com.github.unldenis.hologram.line.Line;
import com.github.unldenis.hologram.line.TextLine;
import com.github.unldenis.hologram.line.hologram.IHologramLoader;
import com.github.unldenis.hologram.line.hologram.TextBlockStandardLoader;
import com.github.unldenis.hologram.placeholder.Placeholders;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

public class HologramBuilder {

  private final Plugin plugin;
  private final Hologram hologram;
  private final List<ILine<?>> lines;

  // no placeholder
  private Placeholders placeholders = new Placeholders(0x00);

  protected HologramBuilder(Plugin plugin, Location location) {
    this.plugin = plugin;
    this.hologram = new Hologram(plugin, location, new TextBlockStandardLoader());
    this.lines = new LinkedList<>();
  }

  public HologramBuilder placeholders(Placeholders placeholders) {
    // update already added lines
    for (ILine<?> line : lines) {
      switch (line.getType()) {
        case TEXT_LINE ->
            ((ITextLine) line).getPlaceholders().add(placeholders);
      }
    }
    //
    this.placeholders = placeholders;
    return this;
  }

  public HologramBuilder addLine(String text) {
    Line line = new Line(plugin);
    TextLine textLine = new TextLine(line, text, placeholders);
    lines.add(textLine);
    return this;
  }

  public HologramBuilder loader(IHologramLoader loader) {
    this.hologram.setLoader(loader);
    return this;
  }

  public HologramBuilder name(String name) {
    this.hologram.setName(name);
    return this;
  }

  public Hologram build() {
    return hologram;
  }

  public Hologram loadAndBuild(IHologramPool pool) {
    this.hologram.load(lines.toArray(new ILine[0]));
    pool.takeCareOf(hologram);

    return hologram;
  }

}
