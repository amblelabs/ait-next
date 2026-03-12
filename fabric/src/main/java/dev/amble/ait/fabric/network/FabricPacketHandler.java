package dev.amble.ait.fabric.network;

import dev.amble.ait.common.items.ItemSonic;
import dev.amble.ait.common.network.SonicFunctionPayload;
import dev.amble.ait.common.network.SonicTogglePayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.item.ItemStack;

public class FabricPacketHandler {

    public static void init() {
        PayloadTypeRegistry.playC2S().register(SonicTogglePayload.TYPE, SonicTogglePayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(SonicFunctionPayload.TYPE, SonicFunctionPayload.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SonicTogglePayload.TYPE, (payload, context) -> context.player().server.execute(() -> {
            ItemStack mainHand = context.player().getMainHandItem();

            if (mainHand.getItem() instanceof ItemSonic) {
                ItemSonic.setOpened(mainHand, payload.opened());
            }
        }));

        ServerPlayNetworking.registerGlobalReceiver(SonicFunctionPayload.TYPE, (payload, context) -> context.player().server.execute(() -> {
            ItemStack mainHand = context.player().getMainHandItem();

            if (mainHand.getItem() instanceof ItemSonic) {
                ItemSonic.setFunction(mainHand, payload.funcIdx());
            }
        }));
    }

    public static void initClient() {
    }
}
