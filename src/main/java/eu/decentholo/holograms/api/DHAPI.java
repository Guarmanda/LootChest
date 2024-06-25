package eu.decentholo.holograms.api;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.decentholo.holograms.api.holograms.Hologram;
import eu.decentholo.holograms.api.holograms.HologramLine;
import eu.decentholo.holograms.api.holograms.HologramPage;
import eu.decentholo.holograms.api.holograms.enums.HologramLineType;
import eu.decentholo.holograms.api.utils.Common;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple access point to the DecentHolograms API.
 * Using this class, you can manipulate with holograms and their contents.
 *
 * @author d0by
 * @since 2.0.12
 */
@SuppressWarnings("unused")
@UtilityClass
public final class DHAPI {

    /**
     * Create a new hologram with the given name on the specified location.
     *
     * @param name     The name.
     * @param location The location.
     * @return The new hologram.
     * @throws IllegalArgumentException If name or location is null or hologram with the given name already exists.
     */
    public static Hologram createHologram(String name, Location location) throws IllegalArgumentException {
        return createHologram(name, location, false);
    }

    /**
     * Create a new hologram with the given name on the specified location.
     *
     * @param name       The name.
     * @param location   The location.
     * @param saveToFile Boolean: Should the hologram be saved into file?
     * @return The new hologram.
     * @throws IllegalArgumentException If name or location is null or hologram with the given name already exists.
     */
    public static Hologram createHologram(String name, Location location, boolean saveToFile) throws IllegalArgumentException {
        return createHologram(name, location, saveToFile, new ArrayList<>());
    }

    /**
     * Create a new hologram with the given name on the specified location with the given lines.
     *
     * @param name     The name.
     * @param location The location.
     * @param lines    The lines of this hologram.
     * @return The new hologram.
     * @throws IllegalArgumentException If name or location is null or hologram with the given name already exists.
     */
    public static Hologram createHologram(String name, Location location, List<String> lines) throws IllegalArgumentException {
        return createHologram(name, location, false, lines);
    }

    /**
     * Create a new hologram with the given name on the specified location with the given lines.
     *
     * @param name       The name.
     * @param location   The location.
     * @param saveToFile Boolean: Should the hologram be saved into file?
     * @param lines      The lines of this hologram.
     * @return The new hologram.
     * @throws IllegalArgumentException If name or location is null or hologram with the given name already exists
     *                                  or if the name contains invalid characters.
     */
    public static Hologram createHologram(String name, Location location, boolean saveToFile, List<String> lines) throws IllegalArgumentException {
        Validate.notNull(name);
        Validate.notNull(location);

        if (!name.matches(Common.NAME_REGEX)) {
            throw new IllegalArgumentException(String.format("Hologram name can only contain alphanumeric characters, underscores and dashes! (%s)", name));
        }

        if (Hologram.getCachedHologramNames().contains(name)) {
            throw new IllegalArgumentException(String.format("Hologram with that name already exists! (%s)", name));
        }

        Hologram hologram = new Hologram(name, location, saveToFile);
        HologramPage page = hologram.getPage(0);
        if (lines != null) {
            for (String line : lines) {
                HologramLine hologramLine = new HologramLine(page, page.getNextLineLocation(), line);
                page.addLine(hologramLine);
            }
        }
        hologram.showAll();

        return hologram;
    }

    /**
     * Add a new line into hologram page.
     *
     * @param hologram  The hologram.
     * @param pageIndex Index of the page.
     * @param content   New lines content.
     * @return The new line.
     * @throws IllegalArgumentException If hologram or content is null or the indexes are invalid.
     */
    public static HologramLine addHologramLine(Hologram hologram, int pageIndex, String content) throws IllegalArgumentException {
        Validate.notNull(hologram);
        Validate.notNull(content);
        HologramPage page = hologram.getPage(pageIndex);
        if (page == null) {
            throw new IllegalArgumentException("Given page index is out of bounds for the hologram.");
        }
        return addHologramLine(page, content);
    }

    /**
     * Add a new line into the hologram page.
     *
     * @param page    The page.
     * @param content New lines content.
     * @return The new line.
     * @throws IllegalArgumentException If page or content is null.
     */
    public static HologramLine addHologramLine(HologramPage page, String content) throws IllegalArgumentException {
        HologramLine line = new HologramLine(page, page.getNextLineLocation(), content);
        page.addLine(line);
 
        return line;
    }


    /**
     * Remove a line from hologram page.
     *
     * @param hologram  The hologram.
     * @param lineIndex Index of the line.
     * @return The removed hologram line.
     * @throws IllegalArgumentException If hologram is null or the indexes are invalid.
     */
    @Nullable
    public static HologramLine removeHologramLine(Hologram hologram, int lineIndex) throws IllegalArgumentException {
        return removeHologramLine(hologram, 0, lineIndex);
    }

    /**
     * Remove a line from hologram page.
     *
     * @param hologram  The hologram.
     * @param pageIndex Index of the page.
     * @param lineIndex Index of the line.
     * @return The removed hologram line.
     * @throws IllegalArgumentException If hologram is null or the indexes are invalid.
     */
    @Nullable
    public static HologramLine removeHologramLine(Hologram hologram, int pageIndex, int lineIndex) throws IllegalArgumentException {
        Validate.notNull(hologram);
        HologramPage page = hologram.getPage(pageIndex);
        if (page == null) {
            throw new IllegalArgumentException("Given page index is out of bounds for the hologram.");
        }
        HologramLine line = page.removeLine(lineIndex);
  
        return line;
    }






}
