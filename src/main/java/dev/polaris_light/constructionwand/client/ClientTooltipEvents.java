package dev.polaris_light.constructionwand.client;

import dev.polaris_light.constructionwand.ConstructionWand;
import dev.polaris_light.constructionwand.api.IWandCore;
import dev.polaris_light.constructionwand.items.wand.ItemWand;
import dev.polaris_light.constructionwand.items.core.ItemCore;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = ConstructionWand.MODID, value = Dist.CLIENT)
public class ClientTooltipEvents {
    private ClientTooltipEvents() {
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();

        if (item instanceof ItemWand) {
            ItemWand.appendWandTooltip(stack, event.getToolTip(), event.getFlags());
        } else if (item instanceof ItemCore && item instanceof IWandCore core) {
            ItemCore.appendCoreTooltip(core, event.getToolTip());
        }
    }
}
