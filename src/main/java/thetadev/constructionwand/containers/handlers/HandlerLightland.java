package thetadev.constructionwand.containers.handlers;

import dev.xkmc.l2backpack.content.capability.PickupModeCap;
import dev.xkmc.l2backpack.content.capability.InvPickupCap;
import dev.xkmc.l2backpack.content.capability.PickupTrace;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import thetadev.constructionwand.ConstructionWand;
import thetadev.constructionwand.api.IContainerHandler;
import thetadev.constructionwand.basics.WandUtil;

public class HandlerLightland implements IContainerHandler {

    @Override
    public boolean matches(Player player, ItemStack itemStack, ItemStack inventoryStack) {
        return resolveInvCap(inventoryStack) != null;
    }

    @Override
    public int countItems(Player player, ItemStack itemStack, ItemStack inventoryStack) {
        if (!(player instanceof ServerPlayer sp)) return 0;
        InvPickupCap<?> cap = resolveInvCap(inventoryStack);
        if (cap == null) return 0;

        PickupTrace trace = new PickupTrace(false, sp);
        if (!trace.push(cap.getSignature(), null)) return 0;
        int result = countRecursive(cap, trace, itemStack, Integer.MAX_VALUE);
        trace.pop();
        return result;
    }

    @Override
    public int useItems(Player player, ItemStack itemStack, ItemStack inventoryStack, int count) {
        if (!(player instanceof ServerPlayer sp)) return count;
        InvPickupCap<?> cap = resolveInvCap(inventoryStack);
        if (cap == null) return count;

        PickupTrace trace = new PickupTrace(false, sp);
        if (!trace.push(cap.getSignature(), null)) return count;
        int result = extractRecursive(cap, trace, itemStack, count);
        trace.pop();
        return result;
    }

    private InvPickupCap<?> resolveInvCap(ItemStack stack) {
        return stack.getCapability(PickupModeCap.TOKEN)
            .resolve()
            .filter(c -> c instanceof InvPickupCap<?>)
            .map(c -> (InvPickupCap<?>) c)
            .orElse(null);
    }

    private int countRecursive(InvPickupCap<?> cap, PickupTrace trace, ItemStack target, int maxCount) {
        IItemHandlerModifiable inv = cap.getInv(trace);
        if (inv == null) return 0;
        int found = 0;

        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (WandUtil.stackEquals(target, stack)) {
                //ConstructionWand.LOGGER.info("Stack Dectect: " + stack.toString());
                found += stack.getCount();
                if (found >= maxCount) return maxCount;
            }

            InvPickupCap<?> nested = resolveInvCap(stack);
            if (nested != null && trace.push(nested.getSignature(), null)) {
                //ConstructionWand.LOGGER.info("Nested Dectect: " + stack.toString());
                found += countRecursive(nested, trace, target, maxCount - found);
                trace.pop();
                if (found >= maxCount) return maxCount;
            }
        }

        return found;
    }

    private int extractRecursive(InvPickupCap<?> cap, PickupTrace trace, ItemStack target, int count) {
        IItemHandlerModifiable inv = cap.getInv(trace);
        if (inv == null) return count;
        int remaining = count;

        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (WandUtil.stackEquals(target, stack)) {
                int extractable = Math.min(stack.getCount(), remaining);
                ItemStack extracted = inv.extractItem(i, extractable, false);
                remaining -= extracted.getCount();
                if (remaining <= 0) return 0;
            }

            InvPickupCap<?> nested = resolveInvCap(stack);
            if (nested != null && trace.push(nested.getSignature(), null)) {
                remaining = extractRecursive(nested, trace, target, remaining);
                trace.pop();
                if (remaining <= 0) return 0;
            }
        }

        return remaining;
    }
}
