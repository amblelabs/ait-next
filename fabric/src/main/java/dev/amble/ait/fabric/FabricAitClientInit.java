package dev.amble.ait.fabric;

import dev.amble.ait.client.AitKeybinds;
import dev.amble.ait.client.SonicInputHandler;
import dev.amble.ait.client.model.AitModelLayers;
import dev.amble.ait.client.renderer.AitAdditionalRenderers;
import dev.amble.ait.common.lib.AitParticles;
import dev.amble.ait.fabric.client.RegisterClientStuff;
import dev.amble.ait.fabric.network.FabricPacketHandler;
import dev.amble.ait.interop.AitInterop;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Function;

public final class FabricAitClientInit implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FabricPacketHandler.initClient();

        HudRenderCallback.EVENT.register(AitAdditionalRenderers::overlayGui);

        RegisterClientStuff.init();

        KeyBindingHelper.registerKeyBinding(AitKeybinds.SONIC_WHEEL);
        KeyBindingHelper.registerKeyBinding(AitKeybinds.SONIC_TOGGLE);

        ClientTickEvents.END_CLIENT_TICK.register(SonicInputHandler::clientTick);

        AitModelLayers.init((loc, defn) -> {
            EntityModelLayerRegistry.registerModelLayer(loc, defn::get);
        });

        AitParticles.FactoryHandler.registerFactories(new AitParticles.FactoryHandler.Consumer() {
            @Override
            public <T extends ParticleOptions> void register(ParticleType<T> type, Function<SpriteSet, ParticleProvider<T>> constructor) {
                ParticleFactoryRegistry.getInstance().register(type, constructor::apply);
            }
        });

        RegisterClientStuff.registerBlockEntityRenderers(BlockEntityRenderers::register);

        AitInterop.clientInit();
        RegisterClientStuff.registerColorProviders(
                ColorProviderRegistry.ITEM::register,
                ColorProviderRegistry.BLOCK::register
        );
    }
}
