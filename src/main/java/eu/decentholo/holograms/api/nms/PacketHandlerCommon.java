package eu.decentholo.holograms.api.nms;

import eu.decentholo.holograms.api.utils.reflect.ReflectConstructor;
import eu.decentholo.holograms.api.utils.reflect.ReflectField;
import eu.decentholo.holograms.api.utils.reflect.ReflectMethod;
import eu.decentholo.holograms.api.utils.reflect.ReflectionUtil;
import eu.decentholo.holograms.api.utils.reflect.Version;
import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class PacketHandlerCommon {

    private static final Class<?> ENTITY_USE_PACKET_CLASS;
    private static final ReflectField<Integer> ENTITY_USE_PACKET_ID_FIELD;
    private static final Class<?> PACKET_DATA_SERIALIZER_CLASS;
    private static final ReflectConstructor PACKET_DATA_SERIALIZER_CONSTRUCTOR;
    private static final ReflectMethod ENTITY_USE_PACKET_A_METHOD;
    private static final ReflectMethod PACKET_DATA_SERIALIZER_READ_INT_METHOD;

    static {
        if (Version.afterOrEqual(17)) {
            ENTITY_USE_PACKET_CLASS = ReflectionUtil.getNMClass("network.protocol.game.PacketPlayInUseEntity");
            PACKET_DATA_SERIALIZER_CLASS = ReflectionUtil.getNMClass("network.PacketDataSerializer");
        } else {
            ENTITY_USE_PACKET_CLASS = ReflectionUtil.getNMSClass("PacketPlayInUseEntity");
            PACKET_DATA_SERIALIZER_CLASS = ReflectionUtil.getNMSClass("PacketDataSerializer");
        }
        if (Version.afterOrEqual(Version.v1_20_R4)) {
            ENTITY_USE_PACKET_ID_FIELD = new ReflectField<>(ENTITY_USE_PACKET_CLASS, "b");
        } else {
            ENTITY_USE_PACKET_ID_FIELD = new ReflectField<>(ENTITY_USE_PACKET_CLASS, "a");
        }
        PACKET_DATA_SERIALIZER_CONSTRUCTOR = new ReflectConstructor(PACKET_DATA_SERIALIZER_CLASS, ByteBuf.class);
        if (Version.afterOrEqual(Version.v1_20_R4)) {
            PACKET_DATA_SERIALIZER_READ_INT_METHOD = new ReflectMethod(PACKET_DATA_SERIALIZER_CLASS, "l");
        } else if (Version.afterOrEqual(Version.v1_20_R3)) {
            PACKET_DATA_SERIALIZER_READ_INT_METHOD = new ReflectMethod(PACKET_DATA_SERIALIZER_CLASS, "n");
        } else if (Version.afterOrEqual(Version.v1_19_R3)) {
            PACKET_DATA_SERIALIZER_READ_INT_METHOD = new ReflectMethod(PACKET_DATA_SERIALIZER_CLASS, "m");
        } else if (Version.afterOrEqual(19)) {
            PACKET_DATA_SERIALIZER_READ_INT_METHOD = new ReflectMethod(PACKET_DATA_SERIALIZER_CLASS, "k");
        } else if (Version.afterOrEqual(17)) {
            PACKET_DATA_SERIALIZER_READ_INT_METHOD = new ReflectMethod(PACKET_DATA_SERIALIZER_CLASS, "j");
        } else if (Version.afterOrEqual(14)) {
            PACKET_DATA_SERIALIZER_READ_INT_METHOD = new ReflectMethod(PACKET_DATA_SERIALIZER_CLASS, "i");
        } else if (Version.afterOrEqual(9)) {
            PACKET_DATA_SERIALIZER_READ_INT_METHOD = new ReflectMethod(PACKET_DATA_SERIALIZER_CLASS, "g");
        } else {
            PACKET_DATA_SERIALIZER_READ_INT_METHOD = new ReflectMethod(PACKET_DATA_SERIALIZER_CLASS, "e");
        }
        if (Version.afterOrEqual(17)) {
            ENTITY_USE_PACKET_A_METHOD = new ReflectMethod(ENTITY_USE_PACKET_CLASS, "a", PACKET_DATA_SERIALIZER_CLASS);
        } else {
            ENTITY_USE_PACKET_A_METHOD = new ReflectMethod(ENTITY_USE_PACKET_CLASS, "b", PACKET_DATA_SERIALIZER_CLASS);
        }
    }

}
