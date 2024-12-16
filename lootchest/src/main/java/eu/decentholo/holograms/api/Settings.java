package eu.decentholo.holograms.api;

import com.google.common.collect.ImmutableMap;

import lombok.experimental.UtilityClass;
import java.util.Map;

@UtilityClass
public class Settings {

    public static final boolean DEFAULT_DOWN_ORIGIN = false;

    public static final double DEFAULT_HEIGHT_TEXT = 0.3;

    public static final int DEFAULT_DISPLAY_RANGE = 48;

    public static final int DEFAULT_UPDATE_RANGE = 48;

    public static final int DEFAULT_UPDATE_INTERVAL = 20;

    public static final boolean ALLOW_PLACEHOLDERS_INSIDE_ANIMATIONS = false;

    public static final Map<String, String> CUSTOM_REPLACEMENTS = ImmutableMap.<String, String>builder()
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
