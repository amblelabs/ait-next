package dev.amble.ait.datagen.tag;

import dev.amble.ait.api.AitAPI;
import dev.amble.ait.xplat.IXplatTags;
import dev.amble.lib.datagen.AmbleItemTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class AitItemTagProvider extends AmbleItemTagProvider {
    private final IXplatTags xtags;

    public AitItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, TagsProvider<Block> pBlockTagsProvider, IXplatTags xtags) {
        super(output, lookup, AitAPI.MOD_ID, pBlockTagsProvider);
        this.xtags = xtags;
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
    }
}