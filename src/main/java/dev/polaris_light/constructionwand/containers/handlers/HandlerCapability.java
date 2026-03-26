package dev.polaris_light.constructionwand.containers.handlers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import dev.polaris_light.constructionwand.api.IContainerHandler;
import dev.polaris_light.constructionwand.basics.WandUtil;
import dev.polaris_light.constructionwand.containers.ContainerTrace;

public class HandlerCapability implements IContainerHandler
{
    @Override
    public boolean matches(Player player, ItemStack itemStack, ItemStack inventoryStack) {
        return inventoryStack != null && !inventoryStack.isEmpty() &&
                ItemAccess.forStack(inventoryStack).getCapability(Capabilities.Item.ITEM) != null;
    }

    @Override
    public int getSignature(Player player, ItemStack inventoryStack) {
        return inventoryStack.hashCode();
    }

    @Override
    public int countItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack) {
        ResourceHandler<ItemResource> resourceHandler = ItemAccess.forStack(inventoryStack).getCapability(Capabilities.Item.ITEM);
        if(resourceHandler == null) return 0;

        int total = 0;

        for(int i = 0; i < resourceHandler.size(); i++) {
            ItemResource containerResource = resourceHandler.getResource(i);
            if(!containerResource.isEmpty() && containerResource.matches(itemStack)) {
                total += Math.max(0, resourceHandler.getAmountAsInt(i));
            }
        }
        return total;
    }

    @Override
    public int useItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack, int count) {
        ResourceHandler<ItemResource> resourceHandler = ItemAccess.forStack(inventoryStack).getCapability(Capabilities.Item.ITEM);
        if(resourceHandler == null) return 0;

        try (var tx = Transaction.openRoot()) {
            int initialCount = count;
            for(int i = 0; i < resourceHandler.size(); i++) {
                ItemResource handlerResource = resourceHandler.getResource(i);
                if(!handlerResource.isEmpty() && WandUtil.stackEquals(itemStack, handlerResource.toStack())) {
                    int extracted = resourceHandler.extract(i, handlerResource, count, tx);
                    if(extracted > 0) {
                        count -= extracted;
                        if(count <= 0) break;
                    }
                }
            }
            if(initialCount != count) tx.commit();
            return count;
        }
    }
}
