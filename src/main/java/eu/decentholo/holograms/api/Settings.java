package eu.decentholo.holograms.api;

import com.google.common.collect.ImmutableMap;

import eu.decentholo.holograms.api.utils.config.Key;
import lombok.experimental.UtilityClass;
import java.util.Map;

@UtilityClass
public class Settings {



    @Key("update-checker")
    public static boolean CHECK_FOR_UPDATES = true;
    @Key(value = "click-cooldown", min = 1, max = 300)
    public static int CLICK_COOLDOWN = 1;
    @Key("defaults.text")
    public static String DEFAULT_TEXT = "Blank Line";
    @Key("defaults.down-origin")
    public static boolean DEFAULT_DOWN_ORIGIN = false;
    @Key(value = "defaults.height.text", min = 0.0d, max = 2.5d)
    public static double DEFAULT_HEIGHT_TEXT = 0.3;
    @Key(value = "defaults.height.icon", min = 0.0d, max = 2.5d)
    public static double DEFAULT_HEIGHT_ICON = 0.6;
    @Key(value = "defaults.height.head", min = 0.0d, max = 2.5d)
    public static double DEFAULT_HEIGHT_HEAD = 0.75;
    @Key(value = "defaults.height.smallhead", min = 0.0d, max = 2.5d)
    public static double DEFAULT_HEIGHT_SMALLHEAD = 0.6;
    @Key(value = "defaults.display-range", min = 1, max = 48)
    public static int DEFAULT_DISPLAY_RANGE = 48;
    @Key(value = "defaults.update-range", min = 1, max = 48)
    public static int DEFAULT_UPDATE_RANGE = 48;
    @Key(value = "defaults.update-interval", min = 1, max = 1200)
    public static int DEFAULT_UPDATE_INTERVAL = 20;
    @Key(value = "defaults.lru-cache-size", min = 5, max = 1e4)
    public static int DEFAULT_LRU_CACHE_SIZE = 500;
    @Key("allow-placeholders-inside-animations")
    public static boolean ALLOW_PLACEHOLDERS_INSIDE_ANIMATIONS = false;

    public static Map<String, String> CUSTOM_REPLACEMENTS = ImmutableMap.<String, String>builder()
            .put("[x]", "\u2588")
            .put("[X]", "\u2588")
            .put("[/]", "\u258C")
            .put("[,]", "\u2591")
            .put("[,,]", "\u2592")
            .put("[,,,]", "\u2593")
            .put("[p]", "\u2022")
            .put("[P]", "\u2022")
            .put("[|]", "\u23B9")
            .build();



}
