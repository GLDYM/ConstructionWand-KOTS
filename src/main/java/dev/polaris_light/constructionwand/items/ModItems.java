package dev.polaris_light.constructionwand.items;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import net.minecraft.core.registries.Registries;
import dev.polaris_light.constructionwand.ConstructionWand;
import dev.polaris_light.constructionwand.items.wand.ItemWandBasic;
import dev.polaris_light.constructionwand.items.wand.ItemWandInfinity;
import dev.polaris_light.constructionwand.items.core.ItemCoreAngel;
import dev.polaris_light.constructionwand.items.core.ItemCoreDestruction;

@EventBusSubscriber(modid = ConstructionWand.MODID)
public class ModItems
{
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ConstructionWand.MODID);

    // Wands
    public static final DeferredItem<Item> WAND_STONE = ITEMS.registerItem("stone_wand", (props) -> new ItemWandBasic(props, ToolMaterial.STONE));
    public static final DeferredItem<Item> WAND_IRON = ITEMS.registerItem("iron_wand", (props) -> new ItemWandBasic(props, ToolMaterial.IRON));
    public static final DeferredItem<Item> WAND_DIAMOND = ITEMS.registerItem("diamond_wand", (props) -> new ItemWandBasic(props, ToolMaterial.DIAMOND));
    public static final DeferredItem<Item> WAND_INFINITY = ITEMS.registerItem("infinity_wand", (props) -> new ItemWandInfinity(props));

    // Cores
    public static final DeferredItem<Item> CORE_ANGEL = ITEMS.registerItem("core_angel", (props) -> new ItemCoreAngel(props.stacksTo(1)));
    public static final DeferredItem<Item> CORE_DESTRUCTION = ITEMS.registerItem("core_destruction", (props) -> new ItemCoreDestruction(props.stacksTo(1)));

    // Collections
    public static final DeferredItem<Item>[] WANDS = new DeferredItem[] {WAND_STONE, WAND_IRON, WAND_DIAMOND, WAND_INFINITY};
    public static final DeferredItem<Item>[] CORES = new DeferredItem[] {CORE_ANGEL, CORE_DESTRUCTION};



    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            for(DeferredItem<Item> itemSupplier : WANDS) {
                event.accept(itemSupplier.get());
            }
        } else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            for(DeferredItem<Item> itemSupplier : CORES) {
                event.accept(itemSupplier.get());
            }
        }
    }
}
