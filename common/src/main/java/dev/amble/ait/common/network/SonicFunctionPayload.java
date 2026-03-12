package dev.amble.ait.common.network;

import dev.amble.ait.api.AitAPI;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SonicFunctionPayload(int funcIdx) implements CustomPacketPayload {

    public static final ResourceLocation ID = AitAPI.modLoc("sonic/func");
    public static final Type<SonicFunctionPayload> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, SonicFunctionPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, SonicFunctionPayload::funcIdx, SonicFunctionPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
