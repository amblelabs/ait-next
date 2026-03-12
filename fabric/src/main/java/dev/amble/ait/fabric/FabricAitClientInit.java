package dev.amble.ait.fabric;

import dev.amble.ait.api.AitAPI;
import dev.amble.ait.client.AitKeybinds;
import dev.amble.ait.client.SonicInputHandler;
import dev.amble.ait.client.lib.AitClientEcs;
import dev.amble.ait.client.model.AitModelLayers;
import dev.amble.ait.client.renderer.AITShaders;
import dev.amble.ait.client.renderer.AitAdditionalRenderers;
import dev.amble.ait.common.lib.AitParticles;
import dev.amble.ait.fabric.client.RegisterClientStuff;
import dev.amble.ait.fabric.network.FabricPacketHandler;
import dev.amble.ait.interop.AitInterop;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public final class FabricAitClientInit implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FabricPacketHandler.initClient();

        HudRenderCallback.EVENT.register(AitAdditionalRenderers::overlayGui);

        RegisterClientStuff.init();

        KeyBindingHelper.registerKeyBinding(AitKeybinds.SONIC_WHEEL);
        KeyBindingHelper.registerKeyBinding(AitKeybinds.SONIC_TOGGLE);

        ClientTickEvents.END_CLIENT_TICK.register(SonicInputHandler::clientTick);

        AitModelLayers.init((loc, def) -> EntityModelLayerRegistry.registerModelLayer(loc, def::get));

        AitParticles.FactoryHandler.registerFactories((type, constructor) ->
                ParticleFactoryRegistry.getInstance().register(type, constructor::apply));

        RegisterClientStuff.registerBlockEntityRenderers(BlockEntityRenderers::register);

        AitInterop.clientInit();
        RegisterClientStuff.registerColorProviders(
                ColorProviderRegistry.ITEM::register,
                ColorProviderRegistry.BLOCK::register
        );

        AitClientEcs.registerAll();
        CoreShaderRegistrationCallback.EVENT.register(context -> {
            context.register(
                    AitAPI.modLoc("rendertype_accumulation"),
                    DefaultVertexFormat.NEW_ENTITY,
                    shader -> AITShaders.setAccumulationShader(shader)
            );
        });
    }
}
