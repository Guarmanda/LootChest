package eu.decentholo.holograms.api.nms.versions;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import eu.decentholo.holograms.api.nms.NMS;
import eu.decentholo.holograms.api.utils.reflect.ReflectConstructor;
import eu.decentholo.holograms.api.utils.reflect.ReflectField;
import eu.decentholo.holograms.api.utils.reflect.ReflectMethod;
import eu.decentholo.holograms.api.utils.reflect.ReflectionUtil;
import eu.decentholo.holograms.api.utils.reflect.Version;

import java.util.ArrayList;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class NMS_1_17 extends NMS {

    // UTILITY
    private static final Class<?> ENTITY_CLASS;
    private static final Class<?> ENTITY_ARMOR_STAND_CLASS;
    private static final Class<?> ENTITY_TYPES_CLASS;
    private static final Class<?> VEC_3D_CLASS;
    private static final ReflectField<?> ENTITY_TYPES_REGISTRY_FIELD;
    private static final ReflectMethod REGISTRY_BLOCKS_FROM_ID_METHOD;
    private static final ReflectMethod CRAFT_CHAT_MESSAGE_FROM_STRING_METHOD;
    // MATH HELPER
    private static final Class<?> MATH_HELPER_CLASS;
    private static final Class<?> POSITION_MOVE_ROTATION_CLASS;
    private static final ReflectMethod MATH_HELPER_A_METHOD;
    // PACKET DATA SERIALIZER
    private static final Class<?> PACKET_DATA_SERIALIZER_CLASS;
    private static final ReflectConstructor PACKET_DATA_SERIALIZER_CONSTRUCTOR;
    private static final ReflectMethod PACKET_DATA_SERIALIZER_WRITE_INT_METHOD;
    private static final ReflectMethod PACKET_DATA_SERIALIZER_WRITE_UUID_METHOD;
    private static final ReflectMethod PACKET_DATA_SERIALIZER_WRITE_DOUBLE_METHOD;
    private static final ReflectMethod PACKET_DATA_SERIALIZER_WRITE_BYTE_METHOD;
    private static final ReflectMethod PACKET_DATA_SERIALIZER_WRITE_SHORT_METHOD;
    private static final ReflectMethod PACKET_DATA_SERIALIZER_WRITE_BOOLEAN_METHOD;
    private static final ReflectConstructor POSITION_MOVE_ROTATION_CONSTRUCTOR;
    private static final ReflectConstructor VEC_3D_CONSTRUCTOR;
    // PACKETS
    private static final ReflectConstructor PACKET_SPAWN_ENTITY_CONSTRUCTOR;
    private static final ReflectConstructor PACKET_SPAWN_ENTITY_LIVING_CONSTRUCTOR;
    private static final ReflectConstructor PACKET_ENTITY_TELEPORT_CONSTRUCTOR;

    private static final ReflectConstructor PACKET_ENTITY_DESTROY_CONSTRUCTOR;
    // DATA WATCHER OBJECT
    private static final Class<?> DWO_CLASS;
    private static final Class<?> DWS_CLASS;
    private static final Class<?> DWI_CLASS;
    private static final Object DWO_CUSTOM_NAME;
    private static final Object DWO_CUSTOM_NAME_VISIBLE;
    private static final Object DWO_ENTITY_DATA;
    private static final Object DWO_ARMOR_STAND_DATA;
    private static final ReflectConstructor DATA_WATCHER_ITEM_CONSTRUCTOR;
    // ENTITY TYPES
    private static final ReflectMethod ENTITY_TYPES_A_METHOD;
    private static final ReflectMethod ENTITY_TYPE_GET_KEY_METHOD;
    private static final ReflectMethod REGISTRY_BLOCKS_GET_ID_METHOD;
    private static final ReflectMethod NAMESPACED_KEY_GET_KEY_METHOD;

    private static final ReflectField<AtomicInteger> ENTITY_COUNTER_FIELD;
    private static final Object VEC_3D_A;

    private static final String BUILTINREGISTRIES = "core.registries.BuiltInRegistries";
    private static final String IREGISTRY = "core.IRegistry";

    static {
        DWO_CLASS = ReflectionUtil.getNMClass("network.syncher.DataWatcherObject");
        DWS_CLASS = ReflectionUtil.getNMClass("network.syncher.DataWatcherSerializer");
        DWI_CLASS = ReflectionUtil.getNMClass("network.syncher.DataWatcher$Item");
        // UTILITY
        ENTITY_CLASS = ReflectionUtil.getNMClass("world.entity.Entity");
        ENTITY_ARMOR_STAND_CLASS = ReflectionUtil.getNMClass("world.entity.decoration.EntityArmorStand");
        ENTITY_TYPES_CLASS = ReflectionUtil.getNMClass("world.entity.EntityTypes");
        VEC_3D_CLASS = ReflectionUtil.getNMClass("world.phys.Vec3D");
        if (Version.afterOrEqual(Version.v1_21_R2)) {
            POSITION_MOVE_ROTATION_CLASS = ReflectionUtil.getNMClass("world.entity.PositionMoveRotation");
        } else {
            POSITION_MOVE_ROTATION_CLASS = null;
        }
        CRAFT_CHAT_MESSAGE_FROM_STRING_METHOD = new ReflectMethod(ReflectionUtil.getObcClass("util.CraftChatMessage"), "fromStringOrNull", String.class);
       // DATA WATCHER
        DATA_WATCHER_ITEM_CONSTRUCTOR = new ReflectConstructor(DWI_CLASS, DWO_CLASS, Object.class);
        POSITION_MOVE_ROTATION_CONSTRUCTOR = new ReflectConstructor(POSITION_MOVE_ROTATION_CLASS, VEC_3D_CLASS, VEC_3D_CLASS, float.class, float.class);
        VEC_3D_CONSTRUCTOR = new ReflectConstructor(VEC_3D_CLASS, double.class, double.class, double.class);


        if (Version.afterOrEqual(18)) {
            if (Version.afterOrEqual(Version.v1_21_R1)) {
                ENTITY_TYPES_REGISTRY_FIELD = new ReflectField<>(ReflectionUtil.getNMClass(BUILTINREGISTRIES), "f");
            } else if (Version.afterOrEqual(Version.v1_20_R3)) {
                ENTITY_TYPES_REGISTRY_FIELD = new ReflectField<>(ReflectionUtil.getNMClass(BUILTINREGISTRIES), "g");
            } else if (Version.afterOrEqual(Version.v1_19_R2)) {
                ENTITY_TYPES_REGISTRY_FIELD = new ReflectField<>(ReflectionUtil.getNMClass(BUILTINREGISTRIES), "h");
            } else if (Version.afterOrEqual(19)) {
                ENTITY_TYPES_REGISTRY_FIELD = new ReflectField<>(ReflectionUtil.getNMClass(IREGISTRY), "X");
            } else if (Version.is(Version.v1_18_R2)) {
                ENTITY_TYPES_REGISTRY_FIELD = new ReflectField<>(ReflectionUtil.getNMClass(IREGISTRY), "W");
            } else {
                ENTITY_TYPES_REGISTRY_FIELD = new ReflectField<>(ReflectionUtil.getNMClass(IREGISTRY), "Z");
            }
            REGISTRY_BLOCKS_FROM_ID_METHOD = new ReflectMethod(ReflectionUtil.getNMClass("core.RegistryBlocks"), "a", int.class);
        } else {
            ENTITY_TYPES_REGISTRY_FIELD = new ReflectField<>(ReflectionUtil.getNMClass(IREGISTRY), "Y");
            REGISTRY_BLOCKS_FROM_ID_METHOD = new ReflectMethod(ReflectionUtil.getNMClass("core.RegistryBlocks"), "fromId", int.class);
        }
        // MATH HELPER
        MATH_HELPER_CLASS = ReflectionUtil.getNMClass("util.MathHelper");
        MATH_HELPER_A_METHOD = new ReflectMethod(MATH_HELPER_CLASS, "a", Random.class);
        // PACKET DATA SERIALIZER
        PACKET_DATA_SERIALIZER_CLASS = ReflectionUtil.getNMClass("network.PacketDataSerializer");
        PACKET_DATA_SERIALIZER_CONSTRUCTOR = new ReflectConstructor(PACKET_DATA_SERIALIZER_CLASS, ByteBuf.class);
        if (Version.afterOrEqual(Version.v1_20_R2)) {
            PACKET_DATA_SERIALIZER_WRITE_INT_METHOD = new ReflectMethod(PACKET_DATA_SERIALIZER_CLASS, "c", int.class);
        } else {
            PACKET_DATA_SERIALIZER_WRITE_INT_METHOD = new ReflectMethod(PACKET_DATA_SERIALIZER_CLASS, "d", int.class);
        }
        PACKET_DATA_SERIALIZER_WRITE_UUID_METHOD = new ReflectMethod(PACKET_DATA_SERIALIZER_CLASS, "a", UUID.class);
        PACKET_DATA_SERIALIZER_WRITE_DOUBLE_METHOD = new ReflectMethod(PACKET_DATA_SERIALIZER_CLASS, "writeDouble", double.class);
        PACKET_DATA_SERIALIZER_WRITE_BYTE_METHOD = new ReflectMethod(PACKET_DATA_SERIALIZER_CLASS, "writeByte", int.class);
        PACKET_DATA_SERIALIZER_WRITE_SHORT_METHOD = new ReflectMethod(PACKET_DATA_SERIALIZER_CLASS, "writeShort", int.class);
        PACKET_DATA_SERIALIZER_WRITE_BOOLEAN_METHOD = new ReflectMethod(PACKET_DATA_SERIALIZER_CLASS, "writeBoolean", boolean.class);
        // PACKETS
        if (Version.before(19)) {
            PACKET_SPAWN_ENTITY_LIVING_CONSTRUCTOR = new ReflectConstructor(ReflectionUtil.getNMClass("network.protocol.game.PacketPlayOutSpawnEntityLiving"),
                    PACKET_DATA_SERIALIZER_CLASS);
            PACKET_SPAWN_ENTITY_CONSTRUCTOR = new ReflectConstructor(ReflectionUtil.getNMClass("network.protocol.game.PacketPlayOutSpawnEntity"),
                    int.class, UUID.class, double.class, double.class, double.class, float.class, float.class, ENTITY_TYPES_CLASS, int.class, VEC_3D_CLASS);
        } else {
            PACKET_SPAWN_ENTITY_LIVING_CONSTRUCTOR = null;
            PACKET_SPAWN_ENTITY_CONSTRUCTOR = new ReflectConstructor(ReflectionUtil.getNMClass("network.protocol.game.PacketPlayOutSpawnEntity"),
                    int.class, UUID.class, double.class, double.class, double.class, float.class, float.class, ENTITY_TYPES_CLASS, int.class, VEC_3D_CLASS, double.class);
        }
        if (Version.afterOrEqual(Version.v1_21_R2)) {
            PACKET_ENTITY_TELEPORT_CONSTRUCTOR = new ReflectConstructor(ReflectionUtil.getNMClass("network.protocol.game.PacketPlayOutEntityTeleport"),
                    int.class, POSITION_MOVE_ROTATION_CLASS, Set.class, boolean.class);
        } else {
            PACKET_ENTITY_TELEPORT_CONSTRUCTOR = new ReflectConstructor(ReflectionUtil.getNMClass("network.protocol.game.PacketPlayOutEntityTeleport"),
                    PACKET_DATA_SERIALIZER_CLASS);
        }
        Class<?> packetEntityDestroyClass = ReflectionUtil.getNMClass("network.protocol.game.PacketPlayOutEntityDestroy");
        if (Version.CURRENT_MINECRAFT_VERSION.equals("1.17")) {
            // So it seems that ONLY "1.17" has this constructor.
            // The NMS version is not even different from "1.17.1", which has the same constructor as all the other versions.
            // Basically, one of the "v1_17_R1" versions is different from the other.
            // Working with minecraft is so much fun.
            PACKET_ENTITY_DESTROY_CONSTRUCTOR = new ReflectConstructor(packetEntityDestroyClass, int.class);
        } else {
            PACKET_ENTITY_DESTROY_CONSTRUCTOR = new ReflectConstructor(packetEntityDestroyClass, int[].class);
        }
        // DATA WATCHER OBJECT
        if (Version.afterOrEqual(18)) {
            if (Version.afterOrEqual(Version.v1_21_R4)) {
                DWO_ENTITY_DATA = new ReflectField<>(ENTITY_CLASS, "am").getValue(null);
                DWO_CUSTOM_NAME = new ReflectField<>(ENTITY_CLASS, "aR").getValue(null);
                DWO_CUSTOM_NAME_VISIBLE = new ReflectField<>(ENTITY_CLASS, "aS").getValue(null);
                DWO_ARMOR_STAND_DATA = new ReflectField<>(ENTITY_ARMOR_STAND_CLASS, "bw").getValue(null);
            } else if (Version.afterOrEqual(Version.v1_21_R3)) {
                DWO_ENTITY_DATA = new ReflectField<>(ENTITY_CLASS, "am").getValue(null);
                DWO_CUSTOM_NAME = new ReflectField<>(ENTITY_CLASS, "aO").getValue(null);
                DWO_CUSTOM_NAME_VISIBLE = new ReflectField<>(ENTITY_CLASS, "aP").getValue(null);
                DWO_ARMOR_STAND_DATA = new ReflectField<>(ENTITY_ARMOR_STAND_CLASS, "bI").getValue(null);
            } else if (Version.afterOrEqual(Version.v1_21_R2)) {
				DWO_ENTITY_DATA = new ReflectField<>(ENTITY_CLASS, "am").getValue(null);
				DWO_CUSTOM_NAME = new ReflectField<>(ENTITY_CLASS, "aO").getValue(null);
				DWO_CUSTOM_NAME_VISIBLE = new ReflectField<>(ENTITY_CLASS, "aP").getValue(null);
                DWO_ARMOR_STAND_DATA = new ReflectField<>(ENTITY_ARMOR_STAND_CLASS, "bJ").getValue(null);
			} else if (Version.afterOrEqual(Version.v1_21_R1)) {
                DWO_ENTITY_DATA = new ReflectField<>(ENTITY_CLASS, "ap").getValue(null);
                DWO_CUSTOM_NAME = new ReflectField<>(ENTITY_CLASS, "aQ").getValue(null);
                DWO_CUSTOM_NAME_VISIBLE = new ReflectField<>(ENTITY_CLASS, "aR").getValue(null);
                DWO_ARMOR_STAND_DATA = new ReflectField<>(ENTITY_ARMOR_STAND_CLASS, "bH").getValue(null);
            } else if (Version.afterOrEqual(Version.v1_20_R4)) {
                DWO_ENTITY_DATA = new ReflectField<>(ENTITY_CLASS, "ap").getValue(null);
                DWO_CUSTOM_NAME = new ReflectField<>(ENTITY_CLASS, "aS").getValue(null);
                DWO_CUSTOM_NAME_VISIBLE = new ReflectField<>(ENTITY_CLASS, "aT").getValue(null);
                DWO_ARMOR_STAND_DATA = new ReflectField<>(ENTITY_ARMOR_STAND_CLASS, "bG").getValue(null);
            } else if (Version.afterOrEqual(Version.v1_20_R2)) {
                DWO_ENTITY_DATA = new ReflectField<>(ENTITY_CLASS, "ao").getValue(null);
                DWO_CUSTOM_NAME = new ReflectField<>(ENTITY_CLASS, "aU").getValue(null);
                DWO_CUSTOM_NAME_VISIBLE = new ReflectField<>(ENTITY_CLASS, "aV").getValue(null);
                DWO_ARMOR_STAND_DATA = new ReflectField<>(ENTITY_ARMOR_STAND_CLASS, "bC").getValue(null);
            } else if (Version.afterOrEqual(Version.v1_20_R1)) {
                DWO_ENTITY_DATA = new ReflectField<>(ENTITY_CLASS, "an").getValue(null);
                DWO_CUSTOM_NAME = new ReflectField<>(ENTITY_CLASS, "aU").getValue(null);
                DWO_CUSTOM_NAME_VISIBLE = new ReflectField<>(ENTITY_CLASS, "aV").getValue(null);
                DWO_ARMOR_STAND_DATA = new ReflectField<>(ENTITY_ARMOR_STAND_CLASS, "bC").getValue(null);
            } else if (Version.afterOrEqual(Version.v1_19_R3)) {
                DWO_ENTITY_DATA = new ReflectField<>(ENTITY_CLASS, "an").getValue(null);
                DWO_CUSTOM_NAME = new ReflectField<>(ENTITY_CLASS, "aR").getValue(null);
                DWO_CUSTOM_NAME_VISIBLE = new ReflectField<>(ENTITY_CLASS, "aS").getValue(null);
                DWO_ARMOR_STAND_DATA = new ReflectField<>(ENTITY_ARMOR_STAND_CLASS, "bB").getValue(null);
            } else if (Version.afterOrEqual(Version.v1_18_R2)) {
                DWO_ENTITY_DATA = new ReflectField<>(ENTITY_CLASS, "Z").getValue(null);
                DWO_CUSTOM_NAME = new ReflectField<>(ENTITY_CLASS, "aM").getValue(null);
                DWO_CUSTOM_NAME_VISIBLE = new ReflectField<>(ENTITY_CLASS, "aN").getValue(null);
                DWO_ARMOR_STAND_DATA = new ReflectField<>(ENTITY_ARMOR_STAND_CLASS, "bG").getValue(null);
            } else {
                DWO_ENTITY_DATA = new ReflectField<>(ENTITY_CLASS, "aa").getValue(null);
                DWO_CUSTOM_NAME = new ReflectField<>(ENTITY_CLASS, "aL").getValue(null);
                DWO_CUSTOM_NAME_VISIBLE = new ReflectField<>(ENTITY_CLASS, "aM").getValue(null);
                DWO_ARMOR_STAND_DATA = new ReflectField<>(ENTITY_ARMOR_STAND_CLASS, "bH").getValue(null);
            }
        } else {
            DWO_ENTITY_DATA = new ReflectField<>(ENTITY_CLASS, "Z").getValue(null);
            DWO_CUSTOM_NAME = new ReflectField<>(ENTITY_CLASS, "aJ").getValue(null);
            DWO_CUSTOM_NAME_VISIBLE = new ReflectField<>(ENTITY_CLASS, "aK").getValue(null);
            DWO_ARMOR_STAND_DATA = new ReflectField<>(ENTITY_ARMOR_STAND_CLASS, "bG").getValue(null);
        }
        
        // ENTITY TYPES
        ENTITY_TYPES_A_METHOD = new ReflectMethod(ENTITY_TYPES_CLASS, "a", String.class);
        ENTITY_TYPE_GET_KEY_METHOD = new ReflectMethod(EntityType.class, "getKey");
        if (Version.afterOrEqual(18)) {
            REGISTRY_BLOCKS_GET_ID_METHOD = new ReflectMethod(ReflectionUtil.getNMClass("core.Registry"), "a", Object.class);
        } else {
            REGISTRY_BLOCKS_GET_ID_METHOD = new ReflectMethod(ReflectionUtil.getNMClass("core.Registry"), "getId", Object.class);
        }
        NAMESPACED_KEY_GET_KEY_METHOD = new ReflectMethod(ReflectionUtil.getClass("org.bukkit.NamespacedKey"), "getKey");

        if (Version.afterOrEqual(Version.v1_20_R4)) {
            ENTITY_COUNTER_FIELD = new ReflectField<>(ENTITY_CLASS, "c");
        } else if (Version.afterOrEqual(Version.v1_19_R3)) {
            ENTITY_COUNTER_FIELD = new ReflectField<>(ENTITY_CLASS, "d");
        } else if (Version.CURRENT.equals(Version.v1_18_R2) || Version.afterOrEqual(19)) {
            ENTITY_COUNTER_FIELD = new ReflectField<>(ENTITY_CLASS, "c");
        } else {
            ENTITY_COUNTER_FIELD = new ReflectField<>(ENTITY_CLASS, "b");
        }
        if (Version.afterOrEqual(Version.v1_21_R2)) {
            VEC_3D_A = new ReflectField<>(VEC_3D_CLASS, "c").getValue(null);
		} else if (Version.afterOrEqual(19)) {
			VEC_3D_A = new ReflectField<>(VEC_3D_CLASS, "b").getValue(null);
		} else {
			VEC_3D_A = new ReflectField<>(VEC_3D_CLASS, "a").getValue(null);
		}
    }

    @Override
    public int getFreeEntityId() {
        return ENTITY_COUNTER_FIELD.getValue(null).addAndGet(1);
    }


    public int getEntityTypeId(EntityType type) {
        if (type == null) return -1;
        Object namespacedKey = ENTITY_TYPE_GET_KEY_METHOD.invoke(type);
        String key = NAMESPACED_KEY_GET_KEY_METHOD.invoke(namespacedKey);
        Optional<?> entityTypes = ENTITY_TYPES_A_METHOD.invokeStatic(key.toLowerCase());
        return entityTypes.map(entityType -> {
            Object registryBlocks = ENTITY_TYPES_REGISTRY_FIELD.getValue(null);
            return REGISTRY_BLOCKS_GET_ID_METHOD.<Integer>invoke(registryBlocks, entityType);
        }).orElse(-1);
    }

    //after or equal 1.19
    private void showFakeEntity(Player player, Location location, int entityTypeId, int entityId) {
        Validate.notNull(player);
        Validate.notNull(location);

        if (Version.afterOrEqual(19)) {
            sendPacket(player, PACKET_SPAWN_ENTITY_CONSTRUCTOR.newInstance(
                    entityId,
                    UUID.randomUUID(),
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    location.getYaw(),
                    location.getPitch(),
                    REGISTRY_BLOCKS_FROM_ID_METHOD.invoke(ENTITY_TYPES_REGISTRY_FIELD.getValue(null), entityTypeId),
                    0,
                    VEC_3D_A,
                    location.getYaw()
            ));
        } else {
            sendPacket(player, PACKET_SPAWN_ENTITY_CONSTRUCTOR.newInstance(
                    entityId,
                    UUID.randomUUID(),
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    location.getYaw(),
                    location.getPitch(),
                    REGISTRY_BLOCKS_FROM_ID_METHOD.invoke(ENTITY_TYPES_REGISTRY_FIELD.getValue(null), entityTypeId),
                    0,
                    VEC_3D_A
            ));
        }
        teleportFakeEntity(player, location, entityId);
    }

    //BEFORE 1.19
    private void showFakeEntityLiving(Player player, Location location, int entityTypeId, int entityId) {
        Validate.notNull(player);
        Validate.notNull(location);

        Object packetDataSerializer = PACKET_DATA_SERIALIZER_CONSTRUCTOR.newInstance(Unpooled.buffer());
        PACKET_DATA_SERIALIZER_WRITE_INT_METHOD.invoke(packetDataSerializer, entityId);
        PACKET_DATA_SERIALIZER_WRITE_UUID_METHOD.invoke(packetDataSerializer, MATH_HELPER_A_METHOD.<UUID>invokeStatic(ThreadLocalRandom.current()));
        PACKET_DATA_SERIALIZER_WRITE_INT_METHOD.invoke(packetDataSerializer, entityTypeId);
        initDataPacker(location, packetDataSerializer);
        PACKET_DATA_SERIALIZER_WRITE_BYTE_METHOD.invoke(packetDataSerializer, (byte) ((int) (location.getYaw() * 256.0F / 360.0F)));
        PACKET_DATA_SERIALIZER_WRITE_SHORT_METHOD.invoke(packetDataSerializer, 0);
        PACKET_DATA_SERIALIZER_WRITE_SHORT_METHOD.invoke(packetDataSerializer, 0);
        PACKET_DATA_SERIALIZER_WRITE_SHORT_METHOD.invoke(packetDataSerializer, 0);
        if (PACKET_SPAWN_ENTITY_LIVING_CONSTRUCTOR != null) {
            sendPacket(player, PACKET_SPAWN_ENTITY_LIVING_CONSTRUCTOR.newInstance(packetDataSerializer));
        }
    }

    private static final Class<?> REGISTRY_FRIENDLY_BYTE_BUF_CLASS;
    private static final Class<?> IREGISTRYCUSTOM_CLASS;
    private static final Class<?> BUILTINREGISTRIES_CLASS;
    private static final Class<?> IREGISTRYCUSTOM_C_CLASS;
    private static final ReflectConstructor REGISTRY_FRIENDLY_BYTE_BUF_CONSTRUCTOR;
    private static final ReflectConstructor IREGISTRYCUSTOM_C_CONSTRUCTOR;
    private static final Class<?> CODEC_CLASS;
    private static final ReflectMethod DWS_GET_CODEC_METHOD;
    private static final ReflectMethod CODEC_ENCODE_METHOD;
    private static final Object ITEM_REGISTRY;
    private static final Object DATA_COMPONENT_TYPE_REGISTRY;

    private static final Class<?> DWR_CLASS;
    private static final ReflectMethod DWI_GET_OBJECT_METHOD;
    private static final ReflectMethod DWI_GET_VALUE_METHOD;
    private static final ReflectMethod DWO_GET_SERIALIZER_METHOD;
    private static final ReflectMethod DWO_GET_INDEX_METHOD;
    private static final ReflectMethod DWS_GET_TYPE_ID_METHOD;
    private static final ReflectMethod DWS_SERIALIZE_METHOD;
    private static final ReflectConstructor PACKET_ENTITY_METADATA_CONSTRUCTOR;

    static {
        Class<?> metadataPacketClass = ReflectionUtil.getNMClass("network.protocol.game.PacketPlayOutEntityMetadata");

        DWR_CLASS = ReflectionUtil.getNMClass("network.syncher.DataWatcherRegistry");
        DWI_GET_OBJECT_METHOD = new ReflectMethod(DWI_CLASS, "a");
        DWI_GET_VALUE_METHOD = new ReflectMethod(DWI_CLASS, "b");
        DWO_GET_SERIALIZER_METHOD = new ReflectMethod(DWO_CLASS, "b");
        DWO_GET_INDEX_METHOD = new ReflectMethod(DWO_CLASS, "a");
        DWS_GET_TYPE_ID_METHOD = new ReflectMethod(DWR_CLASS, "b", DWS_CLASS);

        if (Version.afterOrEqual(Version.v1_20_R4)) {
            REGISTRY_FRIENDLY_BYTE_BUF_CLASS = ReflectionUtil.getNMClass("network.RegistryFriendlyByteBuf");
            IREGISTRYCUSTOM_CLASS = ReflectionUtil.getNMClass("core.IRegistryCustom");
            BUILTINREGISTRIES_CLASS = ReflectionUtil.getNMClass(BUILTINREGISTRIES);
            IREGISTRYCUSTOM_C_CLASS = ReflectionUtil.getNMClass("core.IRegistryCustom$c");
            REGISTRY_FRIENDLY_BYTE_BUF_CONSTRUCTOR = new ReflectConstructor(REGISTRY_FRIENDLY_BYTE_BUF_CLASS, ByteBuf.class, IREGISTRYCUSTOM_CLASS);
            IREGISTRYCUSTOM_C_CONSTRUCTOR = new ReflectConstructor(IREGISTRYCUSTOM_C_CLASS, List.class);
            CODEC_CLASS = ReflectionUtil.getNMClass("network.codec.StreamCodec");
            DWS_GET_CODEC_METHOD = new ReflectMethod(DWS_CLASS, "codec");
            CODEC_ENCODE_METHOD = new ReflectMethod(CODEC_CLASS, "encode", Object.class, Object.class);
            if (BUILTINREGISTRIES_CLASS != null) {
                ITEM_REGISTRY = ReflectionUtil.getFieldValue(BUILTINREGISTRIES_CLASS, "h");
                DATA_COMPONENT_TYPE_REGISTRY = ReflectionUtil.getFieldValue(BUILTINREGISTRIES_CLASS, "as");
            }else{
                ITEM_REGISTRY = null;
                DATA_COMPONENT_TYPE_REGISTRY = null;
            }
            DWS_SERIALIZE_METHOD = null;
            PACKET_ENTITY_METADATA_CONSTRUCTOR = new ReflectConstructor(metadataPacketClass, REGISTRY_FRIENDLY_BYTE_BUF_CLASS);
        } else {
            REGISTRY_FRIENDLY_BYTE_BUF_CLASS = null;
            IREGISTRYCUSTOM_CLASS = null;
            BUILTINREGISTRIES_CLASS = null;
            IREGISTRYCUSTOM_C_CLASS = null;
            REGISTRY_FRIENDLY_BYTE_BUF_CONSTRUCTOR = null;
            IREGISTRYCUSTOM_C_CONSTRUCTOR = null;
            CODEC_CLASS = null;
            DWS_GET_CODEC_METHOD = null;
            CODEC_ENCODE_METHOD = null;
            ITEM_REGISTRY = null;
            DATA_COMPONENT_TYPE_REGISTRY = null;

            DWS_SERIALIZE_METHOD = new ReflectMethod(DWS_CLASS, "a", PACKET_DATA_SERIALIZER_CLASS, Object.class);
            PACKET_ENTITY_METADATA_CONSTRUCTOR = new ReflectConstructor(metadataPacketClass, PACKET_DATA_SERIALIZER_CLASS);
        }
    }

    private void sendEntityMetadata(Player player, int entityId, List<Object> items) {
        Validate.notNull(player);
        Validate.notNull(items);

        Object packetDataSerializer = null;
        if (Version.afterOrEqual(Version.v1_20_R4)) {
            Object c = null;
            if (IREGISTRYCUSTOM_C_CONSTRUCTOR != null) {
                c = IREGISTRYCUSTOM_C_CONSTRUCTOR.newInstance(Arrays.asList(ITEM_REGISTRY, DATA_COMPONENT_TYPE_REGISTRY));
            }
            if (REGISTRY_FRIENDLY_BYTE_BUF_CONSTRUCTOR != null) {
                packetDataSerializer = REGISTRY_FRIENDLY_BYTE_BUF_CONSTRUCTOR.newInstance(Unpooled.buffer(), c);
            }
        } else {
            packetDataSerializer = PACKET_DATA_SERIALIZER_CONSTRUCTOR.newInstance(Unpooled.buffer());
        }
        PACKET_DATA_SERIALIZER_WRITE_INT_METHOD.invoke(packetDataSerializer, entityId);
        for (Object item : items) {
            if (DWI_CLASS != null && !item.getClass().isAssignableFrom(DWI_CLASS)) {
                continue;
            }

            Object object = DWI_GET_OBJECT_METHOD.invoke(item);
            Object value = DWI_GET_VALUE_METHOD.invoke(item);
            Object serializer = DWO_GET_SERIALIZER_METHOD.invoke(object);
            int serializerIndex = DWO_GET_INDEX_METHOD.invoke(object);
            int serializerTypeId = DWS_GET_TYPE_ID_METHOD.invokeStatic(serializer);

            PACKET_DATA_SERIALIZER_WRITE_BYTE_METHOD.invoke(packetDataSerializer, (byte) serializerIndex);
            PACKET_DATA_SERIALIZER_WRITE_INT_METHOD.invoke(packetDataSerializer, serializerTypeId);

            if (Version.afterOrEqual(Version.v1_20_R4)) {
                Object codec = null;
                if (DWS_GET_CODEC_METHOD != null) {
                    codec = DWS_GET_CODEC_METHOD.invoke(serializer);
                }
                if (CODEC_ENCODE_METHOD != null) {
                    CODEC_ENCODE_METHOD.invoke(codec, packetDataSerializer, value);
                }
            } else if (DWS_SERIALIZE_METHOD != null) {
                    DWS_SERIALIZE_METHOD.invoke(serializer, packetDataSerializer, value);

            }
        }
        PACKET_DATA_SERIALIZER_WRITE_BYTE_METHOD.invoke(packetDataSerializer, 0xFF);
        sendPacket(player, PACKET_ENTITY_METADATA_CONSTRUCTOR.newInstance(packetDataSerializer));
    }

    @Override
    public void showFakeEntityArmorStand(Player player, Location location, int entityId, boolean invisible, boolean small, boolean clickable) {
        Validate.notNull(player);
        Validate.notNull(location);

        List<Object> dataWatcherItems = new ArrayList<>();
        dataWatcherItems.add(DATA_WATCHER_ITEM_CONSTRUCTOR.newInstance(DWO_ENTITY_DATA, (byte) (invisible ? 0x20 : 0)));
        dataWatcherItems.add(DATA_WATCHER_ITEM_CONSTRUCTOR.newInstance(DWO_ARMOR_STAND_DATA, (byte) (0x08 | (small ? 0x01 : 0) | (clickable ? 0 : 0x10))));
        int entityTypeId = getEntityTypeId(EntityType.ARMOR_STAND);
        if (entityTypeId == -1) return; 
        if (Version.afterOrEqual(19)) {
            showFakeEntity(player, location, entityTypeId, entityId);
        } else {
            showFakeEntityLiving(player, location, entityTypeId, entityId);
        }
        sendEntityMetadata(player, entityId, dataWatcherItems);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void updateFakeEntityCustomName(Player player, String name, int entityId) {
        Validate.notNull(player);
        Validate.notNull(name);

        List<Object> dataWatcherItems = new ArrayList<>();
        dataWatcherItems.add(DATA_WATCHER_ITEM_CONSTRUCTOR.newInstance(DWO_CUSTOM_NAME, Optional.ofNullable(CRAFT_CHAT_MESSAGE_FROM_STRING_METHOD.invokeStatic(name))));
        dataWatcherItems.add(DATA_WATCHER_ITEM_CONSTRUCTOR.newInstance(DWO_CUSTOM_NAME_VISIBLE, !ChatColor.stripColor(name).isEmpty()));
        sendEntityMetadata(player, entityId, dataWatcherItems);
    }


    @Override
    public void teleportFakeEntity(Player player, Location location, int entityId) {
        Validate.notNull(player);
        Validate.notNull(location);

        Object entityTeleportPacket;
        if (Version.afterOrEqual(Version.v1_21_R2)) {
            Object locationVec3d = VEC_3D_CONSTRUCTOR.newInstance(location.getX(), location.getY(), location.getZ());
            Object zeroVec3d = VEC_3D_CONSTRUCTOR.newInstance(0.0, 0.0, 0.0);
            Object positionMoveRotation = POSITION_MOVE_ROTATION_CONSTRUCTOR.newInstance(locationVec3d, zeroVec3d, location.getYaw(), location.getPitch());
            entityTeleportPacket = PACKET_ENTITY_TELEPORT_CONSTRUCTOR.newInstance(entityId, positionMoveRotation, new HashSet<>(), false);
        } else {
            Object packetDataSerializer = PACKET_DATA_SERIALIZER_CONSTRUCTOR.newInstance(Unpooled.buffer());
            PACKET_DATA_SERIALIZER_WRITE_INT_METHOD.invoke(packetDataSerializer, entityId);
            initDataPacker(location, packetDataSerializer);
            PACKET_DATA_SERIALIZER_WRITE_BOOLEAN_METHOD.invoke(packetDataSerializer, false);
            entityTeleportPacket = PACKET_ENTITY_TELEPORT_CONSTRUCTOR.newInstance(packetDataSerializer);
        }
        sendPacket(player, entityTeleportPacket);
    }

    private void initDataPacker(Location location, Object packetDataSerializer) {
        PACKET_DATA_SERIALIZER_WRITE_DOUBLE_METHOD.invoke(packetDataSerializer, location.getX());
        PACKET_DATA_SERIALIZER_WRITE_DOUBLE_METHOD.invoke(packetDataSerializer, location.getY());
        PACKET_DATA_SERIALIZER_WRITE_DOUBLE_METHOD.invoke(packetDataSerializer, location.getZ());
        PACKET_DATA_SERIALIZER_WRITE_BYTE_METHOD.invoke(packetDataSerializer, (byte) ((int) (location.getYaw() * 256.0F / 360.0F)));
        PACKET_DATA_SERIALIZER_WRITE_BYTE_METHOD.invoke(packetDataSerializer, (byte) ((int) (location.getPitch() * 256.0F / 360.0F)));
    }


    @SuppressWarnings("RedundantCast")
    @Override
    public void hideFakeEntities(Player player, int... entityIds) {
        Validate.notNull(player);
        if (Version.CURRENT_MINECRAFT_VERSION.equals("1.17")) {
            for (int entityId : entityIds) {
                sendPacket(player, PACKET_ENTITY_DESTROY_CONSTRUCTOR.newInstance(entityId));
            }
        } else {
            sendPacket(player, PACKET_ENTITY_DESTROY_CONSTRUCTOR.newInstance((Object) entityIds));
        }
    }

}
