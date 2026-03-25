package dev.polaris_light.constructionwand.crafting;

import dev.polaris_light.constructionwand.ConstructionWand;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipes {
    private ModRecipes() {
    }

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, ConstructionWand.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RecipeWandUpgrade>> WAND_UPGRADE =
            RECIPE_SERIALIZERS.register("wand_upgrade", () -> RecipeWandUpgrade.SERIALIZER);
}
