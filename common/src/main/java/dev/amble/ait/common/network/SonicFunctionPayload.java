package dev.amble.ait.common.network;

import dev.amble.ait.api.AitAPI;
import dev.amble.ait.common.items.ItemSonic;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public record SonicFunctionPayload(int funcIdx) implements CustomPacketPayload {

    public static final ResourceLocation ID = AitAPI.modLoc("sonic/func");
    public static final Type<SonicFunctionPayload> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, SonicFunctionPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, SonicFunctionPayload::funcIdx, SonicFunctionPayload::new);

    public void handle(MinecraftServer server, ServerPlayer player) {
        ItemStack mainHand = player.getMainHandItem();

        if (mainHand.getItem() instanceof ItemSonic)
            ItemSonic.setFunction(mainHand, funcIdx);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
