package dev.polaris_light.constructionwand.event;

import dev.polaris_light.constructionwand.ConstructionWand;
import dev.polaris_light.constructionwand.client.KeybindHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = ConstructionWand.MODID,
                        bus = Mod.EventBusSubscriber.Bus.MOD,
                        value = Dist.CLIENT)
public class KeyMappingsEvents {
    @SubscribeEvent
    public static void registerKeymapping(RegisterKeyMappingsEvent event) {
        event.register(KeybindHandler.KEY_OPT);
    }
}
