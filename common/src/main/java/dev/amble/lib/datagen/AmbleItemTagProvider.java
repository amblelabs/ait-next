package dev.amble.lib.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public abstract class AmbleItemTagProvider extends TagsProvider<Item> {

    protected AmbleItemTagProvider(PackOutput pack, CompletableFuture<HolderLookup.Provider> lookup, String modId,
                                   TagsProvider<Block> getBuilder) {
        super(pack, Registries.ITEM, lookup);
    }
}