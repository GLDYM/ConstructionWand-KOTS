package thetadev.constructionwand.containers.handlers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import thetadev.constructionwand.api.IContainerHandler;
import thetadev.constructionwand.containers.ContainerTrace;
import com.wintercogs.beyonddimensions.Api.DataBase.DimensionsNet;
import com.wintercogs.beyonddimensions.Api.DataBase.Storage.UnifiedStorage;
import com.wintercogs.beyonddimensions.Api.DataBase.Stack.IStackType;
import com.wintercogs.beyonddimensions.Api.DataBase.Stack.ItemStackType;

public class HandlerDimensionsNet implements IContainerHandler {
    // This Handler does not related with any specific Item, it works as long as the player has a Dimensions Net.
    // For every itemStack, matches method will return true, but the signature confirms only one net used.

    @Override
    public boolean matches(Player player, ItemStack inventoryStack) {
        DimensionsNet net = DimensionsNet.getNetFromPlayer(player);
        return net != null;
    }

    @Override
    public int getSignature(Player player, ItemStack inventoryStack) {
        DimensionsNet net = DimensionsNet.getNetFromPlayer(player);
        return (net != null) ? 10000 + net.getId() : -1;
    }

    @Override
    public int countItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack) {
        DimensionsNet net = DimensionsNet.getNetFromPlayer(player);
        if (net == null) return 0;

        UnifiedStorage storage = net.getUnifiedStorage();
        ItemStackType query = new ItemStackType(new ItemStack(itemStack.getItem(), Integer.MAX_VALUE));
        IStackType<?> result = storage.extract(query, true);

        if (result instanceof ItemStackType itemResult) {
            return itemResult.getStack().getCount();
        }
        return 0;
    }

    @Override
    public int useItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack, int count) {
        DimensionsNet net = DimensionsNet.getNetFromPlayer(player);
        if (net == null) return 0;

        UnifiedStorage storage = net.getUnifiedStorage();
        ItemStackType request = new ItemStackType(new ItemStack(itemStack.getItem(), count));
        IStackType<?> extracted = storage.extract(request, false);

        if (extracted instanceof ItemStackType itemResult) {
            return count - itemResult.getStack().getCount();
        }
        return 0;
    }
}
