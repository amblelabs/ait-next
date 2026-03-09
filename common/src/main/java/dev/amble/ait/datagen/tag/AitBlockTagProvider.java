package dev.amble.ait.datagen.tag;

import dev.amble.ait.xplat.IXplatTags;
import dev.amble.lib.datagen.AmbleBlockTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class AitBlockTagProvider extends AmbleBlockTagProvider {
    public final IXplatTags xtags;

    public AitBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                               IXplatTags xtags) {
        super(output, lookupProvider);
        this.xtags = xtags;
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

    }
}