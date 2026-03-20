package dev.amble.ait.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;

public record ArtronItemData(int maxCharge, int charge) {

    public static final Codec<ArtronItemData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("max_charge").forGetter(ArtronItemData::maxCharge),
            Codec.INT.fieldOf("charge").forGetter(ArtronItemData::charge)
    ).apply(instance, ArtronItemData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ArtronItemData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ArtronItemData::maxCharge,
            ByteBufCodecs.INT, ArtronItemData::charge,
            ArtronItemData::new);

    public static ArtronItemData withMaxCharge(int maxCharge) {
        return new ArtronItemData(maxCharge, 0);
    }

    public ArtronItemData remove(int charge) {
        return this.add(-charge);
    }

    public ArtronItemData add(int charge) {
        return new ArtronItemData(this.maxCharge, Mth.clamp(this.charge + charge, 0, this.maxCharge));
    }

    public boolean charged() {
        return this.charge > 0;
    }

    public boolean empty() {
        return this.charge == 0;
    }
}
