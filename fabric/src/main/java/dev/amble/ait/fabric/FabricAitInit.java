package dev.amble.ait.fabric;

import dev.amble.ait.api.mod.AitStatistics;
import dev.amble.ait.common.blocks.behavior.AitComposting;
import dev.amble.ait.common.blocks.behavior.AitStrippables;
import dev.amble.ait.common.lib.*;
import dev.amble.ait.fabric.network.FabricPacketHandler;
import dev.amble.ait.interop.AitInterop;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.function.BiConsumer;

public final class FabricAitInit implements ModInitializer {

    private FabricAitConfig CONFIG;

    @Override
    public void onInitialize() {
        this.CONFIG = FabricAitConfig.setup();
        FabricPacketHandler.init();

        this.initListeners();
        this.initRegistries();

        AitComposting.setup();
        AitStrippables.init();

        AitInterop.init();
    }

    private void initListeners() {
        CommandRegistrationCallback.EVENT.register((dp, a, b) -> {
            AitCommands.register(dp);
        });

        ItemGroupEvents.MODIFY_ENTRIES_ALL.register((tab, entries) -> {
            AitBlocks.registerBlockCreativeTab(entries::accept, tab);
            AitItems.registerItemCreativeTab(entries, tab);
        });
    }

    private void initRegistries() {
        AitBlockSetTypes.registerBlocks(BlockSetType::register);

        AitCreativeTabs.registerCreativeTabs(bind(BuiltInRegistries.CREATIVE_MODE_TAB));

        AitSounds.registerSounds(bind(BuiltInRegistries.SOUND_EVENT));
        AitBlocks.registerBlocks(bind(BuiltInRegistries.BLOCK));
        AitBlocks.registerBlockItems(bind(BuiltInRegistries.ITEM));
        AitBlockEntities.registerTiles(bind(BuiltInRegistries.BLOCK_ENTITY_TYPE));
        AitItems.registerItems(bind(BuiltInRegistries.ITEM));

        AitEntities.registerEntities(bind(BuiltInRegistries.ENTITY_TYPE));
        AitAttributes.register(bind(BuiltInRegistries.ATTRIBUTE));
        AitMobEffects.register(bind(BuiltInRegistries.MOB_EFFECT));
        AitPotions.register(bind(BuiltInRegistries.POTION));

        AitParticles.registerParticles(bind(BuiltInRegistries.PARTICLE_TYPE));

        AitLootFunctions.registerSerializers(bind(BuiltInRegistries.LOOT_FUNCTION_TYPE));

        this.dieInAFire();

        AitStatistics.register();
    }

    private void dieInAFire() {
        var flameOn = FlammableBlockRegistry.getDefaultInstance();
    }

    private <T> BiConsumer<T, ResourceLocation> bind(Registry<T> registry) {
        return (t, id) -> Registry.register(registry, id, t);
    }
}
