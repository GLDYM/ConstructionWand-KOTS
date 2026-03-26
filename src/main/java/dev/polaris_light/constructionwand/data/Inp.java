package dev.polaris_light.constructionwand.data;

import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public record Inp(String name, Ingredient ingredient, ItemPredicate predicate) {
    public static Inp fromItem(HolderLookup.Provider registries, ItemLike in) {
        return new Inp(BuiltInRegistries.ITEM.getKey(in.asItem()).getPath(), Ingredient.of(in),
                ItemPredicate.Builder.item().of(registries.lookupOrThrow(Registries.ITEM), in).build());
    }

    public static Inp fromTag(HolderLookup.Provider registries, TagKey<Item> in) {
        return new Inp(in.location().getPath(), Ingredient.of(tagSet(registries, in)),
                ItemPredicate.Builder.item().of(registries.lookupOrThrow(Registries.ITEM), in).build());
    }

    private static HolderSet<Item> tagSet(HolderLookup.Provider registries, TagKey<Item> tagKey) {
        return registries.lookupOrThrow(Registries.ITEM).getOrThrow(tagKey);
    }
}