package thetadev.constructionwand.containers.handlers;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thetadev.constructionwand.api.IContainerHandler;
import thetadev.constructionwand.containers.ContainerTrace;

import com.wintercogs.beyonddimensions.Api.DataBase.DimensionsNet;
import com.wintercogs.beyonddimensions.Api.DataBase.Storage.UnifiedStorage;
import com.wintercogs.beyonddimensions.Api.DataBase.Stack.IStackKey;
import com.wintercogs.beyonddimensions.Api.DataBase.Stack.ItemStackKey;
import com.wintercogs.beyonddimensions.Item.Custom.NetedItem;
import com.wintercogs.beyonddimensions.Item.Custom.NetTerminalItem;

public class HandlerNetTerminal implements IContainerHandler {

    @Override
    public boolean matches(Player player, ItemStack inventoryStack) {
        return inventoryStack.getItem() instanceof NetTerminalItem;
    }

    @Override
    public int getSignature(Player player, ItemStack inventoryStack) {
        if (!(inventoryStack.getItem() instanceof NetTerminalItem terminal)) return -1;
        int id = NetedItem.getNetId(inventoryStack);
        return (id != -1) ? 10000 + id : -1;
    }

    @Override
    public int countItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack) {
        DimensionsNet net = NetedItem.getNet(inventoryStack);
        if (net == null) return 0;

        UnifiedStorage storage = net.getUnifiedStorage();
        long result = storage.extract(new ItemStackKey(itemStack), Integer.MAX_VALUE, true, false).amount();

        if (result > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }

        return (int) result;
    }

    @Override
    public int useItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack, int count) {
        DimensionsNet net = NetedItem.getNet(inventoryStack);
        if (net == null) return count;

        UnifiedStorage storage = net.getUnifiedStorage();
        long result = storage.extract(new ItemStackKey(itemStack), count, false, false).amount();

        if (result > count) {
            return 0;
        }

        return count - (int) result;
    }
}
