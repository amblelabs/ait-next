package dev.amble.lib.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

abstract public class AmbleBlockTagProvider extends TagsProvider<Block> {

    protected AmbleBlockTagProvider(PackOutput packOut, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOut, Registries.BLOCK, lookupProvider);
    }
}