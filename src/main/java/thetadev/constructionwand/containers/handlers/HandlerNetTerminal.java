package thetadev.constructionwand.containers.handlers;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thetadev.constructionwand.api.IContainerHandler;
import thetadev.constructionwand.containers.ContainerTrace;
import com.wintercogs.beyonddimensions.Item.Custom.NetedItem;
import com.wintercogs.beyonddimensions.Item.Custom.NetTerminalItem;
import com.wintercogs.beyonddimensions.Api.DataBase.DimensionsNet;
import com.wintercogs.beyonddimensions.Api.DataBase.Storage.UnifiedStorage;
import com.wintercogs.beyonddimensions.Api.DataBase.Stack.IStackType;
import com.wintercogs.beyonddimensions.Api.DataBase.Stack.ItemStackType;

public class HandlerNetTerminal implements IContainerHandler {

    @Override
    public boolean matches(Player player, ItemStack inventoryStack) {
        return inventoryStack.getItem() instanceof NetTerminalItem;
    }

    @Override
    public int getSignature(Player player, ItemStack inventoryStack) {
        if (!(inventoryStack.getItem() instanceof NetTerminalItem terminal)) return 0;
        int id = NetedItem.getNetId(inventoryStack);
        return (id != -1) ? 10000 + id : 0;
    }

    @Override
    public int countItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack) {
        DimensionsNet net = getNetFromTerminal(inventoryStack, player);
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
        DimensionsNet net = getNetFromTerminal(inventoryStack, player);
        if (net == null) return count;

        UnifiedStorage storage = net.getUnifiedStorage();
        ItemStackType request = new ItemStackType(new ItemStack(itemStack.getItem(), count));
        IStackType<?> extracted = storage.extract(request, false);

        if (extracted instanceof ItemStackType itemResult) {
            return count - itemResult.getStack().getCount();
        }
        return 0;
    }

    private DimensionsNet getNetFromTerminal(ItemStack terminalStack, Player player) {
        if (!(terminalStack.getItem() instanceof NetTerminalItem terminal)) return null;
        int id = NetedItem.getNetId(terminalStack);
        MinecraftServer server = player.getServer();
        return (server != null) ? DimensionsNet.getNetFromId(id, server) : null;
    }
}
