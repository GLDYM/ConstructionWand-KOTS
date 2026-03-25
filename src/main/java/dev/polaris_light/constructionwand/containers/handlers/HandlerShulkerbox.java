package dev.polaris_light.constructionwand.containers.handlers;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import dev.polaris_light.constructionwand.ConstructionWand;
import dev.polaris_light.constructionwand.api.IContainerHandler;
import dev.polaris_light.constructionwand.basics.WandUtil;
import dev.polaris_light.constructionwand.containers.ContainerTrace;

public class HandlerShulkerbox implements IContainerHandler
{
    private final int SLOTS = 27;

    @Override
    public boolean matches(Player player, ItemStack itemStack, ItemStack inventoryStack) {
        return inventoryStack != null && inventoryStack.getCount() == 1 && Block.byItem(inventoryStack.getItem()) instanceof ShulkerBoxBlock;
    }

    @Override
    public int getSignature(Player player, ItemStack inventoryStack) {
        return inventoryStack.hashCode();
    }

    @Override
    public int countItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack) {
        int count = 0;

        for(ItemStack stack : getItemList(inventoryStack)) {
            if(WandUtil.stackEquals(stack, itemStack)) {
                count += stack.getCount();
            } else {
                count += ConstructionWand.containerManager.countItems(player, trace, itemStack, stack);
            }
        }

        return count;
    }

    @Override
    public int useItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack, int count) {
        NonNullList<ItemStack> itemList = getItemList(inventoryStack);
        boolean changed = false;

        for(ItemStack stack : itemList) {
            if(WandUtil.stackEquals(stack, itemStack)) {
                int toTake = Math.min(count, stack.getCount());
                stack.shrink(toTake);
                count -= toTake;
                changed = true;
                if(count == 0) break;
            } else {
                int before = count;
                count = ConstructionWand.containerManager.useItems(player, trace, itemStack, stack, count);
                if(count < before) {
                    changed = true;
                    if(count == 0) break;
                }
            }
        }
        if(changed) {
            setItemList(inventoryStack, itemList);
            player.getInventory().setChanged();
        }

        return count;
    }

    private NonNullList<ItemStack> getItemList(ItemStack itemStack) {
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        ItemContainerContents contents = itemStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        contents.stream().forEach(stack -> {
            if (stack != null && !stack.isEmpty()) {
                itemStacks.add(stack);
            }
        });
        return itemStacks;
    }

    private void setItemList(ItemStack itemStack, NonNullList<ItemStack> itemStacks) {
        itemStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(itemStacks));
    }
}
