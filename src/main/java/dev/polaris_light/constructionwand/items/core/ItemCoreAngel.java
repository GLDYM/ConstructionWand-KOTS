package dev.polaris_light.constructionwand.items.core;

import net.minecraft.resources.ResourceLocation;
import dev.polaris_light.constructionwand.ConstructionWand;
import dev.polaris_light.constructionwand.api.IWandAction;
import dev.polaris_light.constructionwand.wand.action.ActionAngel;

public class ItemCoreAngel extends ItemCore
{
    public ItemCoreAngel(Properties properties) {
        super(properties);
    }

    @Override
    public int getColor() {
        return 0xFFE9B115;
    }

    @Override
    public IWandAction getWandAction() {
        return new ActionAngel();
    }

    @Override
    public ResourceLocation getRegistryName() {
        return ConstructionWand.loc("core_angel");
    }
}
