package dev.polaris_light.constructionwand.data;

import net.minecraft.data.PackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import dev.polaris_light.constructionwand.ConstructionWand;
import dev.polaris_light.constructionwand.items.ModItems;

import org.jetbrains.annotations.NotNull;

public class ItemModelGenerator extends ModelProvider
{
    public ItemModelGenerator(PackOutput packOutput) {
        super(packOutput, ConstructionWand.MODID);
    }

    @Override
    protected void registerModels(@NotNull BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
        for(DeferredHolder<Item, ? extends Item> itemObject : ModItems.ITEMS.getEntries()) {
            Item item = itemObject.get();
            String name = itemObject.getId().getPath();

            if(item instanceof BlockItem)
                blockModels.registerSimpleItemModel(item, ConstructionWand.loc("block/" + name));
            else itemModels.generateFlatItem(item, net.minecraft.client.data.models.model.ModelTemplates.FLAT_ITEM);
        }
    }

    @Override
    public String getName() {
        return ConstructionWand.MODNAME + " item models";
    }
}
