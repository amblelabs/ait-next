package dev.amble.liquor.fabric;

import dev.amble.liquor.api.mod.LiquorStatistics;
import dev.amble.liquor.common.blocks.behavior.LiquorComposting;
import dev.amble.liquor.common.blocks.behavior.LiquorStrippables;
import dev.amble.liquor.common.lib.*;
import dev.amble.liquor.fabric.network.FabricPacketHandler;
import dev.amble.liquor.interop.LiquorInterop;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.function.BiConsumer;

public final class FabricLiquorInit implements ModInitializer {

    private FabricLiquorConfig CONFIG;

    @Override
    public void onInitialize() {
        this.CONFIG = FabricLiquorConfig.setup();
        FabricPacketHandler.init();

        this.initListeners();
        this.initRegistries();

        LiquorComposting.setup();
        LiquorStrippables.init();

        LiquorInterop.init();
    }

    private void initListeners() {
        CommandRegistrationCallback.EVENT.register((dp, a, b) -> {
            LiquorCommands.register(dp);
        });

        ItemGroupEvents.MODIFY_ENTRIES_ALL.register((tab, entries) -> {
            LiquorBlocks.registerBlockCreativeTab(entries::accept, tab);
            LiquorItems.registerItemCreativeTab(entries, tab);
        });
    }

    private void initRegistries() {
        LiquorBlockSetTypes.registerBlocks(BlockSetType::register);

        LiquorCreativeTabs.registerCreativeTabs(bind(BuiltInRegistries.CREATIVE_MODE_TAB));

        LiquorSounds.registerSounds(bind(BuiltInRegistries.SOUND_EVENT));
        LiquorBlocks.registerBlocks(bind(BuiltInRegistries.BLOCK));
        LiquorBlocks.registerBlockItems(bind(BuiltInRegistries.ITEM));
        LiquorBlockEntities.registerTiles(bind(BuiltInRegistries.BLOCK_ENTITY_TYPE));
        LiquorItems.registerItems(bind(BuiltInRegistries.ITEM));

        LiquorEntities.registerEntities(bind(BuiltInRegistries.ENTITY_TYPE));
        LiquorAttributes.register(bind(BuiltInRegistries.ATTRIBUTE));
        LiquorMobEffects.register(bind(BuiltInRegistries.MOB_EFFECT));
        LiquorPotions.register(bind(BuiltInRegistries.POTION));

        LiquorParticles.registerParticles(bind(BuiltInRegistries.PARTICLE_TYPE));

        LiquorLootFunctions.registerSerializers(bind(BuiltInRegistries.LOOT_FUNCTION_TYPE));

        this.dieInAFire();

        LiquorStatistics.register();
    }

    private void dieInAFire() {
        var flameOn = FlammableBlockRegistry.getDefaultInstance();
    }

    private <T> BiConsumer<T, ResourceLocation> bind(Registry<T> registry) {
        return (t, id) -> Registry.register(registry, id, t);
    }
}
