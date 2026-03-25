package dev.polaris_light.constructionwand.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import dev.polaris_light.constructionwand.containers.ContainerTrace;

public interface IContainerHandler
{
    boolean matches(Player player, ItemStack itemStack, ItemStack inventoryStack);

    // Ender Chest is 0, Invaild ids -1
    int getSignature(Player player, ItemStack inventoryStack);

    int countItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack);

    int useItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack, int count);
}
