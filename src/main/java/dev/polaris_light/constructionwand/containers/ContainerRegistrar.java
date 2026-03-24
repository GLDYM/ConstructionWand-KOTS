package dev.polaris_light.constructionwand.containers;

import net.minecraftforge.fml.ModList;
import dev.polaris_light.constructionwand.ConstructionWand;
import dev.polaris_light.constructionwand.containers.handlers.HandlerAdvWirelessTerminal;
import dev.polaris_light.constructionwand.containers.handlers.HandlerBotania;
import dev.polaris_light.constructionwand.containers.handlers.HandlerBundle;
import dev.polaris_light.constructionwand.containers.handlers.HandlerCapability;
import dev.polaris_light.constructionwand.containers.handlers.HandlerDimensionsNet;
import dev.polaris_light.constructionwand.containers.handlers.HandlerLightland;
import dev.polaris_light.constructionwand.containers.handlers.HandlerNetTerminal;
import dev.polaris_light.constructionwand.containers.handlers.HandlerPortableCell;
import dev.polaris_light.constructionwand.containers.handlers.HandlerShulkerbox;
import dev.polaris_light.constructionwand.containers.handlers.HandlerWirelessGrid;
import dev.polaris_light.constructionwand.containers.handlers.HandlerWirelessTerminal;


public class ContainerRegistrar
{
    public static void register() {
        // Normal backpack will be recognized as Capability
        if(ModList.get().isLoaded("l2backpack")) {
            ConstructionWand.instance.containerManager.register(new HandlerLightland());
            ConstructionWand.LOGGER.info("L2Backpack integration added");
        }

        ConstructionWand.instance.containerManager.register(new HandlerCapability());
        ConstructionWand.instance.containerManager.register(new HandlerShulkerbox());
        ConstructionWand.instance.containerManager.register(new HandlerBundle());

        if(ModList.get().isLoaded("botania")) {
            ConstructionWand.instance.containerManager.register(new HandlerBotania());
            ConstructionWand.LOGGER.info("Botania integration added");
        }

        if(ModList.get().isLoaded("curios")) {
            ConstructionWand.LOGGER.info("Curios integration added");
        }

        if(ModList.get().isLoaded("ae2")) {
            ConstructionWand.instance.containerManager.register(new HandlerPortableCell());
            ConstructionWand.instance.containerManager.register(new HandlerWirelessTerminal());
            ConstructionWand.LOGGER.info("Applied Energistics 2 integration added");
        }

        if(ModList.get().isLoaded("toms_storage")) {
            ConstructionWand.instance.containerManager.register(new HandlerAdvWirelessTerminal());
            ConstructionWand.LOGGER.info("Tom's Simple Storage integration added");
        }

        if(ModList.get().isLoaded("refinedstorage")) {
            ConstructionWand.instance.containerManager.register(new HandlerWirelessGrid());
            ConstructionWand.LOGGER.info("Refined Storage integration added");
        }

        if(ModList.get().isLoaded("beyonddimensions")) {
            ConstructionWand.instance.containerManager.register(new HandlerDimensionsNet());
            ConstructionWand.instance.containerManager.register(new HandlerNetTerminal());
            ConstructionWand.LOGGER.info("Beyond Dimensions integration added");
        }
    }
}
