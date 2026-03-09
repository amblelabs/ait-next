package dev.amble.ait.datagen.recipe;

import dev.amble.ait.api.AitAPI;
import dev.amble.ait.datagen.IXplatConditionsBuilder;
import dev.amble.ait.datagen.IXplatIngredients;
import dev.amble.lib.datagen.AmbleRecipeProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

import java.util.function.Consumer;
import java.util.function.Function;

public class AitXplatRecipes extends AmbleRecipeProvider {

    private final IXplatIngredients ingredients;
    private final Function<RecipeBuilder, IXplatConditionsBuilder> conditions;

    public AitXplatRecipes(PackOutput output, IXplatIngredients ingredients,
                           Function<RecipeBuilder, IXplatConditionsBuilder> conditions) {
        super(output, AitAPI.MOD_ID);
        this.ingredients = ingredients;
        this.conditions = conditions;
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> recipes) {
    }

    private void specialRecipe(Consumer<FinishedRecipe> consumer, SimpleCraftingRecipeSerializer<?> serializer) {
        var name = BuiltInRegistries.RECIPE_SERIALIZER.getKey(serializer);
        SpecialRecipeBuilder.special(serializer).save(consumer, AitAPI.MOD_ID + ":dynamic" + name.getPath());
    }

    private void stoneCutterFromTag(Consumer<FinishedRecipe> recipes, TagKey<Item> tagKey, Item ...results) {
        for (Item result : results) {
            SingleItemRecipeBuilder.stonecutting(Ingredient.of(tagKey), RecipeCategory.BUILDING_BLOCKS, result)
                    .unlockedBy("has_item", hasItem(tagKey))
                    .save(recipes, modLoc("stonecutting/" + result));
        }
    }
}