package dev.amble.ait.datagen.recipe;

import dev.amble.ait.api.AitAPI;
import dev.amble.ait.datagen.IXplatIngredients;
import dev.amble.lib.datagen.AmbleRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class AitXplatRecipes extends AmbleRecipeProvider {

    @SuppressWarnings("FieldCanBeLocal")
    private final IXplatIngredients ingredients;

    public AitXplatRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> future, IXplatIngredients ingredients) {
        super(output, future, AitAPI.MOD_ID);

        this.ingredients = ingredients;
    }

    @Override
    public void buildRecipes(RecipeOutput recipeOutput) {

    }

    private void stoneCutterFromTag(RecipeOutput recipes, TagKey<Item> tagKey, Item... results) {
        for (Item result : results) {
            SingleItemRecipeBuilder.stonecutting(Ingredient.of(tagKey), RecipeCategory.BUILDING_BLOCKS, result)
                    .unlockedBy("has_item", hasItem(tagKey))
                    .save(recipes, modLoc("stonecutting/" + result));
        }
    }
}