package dev.amble.ait.common.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * @param function function = crystalIdx * 8 + relFuncIdx
 * @param opened whether the sonic model is visually opened
 */
public record SonicData(int function, boolean opened) {

    public static final Codec<SonicData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("function").forGetter(SonicData::function),
            Codec.BOOL.optionalFieldOf("opened", false).forGetter(SonicData::opened)
    ).apply(instance, SonicData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SonicData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SonicData::function,
            ByteBufCodecs.BOOL, SonicData::opened,
            SonicData::new);

    public static final SonicData DEFAULT = new SonicData(-1, false);

    public SonicData withFunction(int function) {
        return new SonicData(function, this.opened);
    }

    public SonicData withOpened(boolean opened) {
        return new SonicData(this.function, opened);
    }
}
