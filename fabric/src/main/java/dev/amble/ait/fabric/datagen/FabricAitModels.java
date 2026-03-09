package dev.amble.ait.fabric.datagen;

import dev.amble.ait.common.lib.AitItems;
import dev.amble.lib.fabric.datagen.FabricAmbleModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;

public class FabricAitModels extends FabricAmbleModelProvider {

    public FabricAitModels(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {

    }

    @Override
    public void generateItemModels(ItemModelGenerators gen) {
        gen.generateFlatItem(AitItems.SCREWDRIVER, ModelTemplates.FLAT_ITEM);
    }
}
