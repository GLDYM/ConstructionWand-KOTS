package dev.polaris_light.constructionwand.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;

@EventBusSubscriber
public class ModData
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(true, new RecipeGenerator.Runner(packOutput, lookupProvider));
        generator.addProvider(true, new ItemModelGenerator(packOutput));
    }
}
