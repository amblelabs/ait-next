package dev.amble.ait.fabric.network;

import dev.amble.ait.common.network.SetSonicFunctionC2SPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class FabricPacketHandler {

    public static void init() {
        PayloadTypeRegistry.playC2S().register(SetSonicFunctionC2SPacket.TYPE, SetSonicFunctionC2SPacket.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SetSonicFunctionC2SPacket.TYPE, (packet, context) -> {
            context.server().execute(() -> SetSonicFunctionC2SPacket.handle(packet, context.player()));
        });
    }

    public static void initClient() {
    }
}
