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
     * Move a hologram to the given location.
     *
     * @param name     The holograms name.
     * @param location The location.
     * @throws IllegalArgumentException If hologram or location is null.
     */
    public static void moveHologram(String name, Location location) throws IllegalArgumentException {
        Validate.notNull(name);
        Validate.notNull(location);

        Hologram hologram = getHologram(name);
        if (hologram != null) {
            moveHologram(hologram, location);
        }
    }

    /**
     * Move a hologram to the given location.
     *
     * @param hologram The hologram.
     * @param location The location.
     * @throws IllegalArgumentException If hologram or location is null.
     */
    public static void moveHologram(Hologram hologram, Location location) throws IllegalArgumentException {
        Validate.notNull(hologram);
        Validate.notNull(location);

        Location hologramLocation = hologram.getLocation();
        hologramLocation.setWorld(location.getWorld());
        hologramLocation.setX(location.getX());
        hologramLocation.setY(location.getY());
        hologramLocation.setZ(location.getZ());
        hologram.setLocation(hologramLocation);
        hologram.realignLines();

    }

    /**
     * Update the given hologram for all viewers.
     *
     * @param name The holograms name.
     */
    public static void updateHologram(String name) {
        Validate.notNull(name);

        Hologram hologram = getHologram(name);
        if (hologram != null) {
            hologram.updateAll();
        }
    }

    /**
     * Remove a hologram by its name.
     * <p>
     * The removed hologram will also get its file deleted.
     * </p>
     *
     * @param name The name.
     */
    public static void removeHologram(String name) {
        Validate.notNull(name);

        Hologram hologram = getHologram(name);
        if (hologram != null) {
            hologram.delete();
        }
    }

    /**
     * Get hologram by name.
     *
     * @param name The name.
     * @return The hologram.
     * @throws IllegalArgumentException If the name is null.
     */
    @Nullable
    public static Hologram getHologram(String name) throws IllegalArgumentException {
        Validate.notNull(name);
        return Hologram.getCachedHologram(name);
    }





    /**
     * Add a new line into hologram.
     *
     * @param hologram The hologram.
     * @param content  New lines content.
     * @return The new line.
     * @throws IllegalArgumentException If hologram or content is null.
     */
    public static HologramLine addHologramLine(Hologram hologram, String content) throws IllegalArgumentException {
        return addHologramLine(hologram, 0, content);
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
     * Insert a new line on the specified index into hologram page.
     *
     * @param hologram  The hologram.
     * @param lineIndex Index of the new line.
     * @param content   New lines content.
     * @return The new line.
     * @throws IllegalArgumentException If hologram or content is null or the indexes are invalid.
     */
    public static HologramLine insertHologramLine(Hologram hologram, int lineIndex, String content) throws IllegalArgumentException {
        return insertHologramLine(hologram, 0, lineIndex, content);
    }

    /**
     * Insert a new line on the specified index into hologram page.
     *
     * @param hologram  The hologram.
     * @param pageIndex Index of the hologram page.
     * @param lineIndex Index of the new line.
     * @param content   New lines content.
     * @return The new line.
     * @throws IllegalArgumentException If hologram or content is null or the indexes are invalid.
     */
    public static HologramLine insertHologramLine(Hologram hologram, int pageIndex, int lineIndex, String content) throws IllegalArgumentException {
        Validate.notNull(hologram);
        HologramPage page = hologram.getPage(pageIndex);
        if (page == null) {
            throw new IllegalArgumentException("Given page index is out of bounds for the hologram.");
        }
        return insertHologramLine(page, lineIndex, content);
    }

    /**
     * Insert a new line on the specified index into hologram page.
     *
     * @param page    The page.
     * @param index   Index of the new line.
     * @param content New lines content.
     * @return The new line.
     * @throws IllegalArgumentException If the page or content is null or the indexes are invalid.
     */
    public static HologramLine insertHologramLine(HologramPage page, int index, String content) throws IllegalArgumentException {
        HologramLine oldLine = page.getLine(index);
        if (oldLine == null) {
            throw new IllegalArgumentException("Given line index is out of bounds for the hologram page.");
        }
        HologramLine line = new HologramLine(page, oldLine.getLocation().clone(), content);
        page.insertLine(index, line);
  
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
