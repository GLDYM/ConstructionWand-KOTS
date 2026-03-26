package dev.polaris_light.constructionwand.client;

import dev.polaris_light.constructionwand.ConstructionWand;
import dev.polaris_light.constructionwand.client.property.CoreColorTintSource;
import dev.polaris_light.constructionwand.client.property.UsingCoreModelProperty;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;

@EventBusSubscriber(modid = ConstructionWand.MODID, value = Dist.CLIENT)
public class ClientEvents {
    private ClientEvents() {
    }

    @SubscribeEvent
    public static void registerModelProperties(RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(ConstructionWand.loc("using_core"), UsingCoreModelProperty.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(ConstructionWand.loc("core_color"), CoreColorTintSource.MAP_CODEC);
    }
}
