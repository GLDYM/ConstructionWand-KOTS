package thetadev.constructionwand.containers.handlers;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import thetadev.constructionwand.api.IContainerHandler;
import thetadev.constructionwand.containers.ContainerTrace;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.item.NetworkItem;
import com.refinedmods.refinedstorage.item.WirelessGridItem;

public class HandlerWirelessGrid implements IContainerHandler {

    @Override
    public boolean matches(Player player, ItemStack inventoryStack) {
        return inventoryStack.getItem() instanceof NetworkItem;
    }

    @Override
    public int getSignature(Player player, ItemStack inventoryStack) {
        CompoundTag tag = inventoryStack.getTag();
        if (tag == null) return -1;

        if (!tag.contains("NodeX") || 
            !tag.contains("NodeY") || 
            !tag.contains("NodeZ") || 
            !tag.contains("Dimension")) {
            return -1;
        }

        int x = tag.getInt("NodeX");
        int y = tag.getInt("NodeY");
        int z = tag.getInt("NodeZ");
        String dim = tag.getString("Dimension");
        return (x * 31 + y * 17 + z) ^ dim.hashCode();
    }

    @Override
    public int countItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack) {
        INetwork network = resolveNetwork(player, inventoryStack);
        if (network == null) return 0;

        int extractCost = RS.SERVER_CONFIG.getWirelessGrid().getExtractUsage();
        boolean infiniteEnergy = hasInfiniteEnergy(inventoryStack, extractCost);
        int maxByEnergy;
        if (infiniteEnergy) {
            maxByEnergy = Integer.MAX_VALUE;
        } else {
            IEnergyStorage energy = inventoryStack.getCapability(ForgeCapabilities.ENERGY).orElse(null);
            if (energy == null) return 0;
            int energyStored = energy.getEnergyStored();
            maxByEnergy = energyStored / extractCost;
        }

        if (!infiniteEnergy && maxByEnergy <= 0) {
            player.displayClientMessage(Component.translatable("misc.refinedstorage.wireless_grid.out_of_energy"), true);
            return 0;
        }

        ItemStack simulated = network.extractItem(itemStack, maxByEnergy, Action.SIMULATE);
        return simulated.isEmpty() ? 0 : simulated.getCount();
    }

    @Override
    public int useItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack, int count) {
        INetwork network = resolveNetwork(player, inventoryStack);
        if (network == null) {
            player.displayClientMessage(Component.translatable("misc.refinedstorage.wireless_grid.not_bound"), true);
            return count;
        }

        int extractCost = RS.SERVER_CONFIG.getWirelessGrid().getExtractUsage();
        boolean infiniteEnergy = hasInfiniteEnergy(inventoryStack, extractCost);
        IEnergyStorage energy = inventoryStack.getCapability(ForgeCapabilities.ENERGY).orElse(null);
        int maxByEnergy;
        if (infiniteEnergy) {
            maxByEnergy = Integer.MAX_VALUE;
        } else {
            if (energy == null) return count;
            int energyStored = energy.getEnergyStored();
            maxByEnergy = energyStored / extractCost;
        }

        if (!infiniteEnergy && maxByEnergy <= 0) {
            player.displayClientMessage(Component.translatable("misc.refinedstorage.wireless_grid.out_of_energy"), true);
            return count;
        }

        int effectiveRequest = Math.min(count, maxByEnergy);

        ItemStack extractedSim = network.extractItem(itemStack, effectiveRequest, Action.SIMULATE);
        int canExtract = extractedSim.isEmpty() ? 0 : extractedSim.getCount();

        if (canExtract <= 0) {
            return count;
        }

        ItemStack extracted = network.extractItem(itemStack, canExtract, Action.PERFORM);
        int actuallyExtracted = extracted.isEmpty() ? 0 : extracted.getCount();

        if (energy != null) {
            energy.extractEnergy(actuallyExtracted * extractCost, false);
        }
        return count - actuallyExtracted;
    }

    protected boolean hasInfiniteEnergy(ItemStack stack, int extractCost) {
        return extractCost <= 0
            || !RS.SERVER_CONFIG.getWirelessGrid().getUseEnergy()
            || (stack.getItem() instanceof WirelessGridItem wirelessGrid
                && wirelessGrid.getType() == WirelessGridItem.Type.CREATIVE);
    }

    private INetwork resolveNetwork(Player player, ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return null;

        if (!tag.contains("NodeX") || 
            !tag.contains("NodeY") || 
            !tag.contains("NodeZ") || 
            !tag.contains("Dimension")) {
            return null;
        }

        int x = tag.getInt("NodeX");
        int y = tag.getInt("NodeY");
        int z = tag.getInt("NodeZ");
        BlockPos pos = new BlockPos(x, y, z);
        String dim = tag.getString("Dimension");

        Level termWorld = player.level().getServer().getLevel(ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(dim)));
        if (termWorld == null || !termWorld.isLoaded(pos)) return null;

        BlockEntity be = termWorld.getBlockEntity(pos);
        if (be instanceof INetworkNodeProxy proxy) {
            INetworkNode node = proxy.getNode();
            return node != null ? node.getNetwork() : null;
        }

        return null;
    }
}
