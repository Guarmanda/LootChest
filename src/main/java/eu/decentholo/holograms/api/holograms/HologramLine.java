package eu.decentholo.holograms.api.holograms;

import com.google.common.util.concurrent.AtomicDouble;

import eu.decentholo.holograms.api.DecentHolograms;
import eu.decentholo.holograms.api.DecentHologramsAPI;
import eu.decentholo.holograms.api.Settings;
import eu.decentholo.holograms.api.holograms.enums.EnumFlag;
import eu.decentholo.holograms.api.holograms.enums.HologramLineType;
import eu.decentholo.holograms.api.holograms.objects.HologramObject;
import eu.decentholo.holograms.api.nms.NMS;
import eu.decentholo.holograms.api.utils.Common;
import eu.decentholo.holograms.api.utils.PAPI;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class HologramLine extends HologramObject {

    protected static final DecentHolograms DECENT_HOLOGRAMS = DecentHologramsAPI.get();



    /*
     *	Fields
     */

    private final HologramPage parent;
    private final Map<UUID, String> playerTextMap = new ConcurrentHashMap<>();
    private final Map<UUID, String> lastTextMap = new ConcurrentHashMap<>();
    private HologramLineType type;
    private int[] entityIds = new int[2];
    private final AtomicDouble offsetX = new AtomicDouble(0d);
    private final AtomicDouble offsetY = new AtomicDouble(0d);
    private final AtomicDouble offsetZ = new AtomicDouble(0d);
    private double height;
    private String content;
    private String text;


    private volatile boolean containsAnimations;
    private volatile boolean containsPlaceholders;

    /*
     *	Constructors
     */

    public HologramLine(@Nullable HologramPage parent, @NonNull Location location, @NotNull String content) {
        super(location);
        this.parent = parent;
        NMS nms = NMS.getInstance();
        this.entityIds[0] = nms.getFreeEntityId();
        this.entityIds[1] = nms.getFreeEntityId();
        this.content = content;
        this.type = HologramLineType.UNKNOWN;
        this.height = Settings.DEFAULT_HEIGHT_TEXT;
        this.parseContent();
    }




    /**
     * Parse the current content String.
     */
    public void parseContent() {
        HologramLineType prevType = type;


            type = HologramLineType.TEXT;
            if (prevType != type) {
                height = Settings.DEFAULT_HEIGHT_TEXT;
            }
            text = parseCustomReplacements();

            containsPlaceholders = PAPI.containsPlaceholders(text);
        
        setOffsetY(type.getOffsetY());
    }




    /**
     * Get the type of this line.
     *
     * @return the type of this line.
     */
    @NonNull
    public HologramLineType getType() {
        return type != null ? type : HologramLineType.UNKNOWN;
    }

    /*
     *	Visibility Methods
     */

    @NotNull
    private String getText(@NonNull Player player, boolean update) {
        if (type != HologramLineType.TEXT) {
            return "";
        }

        UUID uuid = player.getUniqueId();
        String string = playerTextMap.get(uuid);

        // Update cache
        if (update || string == null) {
            string = text == null ? "" : text;
            // Parse placeholders.
            if (!hasFlag(EnumFlag.DISABLE_PLACEHOLDERS)) {
                string = parsePlaceholders(string, player, containsPlaceholders);
            }
            // Update the cached text.
            playerTextMap.put(uuid, string);
        }

        // Parse animations
        if (containsAnimations && !hasFlag(EnumFlag.DISABLE_ANIMATIONS)) {

            // Parse placeholders.
            if (Settings.ALLOW_PLACEHOLDERS_INSIDE_ANIMATIONS && !hasFlag(EnumFlag.DISABLE_PLACEHOLDERS)) {
                // This has been done to allow the use of placeholders in animation frames.
                string = parsePlaceholders(string, player, true);
            }
        }

        return Common.colorize(string);
    }

    @NonNull
    private List<Player> getPlayers(boolean onlyViewers, Player... players) {
        List<Player> playerList;
        if (players == null || players.length == 0) {
            playerList = onlyViewers ? getViewerPlayers() : new ArrayList<>(Bukkit.getOnlinePlayers());
        } else {
            playerList = Arrays.asList(players);
        }
        return playerList;
    }

    @NotNull
    private String parsePlaceholders(@NotNull String string, @NonNull Player player, boolean papi) {
        // Replace internal placeholders.
        string = string.replace("{player}", player.getName());
        string = string.replace("{page}", String.valueOf(parent != null ? parent.getIndex() + 1 : 1));
        string = string.replace("{pages}", String.valueOf(parent != null ? parent.getParent().size() : 1));

        // Replace PlaceholderAPI placeholders.
        if (papi) {
            string = PAPI.setPlaceholders(player, string);
            if (string == null) {
                // Some PlaceholderAPI placeholders might be replaced with null, so if the line content
                // is just a single placeholder, there is a possibility that the line will be null. So,
                // if that happens, replace the null with an empty string.
                string = "";
            }
        }
        return string;
    }

    @NonNull
    // Parses custom replacements that can be defined in the config
    private String parseCustomReplacements() {
        if (!content.isEmpty()) {
            for (Map.Entry<String, String> replacement : Settings.CUSTOM_REPLACEMENTS.entrySet()) {
                content = content.replace(replacement.getKey(), replacement.getValue());
            }
        }
        return content;
    }

 
    /**
     * Update the visibility of this line for the given player. This method checks
     * if the player has the permission to see this line and if they are in the display
     * range. Then it updates the visibility accordingly.
     *
     * @param player The player to update visibility for.
     */
    public void updateVisibility(@NonNull Player player) {
        if (isVisible(player) && isInDisplayRange(player)) {
            hide(player);
        } else if (!isVisible(player) && isInDisplayRange(player)) {
            show(player);
        }
    }

    /**
     * Show this line for given players.
     *
     * @param players Given players.
     */
    public void show(Player... players) {
        if (isDisabled()) {
            return;
        }
        List<Player> playerList = getPlayers(false, players);
        NMS nms = NMS.getInstance();
        for (Player player : playerList) {
            if (player == null) {
                continue;
            }
            if (parent != null && parent.getParent().isHideState(player)) {
                continue;
            }
            if (!isVisible(player) && isInDisplayRange(player)) {

                        nms.showFakeEntityArmorStand(player, getLocation(), entityIds[0], true, true, false);
                        nms.updateFakeEntityCustomName(player, getText(player, true), entityIds[0]);
                        

                
                viewers.add(player.getUniqueId());
            }
        }
    }

    /**
     * Update this line for given players.
     *
     * @param players Given players.
     */
    public void update(Player... players) {
        if (isDisabled() || hasFlag(EnumFlag.DISABLE_UPDATING)) {
            return;
        }

        List<Player> playerList = getPlayers(true, players);
        NMS nms = NMS.getInstance();
        for (Player player : playerList) {
            if (type == HologramLineType.TEXT) {
                UUID uuid = player.getUniqueId();
                String lastText = lastTextMap.get(uuid);
                String updatedText = getText(player, true);
                if (!updatedText.equals(lastText)) {
                    lastTextMap.put(uuid, updatedText);
                    nms.updateFakeEntityCustomName(player, updatedText, entityIds[0]);
                }
            } 
        }
    }

    public void updateWithTextForAllViewers(String text){
        this.text = text;
        List<Player> playerList = getPlayers(true);
        NMS nms = NMS.getInstance();
        for (Player player : playerList) {
            if (type == HologramLineType.TEXT) {
                UUID uuid = player.getUniqueId();
                String lastText = lastTextMap.get(uuid);
                String updatedText = text;
                if (!updatedText.equals(lastText)) {
                    lastTextMap.put(uuid, updatedText);
                    playerTextMap.put(uuid, updatedText);
                    nms.updateFakeEntityCustomName(player, updatedText, entityIds[0]);
                }
            } 
        }
    }

    /**
     * Update the location of this line for given players.
     *
     * @param players Given players.
     */
    public void updateLocation(boolean updateRotation, Player... players) {
        if (isDisabled()) {
            return;
        }
        List<Player> playerList = getPlayers(true, players);
        for (Player player : playerList) {
            NMS.getInstance().teleportFakeEntity(player, getLocation(), entityIds[0]);
        }
    }


    /**
     * Hide this line for given players.
     *
     * @param players Given players.
     */
    public void hide(Player... players) {
        List<Player> playerList = getPlayers(true, players);
        for (Player player : playerList) {
            NMS.getInstance().hideFakeEntities(player, entityIds[0], entityIds[1]);
            viewers.remove(player.getUniqueId());
        }
    }

    public boolean isInDisplayRange(@NonNull Player player) {
        return parent == null || parent.getParent().isInDisplayRange(player);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isInUpdateRange(@NonNull Player player) {
        return parent == null || parent.getParent().isInUpdateRange(player);
    }

    public double getOffsetX() {
        return offsetX.get();
    }

    public double getOffsetY() {
        return offsetY.get();
    }

    public double getOffsetZ() {
        return offsetZ.get();
    }

    public void setOffsetX(double offsetX) {
        this.offsetX.set(offsetX);
    }

    public void setOffsetY(double offsetY) {
        this.offsetY.set(offsetY);
    }

    public void setOffsetZ(double offsetZ) {
        this.offsetZ.set(offsetZ);
    }

    /*
     *	Override Methods
     */

    @Override
    public boolean hasFlag(@NonNull EnumFlag flag) {
        return super.hasFlag(flag) || (parent != null && parent.getParent().hasFlag(flag));
    }

    

}
