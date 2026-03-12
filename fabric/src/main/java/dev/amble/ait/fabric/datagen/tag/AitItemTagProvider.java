package dev.amble.ait.fabric.datagen.tag;

import dev.amble.ait.api.mod.AitTags;
import dev.amble.ait.common.lib.AitItems;
import dev.amble.ait.xplat.IXplatTags;
import dev.amble.lib.fabric.datagen.FabricAmbleItemTagProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class AitItemTagProvider extends FabricAmbleItemTagProvider {
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final IXplatTags xtags;

    public AitItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture, @Nullable BlockTagProvider blockTagProvider, IXplatTags xtags) {
        super(output, completableFuture, blockTagProvider);

        this.xtags = xtags;
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        getOrCreateTagBuilder(AitTags.Items.ZEITON_SHARDS).add(
                AitItems.SHARD_AMETHYST, AitItems.SHARD_BASIC, AitItems.SHARD_GRAVITY,
                AitItems.SHARD_OVERCHARGED, AitItems.SHARD_QUARTZ, AitItems.SHARD_REFRACTION,
                AitItems.SHARD_RESONATING, AitItems.SHARD_SCULK
        );
    }
}