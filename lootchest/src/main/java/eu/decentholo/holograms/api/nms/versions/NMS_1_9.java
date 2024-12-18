package eu.decentholo.holograms.api.nms.versions;

import eu.decentholo.holograms.api.nms.NMS;
import eu.decentholo.holograms.api.utils.reflect.ReflectConstructor;
import eu.decentholo.holograms.api.utils.reflect.ReflectField;
import eu.decentholo.holograms.api.utils.reflect.ReflectMethod;
import eu.decentholo.holograms.api.utils.reflect.ReflectionUtil;
import eu.decentholo.holograms.api.utils.reflect.Version;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class NMS_1_9 extends NMS {

    private static final int ARMOR_STAND_ID = Version.before(13) ? 30 : 1;

    // UTILITY
    private static final Class<?> ENTITY_CLASS;
    private static final Class<?> ENTITY_ARMOR_STAND_CLASS;
    private static final ReflectMethod CRAFT_CHAT_MESSAGE_FROM_STRING_METHOD;
    // DATA WATCHER
    private static final Class<?> DATA_WATCHER_CLASS;
    private static final ReflectConstructor DATA_WATCHER_CONSTRUCTOR;
    private static final ReflectMethod DATA_WATCHER_REGISTER_METHOD;
    // MATH HELPER
    private static final Class<?> MATH_HELPER_CLASS;
    private static final ReflectMethod MATH_HELPER_A_METHOD;
    private static final ReflectConstructor PACKET_SPAWN_ENTITY_LIVING_CONSTRUCTOR;
    private static final ReflectConstructor PACKET_ENTITY_METADATA_CONSTRUCTOR;
    private static final ReflectConstructor PACKET_ENTITY_TELEPORT_CONSTRUCTOR;
    private static final ReflectConstructor PACKET_ENTITY_DESTROY_CONSTRUCTOR;
    // DATA WATCHER OBJECT
    private static final Class<?> DWO_CLASS;
    private static final Object DWO_CUSTOM_NAME;
    private static final Object DWO_CUSTOM_NAME_VISIBLE;
    private static final Object DWO_ENTITY_DATA;
    private static final Object DWO_ARMOR_STAND_DATA;
    private static final ReflectField<Object> ENTITY_COUNTER_FIELD;

    static {
        DWO_CLASS = ReflectionUtil.getNMSClass("DataWatcherObject");
        // UTILITY
        ENTITY_CLASS = ReflectionUtil.getNMSClass("Entity");
        ENTITY_ARMOR_STAND_CLASS = ReflectionUtil.getNMSClass("EntityArmorStand");
        CRAFT_CHAT_MESSAGE_FROM_STRING_METHOD = new ReflectMethod(ReflectionUtil.getObcClass("util.CraftChatMessage"), "fromStringOrNull", String.class);
        // DATA WATCHER
        DATA_WATCHER_CLASS = ReflectionUtil.getNMSClass("DataWatcher");
        DATA_WATCHER_CONSTRUCTOR = new ReflectConstructor(DATA_WATCHER_CLASS, ENTITY_CLASS);
        DATA_WATCHER_REGISTER_METHOD = new ReflectMethod(DATA_WATCHER_CLASS, "register", DWO_CLASS, Object.class);
        // MATH HELPER
        MATH_HELPER_CLASS = ReflectionUtil.getNMSClass("MathHelper");
        new ReflectMethod(MATH_HELPER_CLASS, "d", float.class);
        MATH_HELPER_A_METHOD = new ReflectMethod(MATH_HELPER_CLASS, "a", Random.class);
        new ReflectConstructor(ReflectionUtil.getNMSClass("PacketPlayOutSpawnEntity"));
        PACKET_SPAWN_ENTITY_LIVING_CONSTRUCTOR = new ReflectConstructor(ReflectionUtil.getNMSClass("PacketPlayOutSpawnEntityLiving"));
        PACKET_ENTITY_METADATA_CONSTRUCTOR = new ReflectConstructor(ReflectionUtil.getNMSClass("PacketPlayOutEntityMetadata"), int.class, DATA_WATCHER_CLASS, boolean.class);
        PACKET_ENTITY_TELEPORT_CONSTRUCTOR = new ReflectConstructor(ReflectionUtil.getNMSClass("PacketPlayOutEntityTeleport"));
 
        PACKET_ENTITY_DESTROY_CONSTRUCTOR = new ReflectConstructor(ReflectionUtil.getNMSClass("PacketPlayOutEntityDestroy"), int[].class);
        // DATA WATCHER OBJECT
        switch (Version.CURRENT) {
            case v1_9_R1:
                DWO_ENTITY_DATA = new ReflectField<>(ENTITY_CLASS, "ax").getValue(null);
                DWO_CUSTOM_NAME = new ReflectField<>(ENTITY_CLASS, "az").getValue(null);
                DWO_CUSTOM_NAME_VISIBLE = new ReflectField<>(ENTITY_CLASS, "aA").getValue(null);
                break;
            case v1_9_R2:
                DWO_ENTITY_DATA = new ReflectField<>(ENTITY_CLASS, "ay").getValue(null);
                DWO_CUSTOM_NAME = new ReflectField<>(ENTITY_CLASS, "aA").getValue(null);
                DWO_CUSTOM_NAME_VISIBLE = new ReflectField<>(ENTITY_CLASS, "aB").getValue(null);
                break;
            case v1_10_R1:
                DWO_ENTITY_DATA = new ReflectField<>(ENTITY_CLASS, "aa").getValue(null);
                DWO_CUSTOM_NAME = new ReflectField<>(ENTITY_CLASS, "aA").getValue(null);
                DWO_CUSTOM_NAME_VISIBLE = new ReflectField<>(ENTITY_CLASS, "aB").getValue(null);
                break;
            case v1_11_R1:
                DWO_ENTITY_DATA = new ReflectField<>(ENTITY_CLASS, "Z").getValue(null);
                DWO_CUSTOM_NAME = new ReflectField<>(ENTITY_CLASS, "aA").getValue(null);
                DWO_CUSTOM_NAME_VISIBLE = new ReflectField<>(ENTITY_CLASS, "aB").getValue(null);
                break;
            case v1_12_R1:
                DWO_ENTITY_DATA = new ReflectField<>(ENTITY_CLASS, "Z").getValue(null);
                DWO_CUSTOM_NAME = new ReflectField<>(ENTITY_CLASS, "aB").getValue(null);
                DWO_CUSTOM_NAME_VISIBLE = new ReflectField<>(ENTITY_CLASS, "aC").getValue(null);
                break;
            case v1_13_R1:
            case v1_13_R2:
                DWO_ENTITY_DATA = new ReflectField<>(ENTITY_CLASS, "ac").getValue(null);
                DWO_CUSTOM_NAME = new ReflectField<>(ENTITY_CLASS, "aE").getValue(null);
                DWO_CUSTOM_NAME_VISIBLE = new ReflectField<>(ENTITY_CLASS, "aF").getValue(null);
                break;
            case v1_14_R1:
                DWO_ENTITY_DATA = new ReflectField<>(ENTITY_CLASS, "W").getValue(null);
                DWO_CUSTOM_NAME = new ReflectField<>(ENTITY_CLASS, "az").getValue(null);
                DWO_CUSTOM_NAME_VISIBLE = new ReflectField<>(ENTITY_CLASS, "aA").getValue(null);
                break;
            case v1_15_R1:
                DWO_ENTITY_DATA = new ReflectField<>(ENTITY_CLASS, "T").getValue(null);
                DWO_CUSTOM_NAME = new ReflectField<>(ENTITY_CLASS, "az").getValue(null);
                DWO_CUSTOM_NAME_VISIBLE = new ReflectField<>(ENTITY_CLASS, "aA").getValue(null);
                break;
            case v1_16_R1:
                DWO_ENTITY_DATA = new ReflectField<>(ENTITY_CLASS, "T").getValue(null);
                DWO_CUSTOM_NAME = new ReflectField<>(ENTITY_CLASS, "ax").getValue(null);
                DWO_CUSTOM_NAME_VISIBLE = new ReflectField<>(ENTITY_CLASS, "ay").getValue(null);
                break;
            case v1_16_R2:
            case v1_16_R3:
                DWO_ENTITY_DATA = new ReflectField<>(ENTITY_CLASS, "S").getValue(null);
                DWO_CUSTOM_NAME = new ReflectField<>(ENTITY_CLASS, "aq").getValue(null);
                DWO_CUSTOM_NAME_VISIBLE = new ReflectField<>(ENTITY_CLASS, "ar").getValue(null);
                break;
            default:
                DWO_ENTITY_DATA = null;
                DWO_CUSTOM_NAME = null;
                DWO_CUSTOM_NAME_VISIBLE = null;
        }

        if (Version.before(13)) {
            DWO_ARMOR_STAND_DATA = new ReflectField<>(ENTITY_ARMOR_STAND_CLASS, "a").getValue(null);
        } else if (Version.before(14)) {
            DWO_ARMOR_STAND_DATA = new ReflectField<>(ENTITY_ARMOR_STAND_CLASS, "a").getValue(null);
        } else {
            DWO_ARMOR_STAND_DATA = new ReflectField<>(ENTITY_ARMOR_STAND_CLASS, "b").getValue(null);
            // ENTITY TYPES
            Class<?> registryBlocksClass = ReflectionUtil.getNMSClass("RegistryBlocks");
            // ENTITY TYPES
            Class<?> entityTypesClass = ReflectionUtil.getNMSClass("EntityTypes");
            new ReflectMethod(registryBlocksClass, "fromId", int.class);
            if (entityTypesClass != null) {
                for (Method method : entityTypesClass.getMethods()) {
                    if (method.getReturnType().getName().contains("EntitySize")) {
                        new ReflectMethod(entityTypesClass, method.getName());
                    }
                }
            }
            new ReflectField<>(ReflectionUtil.getNMSClass("EntitySize"), "height");
        }

        ENTITY_COUNTER_FIELD = new ReflectField<>(ENTITY_CLASS, "entityCount");
    }

    @Override
    public int getFreeEntityId() {
        Object entityCounter = ENTITY_COUNTER_FIELD.getValue(null);
        if (entityCounter instanceof AtomicInteger) {
            return ((AtomicInteger) ENTITY_COUNTER_FIELD.getValue(null)).addAndGet(1);
        }
        ENTITY_COUNTER_FIELD.setValue(null, (int) entityCounter + 1);
        return (int) entityCounter;
    }


    private void showFakeEntityLiving(Player player, Location location, int entityId, Object dataWatcher) {
        Validate.notNull(player);
        Validate.notNull(location);
        if (DATA_WATCHER_CLASS != null && (dataWatcher == null || !DATA_WATCHER_CLASS.isAssignableFrom(dataWatcher.getClass())))
            return;

        if (NMS_1_9.ARMOR_STAND_ID == -1) return;
        Object spawn = PACKET_SPAWN_ENTITY_LIVING_CONSTRUCTOR.newInstance();
        if (spawn == null) return;
        ReflectionUtil.setFieldValue(spawn, "a", entityId);
        ReflectionUtil.setFieldValue(spawn, "b", MATH_HELPER_A_METHOD.invokeStatic(ThreadLocalRandom.current()));
        ReflectionUtil.setFieldValue(spawn, "c", NMS_1_9.ARMOR_STAND_ID);
        ReflectionUtil.setFieldValue(spawn, "d", location.getX());
        ReflectionUtil.setFieldValue(spawn, "e", location.getY());
        ReflectionUtil.setFieldValue(spawn, "f", location.getZ());
        ReflectionUtil.setFieldValue(spawn, "j", (byte) ((int) (location.getYaw() * 256.0F / 360.0F)));
        ReflectionUtil.setFieldValue(spawn, "k", (byte) ((int) (location.getPitch() * 256.0F / 360.0F)));
        ReflectionUtil.setFieldValue(spawn, "l", (byte) ((int) (location.getYaw() * 256.0F / 360.0F)));
        ReflectionUtil.setFieldValue(spawn, "m", dataWatcher);
        sendPacket(player, spawn);
    }

    @Override
    public void showFakeEntityArmorStand(Player player, Location location, int entityId, boolean invisible, boolean small, boolean clickable) {
        Object dataWatcher = null;
        if (ENTITY_CLASS != null) {
            dataWatcher = DATA_WATCHER_CONSTRUCTOR.newInstance(ENTITY_CLASS.cast(null));
        }
        DATA_WATCHER_REGISTER_METHOD.invoke(dataWatcher, DWO_ENTITY_DATA, (byte) (invisible ? 0x20 : 0x00)); // Invisible
        byte data = 0x08;
        if (small) data += 0x01;
        if (!clickable) data += 0x10;
        DATA_WATCHER_REGISTER_METHOD.invoke(dataWatcher, DWO_ARMOR_STAND_DATA, data);
        showFakeEntityLiving(player, location, entityId, dataWatcher);
        sendPacket(player, PACKET_ENTITY_METADATA_CONSTRUCTOR.newInstance(entityId, dataWatcher, true));
    }

    @Override
    public void updateFakeEntityCustomName(Player player, String name, int entityId) {
        Validate.notNull(player);
        Validate.notNull(name);

        Object dataWatcher = null;
        if (ENTITY_CLASS != null) {
            dataWatcher = DATA_WATCHER_CONSTRUCTOR.newInstance(ENTITY_CLASS.cast(null));
        }
        if (Version.before(13)) {
            DATA_WATCHER_REGISTER_METHOD.invoke(dataWatcher, DWO_CUSTOM_NAME, name); // Custom Name
        } else {
            DATA_WATCHER_REGISTER_METHOD.invoke(dataWatcher, DWO_CUSTOM_NAME, java.util.Optional.ofNullable(CRAFT_CHAT_MESSAGE_FROM_STRING_METHOD.invokeStatic(name))); // Custom Name
        }
        DATA_WATCHER_REGISTER_METHOD.invoke(dataWatcher, DWO_CUSTOM_NAME_VISIBLE, !ChatColor.stripColor(name).isEmpty()); // Custom Name Visible
        sendPacket(player, PACKET_ENTITY_METADATA_CONSTRUCTOR.newInstance(entityId, dataWatcher, true));
    }

    @Override
    public void teleportFakeEntity(Player player, Location location, int entityId) {
        Validate.notNull(player);
        Validate.notNull(location);

        Object teleport = PACKET_ENTITY_TELEPORT_CONSTRUCTOR.newInstance();
        if (teleport == null) return;
        ReflectionUtil.setFieldValue(teleport, "a", entityId);
        ReflectionUtil.setFieldValue(teleport, "b", location.getX());
        ReflectionUtil.setFieldValue(teleport, "c", location.getY());
        ReflectionUtil.setFieldValue(teleport, "d", location.getZ());
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



}
