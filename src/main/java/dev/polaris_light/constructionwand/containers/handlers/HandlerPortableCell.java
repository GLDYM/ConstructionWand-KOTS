package dev.polaris_light.constructionwand.containers.handlers;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.MEStorage;
import appeng.core.localization.PlayerMessages;
import appeng.items.contents.PortableCellMenuHost;
import appeng.items.tools.powered.PortableCellItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import dev.polaris_light.constructionwand.api.IContainerHandler;
import dev.polaris_light.constructionwand.containers.ContainerTrace;

public class HandlerPortableCell implements IContainerHandler {

    @Override
    public boolean matches(Player player, ItemStack itemStack, ItemStack inventoryStack) {
        return inventoryStack.getItem() instanceof PortableCellItem;
    }

    @Override
    public int getSignature(Player player, ItemStack inventoryStack) {
        return inventoryStack.hashCode();
    }

    @Override
    public int countItems(Player player, ContainerTrace trace, ItemStack target, ItemStack cell) {
        if (player instanceof ServerPlayer serverPlayer) {

            MEStorage storage = getStorage(serverPlayer, cell);
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
    public int useItems(Player player, ContainerTrace trace, ItemStack target, ItemStack cell, int count) {
        if (player instanceof ServerPlayer serverPlayer) {
            MEStorage storage = getStorage(serverPlayer, cell);
            if (storage == null) return count;

            AEItemKey key = AEItemKey.of(target);
            if (key == null) return count;

            var extracted = storage.extract(key, count, Actionable.MODULATE, null);
            long remaining = count - (int)extracted;
            return remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) remaining;
        }
        return count;
    }

    private MEStorage getStorage(ServerPlayer player, ItemStack cell) {
        if (cell.getItem() instanceof PortableCellItem c) {
            try {
                PortableCellMenuHost host = new PortableCellMenuHost(
                    c,
                    player,
                    null,
                    (p, menu) -> {}
                );

                double power = c.getAECurrentPower(cell);
                if (power <= 0) {
                    player.displayClientMessage(PlayerMessages.DeviceNotPowered.text(), true);
                    return null;
                }

                return host.getInventory();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
