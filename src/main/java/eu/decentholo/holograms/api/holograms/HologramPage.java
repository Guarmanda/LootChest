package eu.decentholo.holograms.api.holograms;

import com.google.common.collect.ImmutableList;
import eu.decentholo.holograms.api.holograms.enums.HologramLineType;
import eu.decentholo.holograms.api.holograms.objects.FlagHolder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Location;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class HologramPage extends FlagHolder {

    /*
     *	Fields
     */

    private int index;
    private final Hologram parent;
    private final List<Integer> clickableEntityIds = new ArrayList<>();
    private final List<HologramLine> lines = new ArrayList<>();

    /*
     *	Constructors
     */

    public HologramPage(@NonNull Hologram parent, int index) {
        this.parent = parent;
        this.index = index;
    }

    /*
     *	General Methods
     */

    /**
     * Get the current parent hologram of this page.
     *
     * @return the current parent hologram of this page.
     */
    @NonNull
    public Hologram getParent() {
        return parent;
    }

    /**
     * Get height of this hologram in blocks.
     *
     * @return height of this hologram in blocks.
     */
    public double getHeight() {
        double height = 0.0D;
        for (HologramLine hologramLine : lines) {
            height += hologramLine.getHeight();
        }
        return height;
    }

    @NonNull
    public Location getCenter() {
        Location center = parent.getLocation().clone();
        if (parent.isDownOrigin()) {
            center.add(0, getHeight() / 2, 0);
        } else {
            center.subtract(0, getHeight() / 2, 0);
        }
        return center;
    }

    /**
     * Get hologram size. (Number of lines)
     *
     * @return Number of lines in this hologram.
     */
    public int size() {
        return this.lines.size();
    }



    /*
     *	Lines Methods
     */

    /**
     * Re-Align the lines in this hologram page putting them to the right place.
     * <p>
     * This method is good to use after teleporting the hologram page.
     */
    public void realignLines() {
        Location currentLocation = parent.getLocation().clone();
        if (parent.isDownOrigin()) {
            currentLocation.add(0, getHeight(), 0);
        }

        for (HologramLine line : lines) {
            Location lineLocation = line.getLocation();
            lineLocation.setX(currentLocation.getX() + line.getOffsetX());
            lineLocation.setY(currentLocation.getY() + line.getOffsetY());
            lineLocation.setZ(currentLocation.getZ() + line.getOffsetZ());

            line.setLocation(lineLocation);
            line.updateLocation(true);
            currentLocation.subtract(0, line.getHeight(), 0);
        }
    }

    /**
     * Add a new line to the bottom of this hologram page.
     *
     * @param line New line.
     * @return Boolean whether the operation was successful.
     */
    public boolean addLine(@NonNull HologramLine line) {
        lines.add(line);
        parent.getViewerPlayers(this.index).forEach(line::show);
        realignLines();
        return true;
    }

    /**
     * Insert a new line into this hologram page.
     *
     * @param index Index of the new line.
     * @param line  New line.
     * @return Boolean whether the operation was successful.
     */
    public boolean insertLine(int index, @NonNull HologramLine line) {
        if (index < 0 || index >= size()) {
            return false;
        }
        lines.add(index, line);
        parent.getViewerPlayers(this.index).forEach(line::show);
        realignLines();
        return true;
    }

    /**
     * Set new content of a line in this hologram page.
     *
     * @param index   Index of the line.
     * @param content Line's new content.
     * @return Boolean whether the operation was successful.
     */
    public boolean setLine(int index, @NonNull String content) {
        HologramLine line = getLine(index);
        if (line == null) {
            return false;
        }

        HologramLineType previousType = line.getType();

        line.setContent(content);

        if (line.getType() != previousType ) {
            line.hide();
            line.show();
            realignLines();
        }
        return true;
    }

    /**
     * Get line on a specified index in this hologram page.
     *
     * @param index Index of the line.
     * @return The HologramLine or null if it wasn't found.
     */
    public HologramLine getLine(int index) {
        if (index < 0 || index >= size()) {
            return null;
        }
        return lines.get(index);
    }

    /**
     * Remove a line from this hologram page.
     *
     * @param index Index of the line.
     * @return The removed line or null if it wasn't found.
     */
    public HologramLine removeLine(int index) {
        if (index < 0 || index >= size()) {
            return null;
        }
        HologramLine line = lines.remove(index);
        if (line != null) {
            line.destroy();
            line.hide();
            realignLines();
        }
        return line;
    }

    /**
     * Get the Location at the bottom of this hologram page that's available for a new line.
     *
     * @return the Location at the bottom of this hologram page that's available for a new line.
     */
    @NonNull
    public Location getNextLineLocation() {
        if (size() == 0) {
            return parent.getLocation().clone();
        }
        HologramLine line = lines.get(lines.size() - 1);
        return line.getLocation().clone().subtract(0, line.getHeight(), 0);
    }

    /**
     * Get the List of all lines in this hologram page.
     *
     * @return List of all lines in this hologram page.
     */
    @NonNull
    public List<HologramLine> getLines() {
        return ImmutableList.copyOf(lines);
    }

 

  

}
