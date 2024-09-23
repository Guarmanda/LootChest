package eu.decentholo.holograms.api.utils.items;

import com.cryptomorin.xseries.XMaterial;
import eu.decentholo.holograms.api.utils.reflect.ReflectMethod;
import eu.decentholo.holograms.api.utils.reflect.ReflectionUtil;
import eu.decentholo.holograms.api.utils.reflect.Version;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@UtilityClass
public final class DecentMaterial {

    private static final Map<String, String> MATERIAL_ALIASES = new HashMap<>();
    private static final ReflectMethod MATERIAL_IS_ITEM_METHOD;

    private static final Pattern SPACING_CHARS_REGEX;

    static {
        SPACING_CHARS_REGEX = Pattern.compile("[_ \\-]+");
    }
    /**
     * Remove spacing characters from the given string.
     *
     * <p>Spacing characters: ' ', '-', '_'</p>
     *
     * @param string The string.
     * @return The string without spacing characters.
     */
    public static String removeSpacingChars(String string) {
        if (string == null) {
            return null;
        }
        return SPACING_CHARS_REGEX.matcher(string).replaceAll("");
    }

    static {
        for (Material material : Material.values()) {
            MATERIAL_ALIASES.put(removeSpacingChars(material.name()).toLowerCase(), material.name());
        }

        if (Version.before(13)) {
            MATERIAL_IS_ITEM_METHOD = new ReflectMethod(ReflectionUtil.getNMSClass("Item"), "getById", int.class);
        } else {
            MATERIAL_IS_ITEM_METHOD = new ReflectMethod(Material.class, "isItem");
        }
    }

    public static Material parseMaterial(String materialName) {
        // Backwards compatibility
        Material materialFromAliases = Material.getMaterial(MATERIAL_ALIASES.get(removeSpacingChars(materialName).toLowerCase()));
        if (materialFromAliases != null) {
            return materialFromAliases;
        }
        Optional<XMaterial> xMaterialOptional = XMaterial.matchXMaterial(materialName);
        return xMaterialOptional.map(XMaterial::parseMaterial).orElse(null);
    }

    public static boolean isItem(Material material) {
        if (Version.afterOrEqual(13)) {
            return MATERIAL_IS_ITEM_METHOD.invoke(material);
        } else {
            return MATERIAL_IS_ITEM_METHOD.invokeStatic(material.getId()) != null;
        }
    }

}
