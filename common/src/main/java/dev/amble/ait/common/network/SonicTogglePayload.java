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

public record SonicTogglePayload(boolean opened) implements CustomPacketPayload {

    public static final ResourceLocation ID = AitAPI.modLoc("sonic/toggle");
    public static final Type<SonicTogglePayload> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, SonicTogglePayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.BOOL, SonicTogglePayload::opened, SonicTogglePayload::new);

    public void handle(MinecraftServer server, ServerPlayer player) {
        ItemStack mainHand = player.getMainHandItem();

        if (mainHand.getItem() instanceof ItemSonic)
            ItemSonic.setOpened(mainHand, opened);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
