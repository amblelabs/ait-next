package dev.amble.ait.fabric.datagen;

import dev.amble.ait.common.lib.AitBlocks;
import dev.amble.ait.common.lib.AitItems;
import dev.amble.lib.fabric.datagen.FabricAmbleModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;

public class FabricAitModelProvider extends FabricAmbleModelProvider {

    public FabricAitModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators gen) {
        // will use later
    }

    @Override
    public void generateItemModels(ItemModelGenerators gen) {
        gen.generateFlatItem(AitItems.SCREWDRIVER, ModelTemplates.FLAT_HANDHELD_ROD_ITEM);

        // Police box item
        gen.generateFlatItem(AitBlocks.EXTERIOR_BLOCK.asItem(), ModelTemplates.FLAT_ITEM);

        // Zeiton shards
        gen.generateFlatItem(AitItems.SHARD_AMETHYST, ModelTemplates.FLAT_ITEM);
        gen.generateFlatItem(AitItems.SHARD_BASIC, ModelTemplates.FLAT_ITEM);
        gen.generateFlatItem(AitItems.SHARD_GRAVITY, ModelTemplates.FLAT_ITEM);
        gen.generateFlatItem(AitItems.SHARD_OVERCHARGED, ModelTemplates.FLAT_ITEM);
        gen.generateFlatItem(AitItems.SHARD_QUARTZ, ModelTemplates.FLAT_ITEM);
        gen.generateFlatItem(AitItems.SHARD_REFRACTION, ModelTemplates.FLAT_ITEM);
        gen.generateFlatItem(AitItems.SHARD_RESONATING, ModelTemplates.FLAT_ITEM);
        gen.generateFlatItem(AitItems.SHARD_SCULK, ModelTemplates.FLAT_ITEM);
    }
}
