package eu.decentholo.holograms.api.nms.versions;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.decentholo.holograms.api.nms.NMS;
import eu.decentholo.holograms.api.utils.reflect.ReflectConstructor;
import eu.decentholo.holograms.api.utils.reflect.ReflectField;
import eu.decentholo.holograms.api.utils.reflect.ReflectMethod;
import eu.decentholo.holograms.api.utils.reflect.ReflectionUtil;

@SuppressWarnings("unused")
public class NMS_1_8 extends NMS {

    // UTILITY
    private static final Class<?> ENTITY_CLASS;
    private static final Class<?> ITEM_STACK_CLASS;
    private static final ReflectMethod CRAFT_ITEM_NMS_COPY_METHOD;
    // DATA WATCHER
    private static final Class<?> DATA_WATCHER_CLASS;
    private static final ReflectConstructor DATA_WATCHER_CONSTRUCTOR;
    private static final ReflectMethod DATA_WATCHER_A_METHOD;
    // MATH HELPER
    private static final Class<?> MATH_HELPER_CLASS;
    private static final ReflectMethod MATH_HELPER_FLOOR_METHOD;
    private static final ReflectMethod MATH_HELPER_D_METHOD;
    // PACKETS
    private static final ReflectConstructor PACKET_SPAWN_ENTITY_CONSTRUCTOR;
    private static final ReflectConstructor PACKET_SPAWN_ENTITY_LIVING_CONSTRUCTOR;
    private static final ReflectConstructor PACKET_ENTITY_METADATA_CONSTRUCTOR;
    private static final ReflectConstructor PACKET_ENTITY_TELEPORT_CONSTRUCTOR;
    private static final ReflectConstructor PACKET_ATTACH_ENTITY_CONSTRUCTOR;
    private static final ReflectConstructor PACKET_ENTITY_EQUIPMENT_CONSTRUCTOR;
    private static final ReflectConstructor PACKET_ENTITY_DESTROY_CONSTRUCTOR;

    private static final ReflectField<Integer> ENTITY_COUNTER_FIELD;

    static {
        // UTILITY
        ENTITY_CLASS = ReflectionUtil.getNMSClass("Entity");
        ITEM_STACK_CLASS = ReflectionUtil.getNMSClass("ItemStack");
        CRAFT_ITEM_NMS_COPY_METHOD = new ReflectMethod(ReflectionUtil.getObcClass("inventory.CraftItemStack"), "asNMSCopy", ItemStack.class);
        // DATA WATCHER
        DATA_WATCHER_CLASS = ReflectionUtil.getNMSClass("DataWatcher");
        DATA_WATCHER_CONSTRUCTOR = new ReflectConstructor(DATA_WATCHER_CLASS, ENTITY_CLASS);
        DATA_WATCHER_A_METHOD = new ReflectMethod(DATA_WATCHER_CLASS, "a", int.class, Object.class);
        // MATH HELPER
        MATH_HELPER_CLASS = ReflectionUtil.getNMSClass("MathHelper");
        MATH_HELPER_FLOOR_METHOD = new ReflectMethod(MATH_HELPER_CLASS, "floor", double.class);
        MATH_HELPER_D_METHOD = new ReflectMethod(MATH_HELPER_CLASS, "d", float.class);
        // PACKETS
        PACKET_SPAWN_ENTITY_CONSTRUCTOR = new ReflectConstructor(ReflectionUtil.getNMSClass("PacketPlayOutSpawnEntity"));
        PACKET_SPAWN_ENTITY_LIVING_CONSTRUCTOR = new ReflectConstructor(ReflectionUtil.getNMSClass("PacketPlayOutSpawnEntityLiving"));
        PACKET_ENTITY_METADATA_CONSTRUCTOR = new ReflectConstructor(ReflectionUtil.getNMSClass("PacketPlayOutEntityMetadata"), int.class, DATA_WATCHER_CLASS, boolean.class);
        PACKET_ENTITY_TELEPORT_CONSTRUCTOR = new ReflectConstructor(ReflectionUtil.getNMSClass("PacketPlayOutEntityTeleport"));
        PACKET_ATTACH_ENTITY_CONSTRUCTOR = new ReflectConstructor(ReflectionUtil.getNMSClass("PacketPlayOutAttachEntity"));
        PACKET_ENTITY_EQUIPMENT_CONSTRUCTOR = new ReflectConstructor(ReflectionUtil.getNMSClass("PacketPlayOutEntityEquipment"), int.class, int.class, ITEM_STACK_CLASS);
        PACKET_ENTITY_DESTROY_CONSTRUCTOR = new ReflectConstructor(ReflectionUtil.getNMSClass("PacketPlayOutEntityDestroy"), int[].class);

        ENTITY_COUNTER_FIELD = new ReflectField<>(ENTITY_CLASS, "entityCount");
    }

    @Override
    public int getFreeEntityId() {
        int entityCount = ENTITY_COUNTER_FIELD.getValue(null);
        ENTITY_COUNTER_FIELD.setValue(null, entityCount + 1);
        return entityCount;
    }

    @Override
    public void showFakeEntityArmorStand(Player player, Location location, int entityId, boolean invisible, boolean small, boolean clickable) {
        Object dataWatcher = DATA_WATCHER_CONSTRUCTOR.newInstance(ENTITY_CLASS.cast(null));
        DATA_WATCHER_A_METHOD.invoke(dataWatcher, 0, (byte) (invisible ? 0x20 : 0x00)); // Invisible
        byte data = 0x08;
        if (small) data += 0x01;
        if (!clickable) data += 0x10;
        DATA_WATCHER_A_METHOD.invoke(dataWatcher, 10, data);
        showFakeEntityLiving(player, location, entityId, dataWatcher);
    }

    @Override
    public void updateFakeEntityCustomName(Player player, String name, int entityId) {
        Validate.notNull(player);
        Validate.notNull(name);

        Object dataWatcher = DATA_WATCHER_CONSTRUCTOR.newInstance(ENTITY_CLASS.cast(null));
        DATA_WATCHER_A_METHOD.invoke(dataWatcher, 2, name); // Custom Name
        DATA_WATCHER_A_METHOD.invoke(dataWatcher, 3, (byte) (ChatColor.stripColor(name).isEmpty() ? 0 : 1)); // Custom Name Visible
        sendPacket(player, PACKET_ENTITY_METADATA_CONSTRUCTOR.newInstance(entityId, dataWatcher, true));
    }

    @Override
    public void teleportFakeEntity(Player player, Location location, int entityId) {
        Validate.notNull(player);
        Validate.notNull(location);

        Object teleport = PACKET_ENTITY_TELEPORT_CONSTRUCTOR.newInstance();
        if (buildSpawnPacket(location, entityId, teleport)) return;
        ReflectionUtil.setFieldValue(teleport, "e", (byte) ((int) (location.getYaw() * 256.0F / 360.0F)));
        ReflectionUtil.setFieldValue(teleport, "f", (byte) ((int) (location.getPitch() * 256.0F / 360.0F)));
        ReflectionUtil.setFieldValue(teleport, "g", false);
        sendPacket(player, teleport);
    }


    @SuppressWarnings("RedundantCast")
    @Override
    public void hideFakeEntities(Player player, int... entityIds) {
        Validate.notNull(player);
        sendPacket(player, PACKET_ENTITY_DESTROY_CONSTRUCTOR.newInstance((Object) entityIds));
    }

    public void showFakeEntity(Player player, Location location, int entityTypeId, int entityId) {
        Validate.notNull(player);
        Validate.notNull(location);

        Object spawn = PACKET_SPAWN_ENTITY_CONSTRUCTOR.newInstance();
        if (buildSpawnPacket(location, entityId, spawn)) return;
        ReflectionUtil.setFieldValue(spawn, "h", MATH_HELPER_D_METHOD.invokeStatic(location.getPitch() * 256.0F / 360.0F));
        ReflectionUtil.setFieldValue(spawn, "i", MATH_HELPER_D_METHOD.invokeStatic(location.getYaw() * 256.0F / 360.0F));
        ReflectionUtil.setFieldValue(spawn, "j", entityTypeId);
        sendPacket(player, spawn);
    }

    private boolean buildSpawnPacket(Location location, int entityId, Object spawn) {
        if (spawn == null) return true;
        ReflectionUtil.setFieldValue(spawn, "a", entityId);
        ReflectionUtil.setFieldValue(spawn, "b", MATH_HELPER_FLOOR_METHOD.invokeStatic(location.getX() * 32.0D));
        ReflectionUtil.setFieldValue(spawn, "c", MATH_HELPER_FLOOR_METHOD.invokeStatic(location.getY() * 32.0D));
        ReflectionUtil.setFieldValue(spawn, "d", MATH_HELPER_FLOOR_METHOD.invokeStatic(location.getZ() * 32.0D));
        return false;
    }

    private void showFakeEntityLiving(Player player, Location location, int entityId, Object dataWatcher) {
        Validate.notNull(player);
        Validate.notNull(location);
        if (dataWatcher == null || !(DATA_WATCHER_CLASS != null && DATA_WATCHER_CLASS.isAssignableFrom(dataWatcher.getClass()))) return;

        Object spawn = PACKET_SPAWN_ENTITY_LIVING_CONSTRUCTOR.newInstance();
        if (spawn == null) return;
        ReflectionUtil.setFieldValue(spawn, "a", entityId);
        ReflectionUtil.setFieldValue(spawn, "b", 30);
        ReflectionUtil.setFieldValue(spawn, "c", MATH_HELPER_FLOOR_METHOD.invokeStatic(location.getX() * 32.0D));
        ReflectionUtil.setFieldValue(spawn, "d", MATH_HELPER_FLOOR_METHOD.invokeStatic(location.getY() * 32.0D));
        ReflectionUtil.setFieldValue(spawn, "e", MATH_HELPER_FLOOR_METHOD.invokeStatic(location.getZ() * 32.0D));
        ReflectionUtil.setFieldValue(spawn, "i", (byte) ((int) (location.getYaw() * 256.0F / 360.0F)));
        ReflectionUtil.setFieldValue(spawn, "j", (byte) ((int) (location.getPitch() * 256.0F / 360.0F)));
        ReflectionUtil.setFieldValue(spawn, "k", (byte) ((int) (location.getYaw() * 256.0F / 360.0F)));
        ReflectionUtil.setFieldValue(spawn, "l", dataWatcher);
        sendPacket(player, spawn);
    }

}
