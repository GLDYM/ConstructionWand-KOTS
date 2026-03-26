package dev.polaris_light.constructionwand.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import dev.polaris_light.constructionwand.api.IWandCore;
import dev.polaris_light.constructionwand.basics.ConfigServer;
import dev.polaris_light.constructionwand.basics.option.WandOptions;
import dev.polaris_light.constructionwand.items.wand.ItemWand;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import javax.annotation.Nonnull;

public class RecipeWandUpgrade extends CustomRecipe
{
    public static final MapCodec<RecipeWandUpgrade> CODEC = MapCodec.unit(new RecipeWandUpgrade());
    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeWandUpgrade> STREAM_CODEC = NeoForgeStreamCodecs.uncheckedUnit(new RecipeWandUpgrade());
    public static final RecipeSerializer<RecipeWandUpgrade> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

    public RecipeWandUpgrade() {
        super();
    }

    @Override
    public boolean matches(@Nonnull CraftingInput inv, @Nonnull Level worldIn) {
        ItemStack wand = null;
        IWandCore upgrade = null;

        for(int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getItem(i);
            if(!stack.isEmpty()) {
                if(wand == null && stack.getItem() instanceof ItemWand) wand = stack;
                else if(upgrade == null && stack.getItem() instanceof IWandCore)
                    upgrade = (IWandCore) stack.getItem();
                else return false;
            }
        }

        if(wand == null || upgrade == null) return false;
        Identifier upgradeId = upgrade.getRegistryName();
        if (upgradeId == null) return false;

        ConfigServer.WandProperties properties = ConfigServer.getWandProperties(wand.getItem());
        boolean canUpgrade = properties != ConfigServer.WandProperties.DEFAULT ? properties.isUpgradeable() : true;

        return !hasUpgrade(wand, upgradeId) && canUpgrade;
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull CraftingInput inv) {
        ItemStack wand = null;
        IWandCore upgrade = null;

        for(int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getItem(i);
            if(!stack.isEmpty()) {
                if(stack.getItem() instanceof ItemWand) wand = stack;
                else if(stack.getItem() instanceof IWandCore) upgrade = (IWandCore) stack.getItem();
            }
        }

        if(wand == null || upgrade == null) return ItemStack.EMPTY;

        ItemStack newWand = wand.copy();
        new WandOptions(newWand).addUpgrade(upgrade);
        return newWand;
    }

    // @Override
    // public boolean canCraftInDimensions(int width, int height) {
    //     return width * height >= 2;
    // }

    @Nonnull
    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return ModRecipes.WAND_UPGRADE.get();
    }

    private static boolean hasUpgrade(ItemStack wand, Identifier upgradeId) {
        CompoundTag root = wand.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!root.contains("wand_options")) return false;

        CompoundTag options = root.getCompound("wand_options").orElse(new CompoundTag());
        ListTag upgrades = options.getList("cores").orElse(new ListTag());
        String target = upgradeId.toString();

        for (int i = 0; i < upgrades.size(); i++) {
            if (target.equals(upgrades.getString(i))) {
                return true;
            }
        }

        return false;
    }
}
