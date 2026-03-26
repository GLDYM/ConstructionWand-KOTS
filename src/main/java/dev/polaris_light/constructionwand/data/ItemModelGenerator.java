package dev.polaris_light.constructionwand.data;

import dev.polaris_light.constructionwand.client.property.CoreColorTintSource;
import dev.polaris_light.constructionwand.client.property.UsingCoreModelProperty;
import net.minecraft.data.PackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import dev.polaris_light.constructionwand.ConstructionWand;
import dev.polaris_light.constructionwand.items.ModItems;
import dev.polaris_light.constructionwand.items.wand.ItemWand;

import org.jetbrains.annotations.NotNull;

public class ItemModelGenerator extends ModelProvider
{
    private static final ModelTemplate TWO_LAYERED_HANDHELD = ModelTemplates.createItem("handheld", TextureSlot.LAYER0, TextureSlot.LAYER1);

    public ItemModelGenerator(PackOutput packOutput) {
        super(packOutput, ConstructionWand.MODID);
    }

    @Override
    protected void registerModels(@NotNull BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
        for(DeferredHolder<Item, ? extends Item> itemObject : ModItems.ITEMS.getEntries()) {
            Item item = itemObject.get();
            String name = itemObject.getId().getPath();

            if(item instanceof ItemWand)
                generateCustomItemModel(itemModels, item);
            else if(item instanceof BlockItem)
                blockModels.registerSimpleItemModel(item, ConstructionWand.loc("block/" + name));
            else itemModels.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
        }
    }

    @Override
    public String getName() {
        return ConstructionWand.MODNAME + " item models";
    }

    public void generateCustomItemModel(ItemModelGenerators itemModels, Item item) {
        Identifier modelLocation = ModelLocationUtils.getModelLocation(item);
        Identifier modelWithCoreLocation = modelLocation.withSuffix("_core");

        itemModels.createFlatItemModel(item, ModelTemplates.FLAT_HANDHELD_ITEM);

        // Generate the base handheld model and an additional *_core layered variant.
        TWO_LAYERED_HANDHELD.create(
                modelWithCoreLocation,
            TextureMapping.layered(new Material(modelLocation), new Material(ConstructionWand.loc("item/overlay_core"))),
                itemModels.modelOutput
        );

        ItemModel.Unbaked baseModel = ItemModelUtils.tintedModel(
            modelLocation,
            ItemModelUtils.constantTint(-1),
            CoreColorTintSource.INSTANCE
        );
        ItemModel.Unbaked coreModel = ItemModelUtils.tintedModel(
            modelWithCoreLocation,
            ItemModelUtils.constantTint(-1),
            CoreColorTintSource.INSTANCE
        );

        itemModels.itemModelOutput.accept(
            item,
            ItemModelUtils.rangeSelect(
                UsingCoreModelProperty.INSTANCE,
                baseModel,
                ItemModelUtils.override(coreModel, 1.0F)
            )
        );
    }
}
