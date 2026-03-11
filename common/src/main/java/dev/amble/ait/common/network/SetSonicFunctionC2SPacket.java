package dev.amble.ait.common.network;

import dev.amble.ait.api.AitAPI;
import dev.amble.ait.common.items.ItemSonic;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public record SetSonicFunctionC2SPacket(int funcIdx) implements CustomPacketPayload {

    public static final Type<SetSonicFunctionC2SPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(AitAPI.MOD_ID, "set_sonic_function")
    );

    public static final StreamCodec<FriendlyByteBuf, SetSonicFunctionC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SetSonicFunctionC2SPacket::funcIdx, SetSonicFunctionC2SPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SetSonicFunctionC2SPacket packet, ServerPlayer player) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        ItemStack sonic = mainHand.getItem() instanceof ItemSonic ? mainHand
                : offHand.getItem() instanceof ItemSonic ? offHand : null;

        if (sonic == null) return;

        ItemSonic.setFunction(sonic, packet.funcIdx());
    }
}

