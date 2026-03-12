package dev.amble.ait.fabric.datagen;

import dev.amble.ait.datagen.AitLootTables;
import dev.amble.ait.datagen.AitAdvancements;
import dev.amble.ait.datagen.IXplatIngredients;
import dev.amble.ait.datagen.recipe.AitXplatRecipes;
import dev.amble.ait.fabric.datagen.lang.FabricAitLangProvider;
import dev.amble.ait.fabric.datagen.tag.AitBlockTagProvider;
import dev.amble.ait.fabric.datagen.tag.AitItemTagProvider;
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

@SuppressWarnings({"SameParameterValue", "unused"})
public class FabricAitDataGenerators implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator gen) {
        var pack = gen.createPack();
        var tags = IXplatAbstractions.INSTANCE.tags();

        pack.addProvider((output, lookup) -> new AitXplatRecipes(
                output, lookup, INGREDIENTS));

        var blockTags = new BlockTagProviderWrapper();
        pack.addProvider((output, lookup) -> blockTags.provider = new AitBlockTagProvider(output, lookup, tags));
        pack.addProvider((output, lookup) -> new AitItemTagProvider(output, lookup, blockTags.provider, tags));

        pack.addProvider((output, lookup) -> new LootTableProvider(
                output, Set.of(), List.of(new LootTableProvider.SubProviderEntry(provider -> new AitLootTables(),
                LootContextParamSets.ALL_PARAMS)), lookup
        ));

        pack.addProvider((output, lookup) -> new AdvancementProvider(
                output, lookup, List.of(new AitAdvancements())
        ));

        pack.addProvider((output, lookup) -> new FabricAitModelProvider(output));

        pack.addProvider(FabricAitLangProvider.EnUs::new);
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
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(namespace, s));
    }
}