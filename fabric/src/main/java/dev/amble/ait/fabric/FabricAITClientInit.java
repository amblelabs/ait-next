package dev.amble.liquor.fabric;

import dev.amble.liquor.client.model.LiquorModelLayers;
import dev.amble.liquor.client.renderer.LiquorAdditionalRenderers;
import dev.amble.liquor.common.lib.LiquorParticles;
import dev.amble.liquor.fabric.client.RegisterClientStuff;
import dev.amble.liquor.fabric.network.FabricPacketHandler;
import dev.amble.liquor.interop.LiquorInterop;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Function;

public final class FabricLiquorClientInit implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FabricPacketHandler.initClient();

        HudRenderCallback.EVENT.register(LiquorAdditionalRenderers::overlayGui);

        RegisterClientStuff.init();

        LiquorModelLayers.init((loc, defn) -> {
            EntityModelLayerRegistry.registerModelLayer(loc, defn::get);
        });

        LiquorParticles.FactoryHandler.registerFactories(new LiquorParticles.FactoryHandler.Consumer() {
            @Override
            public <T extends ParticleOptions> void register(ParticleType<T> type, Function<SpriteSet, ParticleProvider<T>> constructor) {
                ParticleFactoryRegistry.getInstance().register(type, constructor::apply);
            }
        });

        RegisterClientStuff.registerBlockEntityRenderers(BlockEntityRenderers::register);

        LiquorInterop.clientInit();
        RegisterClientStuff.registerColorProviders(
                ColorProviderRegistry.ITEM::register,
                ColorProviderRegistry.BLOCK::register
        );
    }
}
