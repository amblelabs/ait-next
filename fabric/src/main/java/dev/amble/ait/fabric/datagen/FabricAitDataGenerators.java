package dev.amble.ait.fabric.datagen;

import dev.amble.ait.datagen.AitLootTables;
import dev.amble.ait.datagen.AitAdvancements;
import dev.amble.ait.datagen.IXplatIngredients;
import dev.amble.ait.datagen.recipe.AitXplatRecipes;
import dev.amble.ait.datagen.tag.AitBlockTagProvider;
import dev.amble.ait.datagen.tag.AitItemTagProvider;
import dev.amble.ait.xplat.IXplatAbstractions;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;

public class FabricAitDataGenerators implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator gen) {
        var pack = gen.createPack();
        var tags = IXplatAbstractions.INSTANCE.tags();

        pack.addProvider((FabricDataGenerator.Pack.Factory<AitXplatRecipes>) x -> new AitXplatRecipes(
                x, INGREDIENTS, FabricAitConditionsBuilder::new));

        var blockTags = new BlockTagProviderWrapper();
        pack.addProvider((output, lookup) -> blockTags.provider = new AitBlockTagProvider(output, lookup, tags));
        pack.addProvider((output, lookup) -> new AitItemTagProvider(output, lookup, blockTags.provider, tags));

        pack.addProvider((FabricDataGenerator.Pack.Factory<LootTableProvider>) output -> new LootTableProvider(
                output, Set.of(), List.of(new LootTableProvider.SubProviderEntry(AitLootTables::new, LootContextParamSets.ALL_PARAMS))
        ));

        pack.addProvider((output, lookup) -> new AdvancementProvider(
                output, lookup, List.of(new AitAdvancements())
        ));

        pack.addProvider((output, lookup) -> new FabricAitModels(output));
    }

    private static class BlockTagProviderWrapper {
        AitBlockTagProvider provider;
    }

    private static final IXplatIngredients INGREDIENTS = new IXplatIngredients() {

    };

    private static TagKey<Item> tag(String s) {
        return tag("c", s);
    }

    private static TagKey<Item> tag(String namespace, String s) {
        return TagKey.create(Registries.ITEM, new ResourceLocation(namespace, s));
    }
}