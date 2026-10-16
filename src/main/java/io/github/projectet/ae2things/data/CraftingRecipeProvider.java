package io.github.projectet.ae2things.data;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import io.github.projectet.ae2things.AE2Things;
import io.github.projectet.ae2things.item.AETItems;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.neoforged.neoforge.common.Tags;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;

// Fleet port (26.1): recipe providers are created per run by a RecipeProvider.Runner; builders are instance methods.
public class CraftingRecipeProvider extends RecipeProvider {
    public CraftingRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        driveRecipes(output);
        housingRecipe(output);
    }

    public static final class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new CraftingRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "AE2 Things Recipes";
        }
    }

    private void housingRecipe(RecipeOutput output) {
        shaped(RecipeCategory.MISC, AETItems.DISK_HOUSING)
                .pattern("aba")
                .pattern("b b")
                .pattern("ded")
                .define('a', AEBlocks.QUARTZ_GLASS)
                .define('b', Tags.Items.DUSTS_REDSTONE)
                .define('d', Tags.Items.INGOTS_NETHERITE)
                .define('e', Tags.Items.GEMS_AMETHYST)
                .unlockedBy("has_netherite", has(Tags.Items.INGOTS_NETHERITE))
                .save(output);
    }

    private void driveRecipes(RecipeOutput output) {
        var componentsToDrive = Map.of(
                AEItems.CELL_COMPONENT_1K, AETItems.DISK_DRIVE_1K,
                AEItems.CELL_COMPONENT_4K, AETItems.DISK_DRIVE_4K,
                AEItems.CELL_COMPONENT_16K, AETItems.DISK_DRIVE_16K,
                AEItems.CELL_COMPONENT_64K, AETItems.DISK_DRIVE_64K,
                AEItems.CELL_COMPONENT_256K, AETItems.DISK_DRIVE_256K);

        for (var entry : componentsToDrive.entrySet()) {
            var component = entry.getKey();
            var drive = entry.getValue();

            shaped(RecipeCategory.MISC, drive.get())
                    .pattern("aba")
                    .pattern("bcb")
                    .pattern("ded")
                    .define('a', AEBlocks.QUARTZ_GLASS)
                    .define('b', Tags.Items.DUSTS_REDSTONE)
                    .define('c', component)
                    .define('d', Tags.Items.INGOTS_NETHERITE)
                    .define('e', Tags.Items.GEMS_AMETHYST)
                    .unlockedBy("has_netherite", has(Tags.Items.INGOTS_NETHERITE))
                    .save(output);

            shapeless(RecipeCategory.MISC, drive.get())
                    .requires(AETItems.DISK_HOUSING)
                    .requires(component)
                    .unlockedBy("has_housing", has(AETItems.DISK_HOUSING))
                    .unlockedBy("has_component", has(component))
                    .save(output, drive.getId().withSuffix("_with_housing").toString());
        }
    }
}
