package net.marsh.soupdup.component;

import com.mojang.serialization.Codec;
import net.marsh.soupdup.SoupdUp;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.UnaryOperator;

public class SoupdUpComponentTypes {
    public static final ComponentType<Boolean> TIME_UPGRADED = register(
            "time_upgraded", builder -> builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOLEAN)
    );
    private static <T>ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(SoupdUp.MOD_ID, name),
                builderOperator.apply(ComponentType.builder()).build());
    }
    public static void registerDataComponentTypes(){
        SoupdUp.LOGGER.info("Registering Data Component Types for " + SoupdUp.MOD_ID);
    }
}
