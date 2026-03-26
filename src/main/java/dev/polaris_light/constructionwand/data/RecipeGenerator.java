package dev.polaris_light.constructionwand.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import dev.polaris_light.constructionwand.items.ModItems;

import java.util.concurrent.CompletableFuture;

public class RecipeGenerator extends RecipeProvider
{
    public RecipeGenerator(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
        super(provider, recipeOutput);
    }

    @Override
    protected void buildRecipes() {
        wandRecipe(ModItems.WAND_STONE.get(), Inp.fromTag(registries, ItemTags.STONE_TOOL_MATERIALS));
        wandRecipe(ModItems.WAND_IRON.get(), Inp.fromTag(registries, Tags.Items.INGOTS_IRON));
        wandRecipe(ModItems.WAND_DIAMOND.get(), Inp.fromTag(registries, Tags.Items.GEMS_DIAMOND));
        wandRecipe(ModItems.WAND_INFINITY.get(), Inp.fromTag(registries, Tags.Items.NETHER_STARS));

        coreRecipe(ModItems.CORE_ANGEL.get(), Inp.fromTag(registries, Tags.Items.FEATHERS), Inp.fromTag(registries, Tags.Items.INGOTS_GOLD));
        coreRecipe(ModItems.CORE_DESTRUCTION.get(), Inp.fromTag(registries, Tags.Items.STORAGE_BLOCKS_DIAMOND), Inp.fromItem(registries, Items.DIAMOND_PICKAXE));

    }

    private void wandRecipe(ItemLike wand, Inp material) {
        shaped(RecipeCategory.TOOLS, wand)
                .define('X', material.ingredient())
                .define('#', Tags.Items.RODS_WOODEN)
                .pattern("  X")
                .pattern(" # ")
                .pattern("#  ")
                .unlockedBy("has_item", inventoryTrigger(material.predicate()))
                .save(output);
    }

    private void coreRecipe(ItemLike core, Inp item1, Inp item2) {
        shaped(RecipeCategory.MISC, core)
                .define('O', item1.ingredient())
                .define('X', item2.ingredient())
                .define('#', Tags.Items.GLASS_PANES)
                .pattern(" #X")
                .pattern("#O#")
                .pattern("X# ")
                .unlockedBy("has_item", inventoryTrigger(item1.predicate()))
                .save(output);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<Provider> completableFuture) {
            super(output, completableFuture);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
            return new RecipeGenerator(provider, recipeOutput);
        }

        @Override
        public String getName() {
            return "Construction Wand Recipes";
        }
    }
}