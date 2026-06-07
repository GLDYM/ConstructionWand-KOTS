package thetadev.constructionwand.containers.handlers;

import com.refinedmods.refinedstorageaddons.RSAddons;
import com.refinedmods.refinedstorageaddons.item.WirelessCraftingGridItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HandlerWirelessCraftingGrid extends HandlerWirelessGrid {
    @Override
    public boolean matches(Player player, ItemStack inventoryStack) {
        return inventoryStack.getItem() instanceof WirelessCraftingGridItem;
    }

    @Override
    protected boolean hasInfiniteEnergy(ItemStack stack, int extractCost) {
        return super.hasInfiniteEnergy(stack, extractCost)
            || !RSAddons.SERVER_CONFIG.getWirelessCraftingGrid().getUseEnergy()
            || (stack.getItem() instanceof WirelessCraftingGridItem wirelessCraftingGrid
                && wirelessCraftingGrid.getType() == WirelessCraftingGridItem.Type.CREATIVE);
    }
}
