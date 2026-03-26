package dev.polaris_light.constructionwand.items.core;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import dev.polaris_light.constructionwand.ConstructionWand;
import dev.polaris_light.constructionwand.api.IWandCore;

import java.util.List;

public abstract class ItemCore extends Item implements IWandCore
{
    public ItemCore(Properties properties) {
        super(properties);
    }

    public static void appendCoreTooltip(IWandCore core, List<Component> lines) {
        lines.add(
                Component.translatable(ConstructionWand.MODID + ".option.cores." + core.getRegistryName().toString() + ".desc")
                        .withStyle(ChatFormatting.GRAY)
        );
        lines.add(
                Component.translatable(ConstructionWand.MODID + ".tooltip.core_tip").withStyle(ChatFormatting.AQUA)
        );
    }
}
