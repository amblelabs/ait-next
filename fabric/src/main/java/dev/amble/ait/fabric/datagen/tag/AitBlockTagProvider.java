package dev.amble.ait.fabric.datagen.tag;

import dev.amble.ait.xplat.IXplatTags;
import dev.amble.lib.fabric.datagen.FabricAmbleBlockTagProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class AitBlockTagProvider extends FabricAmbleBlockTagProvider {
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final IXplatTags xtags;

    public AitBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, IXplatTags xtags) {
        super(output, lookupProvider);
        this.xtags = xtags;
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

    }
}