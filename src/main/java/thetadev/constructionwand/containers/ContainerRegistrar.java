package thetadev.constructionwand.containers;

import net.minecraftforge.fml.ModList;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.containers.handlers.HandlerBotania;
import thetadev.constructionwand.containers.handlers.HandlerBundle;
import thetadev.constructionwand.containers.handlers.HandlerCapability;
import thetadev.constructionwand.containers.handlers.HandlerLightland;
import thetadev.constructionwand.containers.handlers.HandlerMENetworkAccess;
import thetadev.constructionwand.containers.handlers.HandlerShulkerbox;

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
            ConstructionWand.instance.containerManager.register(new HandlerMENetworkAccess());
            ConstructionWand.LOGGER.info("Applied Energistics 2 integration added");
        }
    }
}
