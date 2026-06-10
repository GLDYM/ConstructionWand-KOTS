package dev.polaris_light.constructionwand.containers.handlers;

import dev.polaris_light.constructionwand.api.IContainerHandler;
import dev.polaris_light.constructionwand.containers.ContainerTrace;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.ItemCapability;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.item.BlockProvider;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class HandlerBotania implements IContainerHandler
{
    @Override
    public boolean matches(Player player, ItemStack itemStack, ItemStack inventoryStack) {
        return getBlockProvider(inventoryStack) != null;
    }

    @Override
    public int getSignature(Player player, ItemStack inventoryStack) {
        return inventoryStack.hashCode();
    }

    @Override
    public int countItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack) {
        BlockProvider prov = getBlockProvider(inventoryStack);
        if (prov == null) return 0;

        int provCount = prov.getBlockCount(player, inventoryStack, Block.byItem(itemStack.getItem()));
        if (provCount == -1)
            return Integer.MAX_VALUE;
        return provCount;
    }

    @Override
    public int useItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack, int count) {
        BlockProvider prov = getBlockProvider(inventoryStack);
        if (prov == null) return count;

        if (prov.provideBlock(player, inventoryStack, Block.byItem(itemStack.getItem()), true))
            return 0;
        return count;
    }

    @SuppressWarnings("unchecked")
    private static BlockProvider getBlockProvider(ItemStack inventoryStack) {
        if (inventoryStack == null) return null;

        try {
            Field legacyField = BotaniaForgeCapabilities.class.getField("BLOCK_PROVIDER");
            Object capability = legacyField.get(null);
            if (capability instanceof ItemCapability<?, ?> itemCapability) {
                return inventoryStack.getCapability((ItemCapability<BlockProvider, Void>) itemCapability);
            }
        } catch (ReflectiveOperationException ignored) {
        }

        try {
            Field lookupField = BlockProvider.class.getField("LOOKUP");
            Object lookup = lookupField.get(null);
            for (Method method : BotaniaForgeCapabilities.class.getMethods()) {
                if (!method.getName().equals("getItemApiLookupById") || method.getParameterCount() != 1) {
                    continue;
                }

                Object capability = method.invoke(null, lookup);
                if (capability instanceof ItemCapability<?, ?> itemCapability) {
                    return inventoryStack.getCapability((ItemCapability<BlockProvider, Void>) itemCapability);
                }
            }
        } catch (ReflectiveOperationException ignored) {
        }

        return null;
    }
}
