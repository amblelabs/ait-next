package dev.amble.ait.fabric.network;

import dev.amble.ait.common.network.SonicFunctionPayload;
import dev.amble.ait.common.network.SonicTogglePayload;
import dev.amble.ait.common.network.tardis.manager.TardisSyncPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Function;

public class FabricPacketHandler {

    public static void init() {
        PayloadTypeRegistry.playC2S().register(SonicTogglePayload.TYPE, SonicTogglePayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(SonicFunctionPayload.TYPE, SonicFunctionPayload.STREAM_CODEC);

        PayloadTypeRegistry.playS2C().register(TardisSyncPayload.TYPE, TardisSyncPayload.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SonicTogglePayload.TYPE, c2s(p -> p::handle));
        ServerPlayNetworking.registerGlobalReceiver(SonicFunctionPayload.TYPE, c2s(p -> p::handle));
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(TardisSyncPayload.TYPE, s2c(p -> p::handle));
    }

    private static <T extends CustomPacketPayload> ServerPlayNetworking.PlayPayloadHandler<T> c2s(Function<T, C2SPacketHandler> handler) {
        return (payload, context) -> context.server().execute(() -> handler.apply(payload).handle(context.server(), context.player()));
    }

    @Environment(EnvType.CLIENT)
    private static <T extends CustomPacketPayload> ClientPlayNetworking.PlayPayloadHandler<T> s2c(Function<T, S2CPacketHandler> handler) {
        return (payload, context) -> handler.apply(payload).handle(context.client(), context.player());
    }

    // TODO: move these to common side
    @FunctionalInterface
    interface C2SPacketHandler {
        void handle(MinecraftServer server, ServerPlayer player);
    }

    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    interface S2CPacketHandler {
        void handle(Minecraft minecraft, LocalPlayer player);
    }
}
