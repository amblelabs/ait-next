package dev.amble.ait.common.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * @param function function = crystalIdx * 8 + relFuncIdx
 */
public record SonicData(int function) {
    
    public static final Codec<SonicData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("function").forGetter(SonicData::function)
    ).apply(instance, SonicData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SonicData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SonicData::function, SonicData::new);

    public static final SonicData DEFAULT = new SonicData(-1);
}
