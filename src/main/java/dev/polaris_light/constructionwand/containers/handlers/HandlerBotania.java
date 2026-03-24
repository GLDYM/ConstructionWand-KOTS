package dev.polaris_light.constructionwand.containers.handlers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import dev.polaris_light.constructionwand.api.IContainerHandler;
import dev.polaris_light.constructionwand.containers.ContainerTrace;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.item.BlockProvider;

import java.util.Optional;

public class HandlerBotania implements IContainerHandler
{
    @Override
    public boolean matches(Player player, ItemStack inventoryStack) {
        return inventoryStack != null && inventoryStack.getCapability(BotaniaForgeCapabilities.BLOCK_PROVIDER).isPresent();
    }

    @Override
    public int getSignature(Player player, ItemStack inventoryStack) {
        return inventoryStack.hashCode();
    }

    @Override
    public int countItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack) {
        Optional<BlockProvider> provOptional = inventoryStack.getCapability(BotaniaForgeCapabilities.BLOCK_PROVIDER).resolve();
        if(provOptional.isEmpty()) return 0;

        BlockProvider prov = provOptional.get();
        int provCount = prov.getBlockCount(player, inventoryStack, Block.byItem(itemStack.getItem()));
        if(provCount == -1)
            return Integer.MAX_VALUE;
        return provCount;
    }

    @Override
    public int useItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack, int count) {
        Optional<BlockProvider> provOptional = inventoryStack.getCapability(BotaniaForgeCapabilities.BLOCK_PROVIDER).resolve();
        if(provOptional.isEmpty()) return 0;

        BlockProvider prov = provOptional.get();
        if(prov.provideBlock(player, inventoryStack, Block.byItem(itemStack.getItem()), true))
            return 0;
        return count;
    }
}