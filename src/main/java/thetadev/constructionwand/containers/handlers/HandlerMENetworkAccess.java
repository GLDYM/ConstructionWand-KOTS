package thetadev.constructionwand.containers.handlers;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.MEStorage;
import appeng.helpers.WirelessTerminalMenuHost;
import appeng.items.tools.powered.WirelessTerminalItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thetadev.constructionwand.api.IContainerHandler;

public class HandlerMENetworkAccess implements IContainerHandler {

    @Override
    public boolean matches(Player player, ItemStack itemStack, ItemStack stack) {
        return stack.getItem() instanceof WirelessTerminalItem;
    }


    @Override
    public int countItems(Player player, ItemStack target, ItemStack terminal) {
        if (player instanceof ServerPlayer serverPlayer) {

            MEStorage storage = getStorage(serverPlayer, terminal);
            if (storage == null) return 0;

            AEItemKey key = AEItemKey.of(target);
            if (key == null) return 0;

            long amount = 0;
            for (var entry : storage.getAvailableStacks()) {
                if (entry.getKey().equals(key)) {
                    amount = entry.getLongValue();
                    break;
                }
            }
            return amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount;
        }
        return 0;
    }


    @Override
    public int useItems(Player player, ItemStack target, ItemStack terminal, int count) {
        if (player instanceof ServerPlayer serverPlayer) {
            MEStorage storage = getStorage(serverPlayer, terminal);
            if (storage == null) return count;

            AEItemKey key = AEItemKey.of(target);
            if (key == null) return count;

            var extracted = storage.extract(key, count, Actionable.MODULATE, null);
            long remaining = count - (int)extracted;
            return remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) remaining;
        }
        return count;
    }

    private MEStorage getStorage(ServerPlayer player, ItemStack terminal) {
        if (!(terminal.getItem() instanceof WirelessTerminalItem)) return null;

        try {
            WirelessTerminalMenuHost host = new WirelessTerminalMenuHost(
                player,
                null,
                terminal,
                (p, menu) -> {}
            );
            return host.getInventory();
        } catch (Exception e) {
            return null;
        }
    }
}
