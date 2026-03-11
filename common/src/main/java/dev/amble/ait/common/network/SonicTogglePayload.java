package dev.amble.ait.common.network;

import dev.amble.ait.api.AitAPI;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SonicTogglePayload(boolean opened) implements CustomPacketPayload {

    public static final ResourceLocation ID = AitAPI.modLoc("sonic_toggle");
    public static final Type<SonicTogglePayload> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, SonicTogglePayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.BOOL, SonicTogglePayload::opened, SonicTogglePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
