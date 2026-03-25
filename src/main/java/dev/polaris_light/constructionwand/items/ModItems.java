package dev.polaris_light.constructionwand.items;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import dev.polaris_light.constructionwand.ConstructionWand;
import dev.polaris_light.constructionwand.basics.option.WandOptions;
import dev.polaris_light.constructionwand.items.core.ItemCoreAngel;
import dev.polaris_light.constructionwand.items.core.ItemCoreDestruction;
import dev.polaris_light.constructionwand.items.wand.ItemWand;
import dev.polaris_light.constructionwand.items.wand.ItemWandBasic;
import dev.polaris_light.constructionwand.items.wand.ItemWandInfinity;

@EventBusSubscriber(modid = ConstructionWand.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, ConstructionWand.MODID);

    // Wands
    public static final DeferredHolder<Item, Item> WAND_STONE = ITEMS.register("stone_wand", () -> new ItemWandBasic(propWand(), Tiers.STONE));
    public static final DeferredHolder<Item, Item> WAND_IRON = ITEMS.register("iron_wand", () -> new ItemWandBasic(propWand(), Tiers.IRON));
    public static final DeferredHolder<Item, Item> WAND_DIAMOND = ITEMS.register("diamond_wand", () -> new ItemWandBasic(propWand(), Tiers.DIAMOND));
    public static final DeferredHolder<Item, Item> WAND_INFINITY = ITEMS.register("infinity_wand", () -> new ItemWandInfinity(propWand()));

    // Cores
    public static final DeferredHolder<Item, Item> CORE_ANGEL = ITEMS.register("core_angel", () -> new ItemCoreAngel(propUpgrade()));
    public static final DeferredHolder<Item, Item> CORE_DESTRUCTION = ITEMS.register("core_destruction", () -> new ItemCoreDestruction(propUpgrade()));

    // Collections
    public static final DeferredHolder<Item, Item>[] WANDS = new DeferredHolder[] {WAND_STONE, WAND_IRON, WAND_DIAMOND, WAND_INFINITY};
    public static final DeferredHolder<Item, Item>[] CORES = new DeferredHolder[] {CORE_ANGEL, CORE_DESTRUCTION};

    public static Item.Properties propWand() {
        return new Item.Properties();
    }

    private static Item.Properties propUpgrade() {
        return new Item.Properties().stacksTo(1);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerModelProperties() {
        for(DeferredHolder<Item, Item> itemSupplier : WANDS) {
            Item item = itemSupplier.get();
            ItemProperties.register(
                    item, ConstructionWand.loc("using_core"),
                    (stack, world, entity, n) -> entity == null || !(stack.getItem() instanceof ItemWand) ? 0 :
                            new WandOptions(stack).cores.get().getColor() != -1 ? 1 : 0
                            // fuck ARGB, ARGB < -1 !
                            // new WandOptions(stack).cores.get().getColor() > -1 ? 1 : 0
            );
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        for(DeferredHolder<Item, Item> itemSupplier : WANDS) {
            Item item = itemSupplier.get();
            event.register((stack, layer) -> (layer == 1 && stack.getItem() instanceof ItemWand) ?
                    new WandOptions(stack).cores.get().getColor() : -1, item);
        }
    }

    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            for(DeferredHolder<Item, Item> itemSupplier : WANDS) {
                event.accept(itemSupplier.get());
            }
        } else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            for(DeferredHolder<Item, Item> itemSupplier : CORES) {
                event.accept(itemSupplier.get());
            }
        }
    }
}
