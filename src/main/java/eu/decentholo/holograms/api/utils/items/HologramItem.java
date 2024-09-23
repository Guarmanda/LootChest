package eu.decentholo.holograms.api.utils.items;

import de.tr7zw.changeme.nbtapi.NBTItem;
import eu.decentholo.holograms.api.utils.PAPI;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@Data
@AllArgsConstructor
public class HologramItem {

    private static final String ENCHANTED_INDICATOR = "!ENCHANTED";
    private final String content;
    private String nbt;
    private String extras;
    private Material material;
    private short durability = 0;
    private boolean enchanted = false;

    public HologramItem(String string) {
        this.content = string;
        this.parseContent();
    }

    public ItemStack parse(Player player) {
        try {
            ItemBuilder itemBuilder = new ItemBuilder(material);
            if (durability > 0) {
                itemBuilder.withDurability(durability);
            }

            ItemStack itemStack = itemBuilder.toItemStack();

            if (nbt != null) {
                applyNBT(player, itemStack);
            }

            return itemStack;
        } catch (Exception e) {
            return new ItemStack(Material.STONE);
        }
    }

    @SuppressWarnings("deprecation")
    private void applyNBT(Player player, ItemStack itemStack) {
        try {
            Bukkit.getUnsafe().modifyItemStack(itemStack, player == null ? nbt : PAPI.setPlaceholders(player, nbt));
        } catch (Exception e) {
            //Log.warn("Failed to apply NBT tag to item: %s", e, nbt);
        }
    }

    private void parseContent() {
        String string = this.content;
        string = findExtras(string);
        string = findNBT(string);
        string = checkEnchanted(string);
        parseMaterial(string);

        if (this.material == null) {
            this.material = Material.STONE;
        }
    }

    private void parseMaterial(String string) {
        String materialString = string.trim().split(" ", 2)[0];
        String materialName = materialString;
        if (materialString.contains(":")) {
            String[] materialStringSpl = materialString.split(":", 2);
            materialName = materialStringSpl[0];
            try {
                this.durability = Short.parseShort(materialStringSpl[1]);
            } catch (Exception e) {
                this.durability = 0;
            }
        }
        this.material = DecentMaterial.parseMaterial(materialName);
    }

    private String checkEnchanted(String string) {
        if (string.contains(ENCHANTED_INDICATOR)) {
            string = string.replace(ENCHANTED_INDICATOR, "");
            this.enchanted = true;
        }
        return string;
    }

    private String findNBT(String string) {
        if (string.contains("{") && string.contains("}")) {
            int nbtStart = string.indexOf('{');
            int nbtEnd = string.lastIndexOf('}');
            if (nbtStart > 0 && nbtEnd > 0 && nbtEnd > nbtStart) {
                this.nbt = string.substring(nbtStart, nbtEnd + 1);
                string = string.substring(0, nbtStart) + string.substring(nbtEnd + 1);
            }
        }
        return string;
    }

    private String findExtras(String string) {
        if (string.contains("(") && string.contains(")")) {
            int extrasStart = string.indexOf('(');
            int extrasEnd = string.lastIndexOf(')');
            if (extrasStart > 0 && extrasEnd > 0 && extrasEnd > extrasStart) {
                this.extras = string.substring(extrasStart + 1, extrasEnd);
                string = string.substring(0, extrasStart) + string.substring(extrasEnd + 1);
            }
        }
        return string;
    }

    @SuppressWarnings("deprecation")
    public static HologramItem fromItemStack(ItemStack itemStack) {
        Validate.notNull(itemStack);

        StringBuilder stringBuilder = new StringBuilder();
        Material material = itemStack.getType();
        stringBuilder.append(material.name());
        int durability = itemStack.getDurability();
        if (durability > 0) {
            stringBuilder.append(":").append(durability);
        }
        stringBuilder.append(" ");
        Map<Enchantment, Integer> enchants = itemStack.getEnchantments();
        if (enchants != null && !enchants.isEmpty()) {
            stringBuilder.append(ENCHANTED_INDICATOR).append(" ");
        }
        
        NBTItem nbtItem = new NBTItem(itemStack);
        if (nbtItem.hasTag("CustomModelData")) {
            int customModelData = nbtItem.getInteger("CustomModelData");
            stringBuilder.append(" {CustomModelData:").append(customModelData).append("}");
        }
        return new HologramItem(stringBuilder.toString());
    }

    public static ItemStack parseItemStack(String string, Player player) {
        string = PAPI.setPlaceholders(player, string);
        return new HologramItem(string).parse(player);
    }

}
