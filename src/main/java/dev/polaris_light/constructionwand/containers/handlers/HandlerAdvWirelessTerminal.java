package dev.polaris_light.constructionwand.containers.handlers;

import com.tom.storagemod.Config;
import com.tom.storagemod.tile.StorageTerminalBlockEntity;
import com.tom.storagemod.item.AdvWirelessTerminalItem;
import com.tom.storagemod.util.StoredItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import dev.polaris_light.constructionwand.api.IContainerHandler;
import dev.polaris_light.constructionwand.basics.WandUtil;
import dev.polaris_light.constructionwand.containers.ContainerTrace;

public class HandlerAdvWirelessTerminal implements IContainerHandler {

    @Override
    public boolean matches(Player player, ItemStack inventoryStack) {
        return inventoryStack.getItem() instanceof AdvWirelessTerminalItem;
    }

    @Override
    public int getSignature(Player player, ItemStack inventoryStack) {
        CompoundTag tag = inventoryStack.getTag();
        if (tag == null) return -1;

        if (!tag.contains("BindX") || 
            !tag.contains("BindY") || 
            !tag.contains("BindZ") || 
            !tag.contains("BindDim")) {
            return -1;
        }

        int x = tag.getInt("BindX");
        int y = tag.getInt("BindY");
        int z = tag.getInt("BindZ");
        String dim = tag.getString("BindDim");
        return (x * 31 + y * 17 + z) ^ dim.hashCode();
    }

    @Override
    public int countItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack) {
        if (!(player instanceof ServerPlayer)) return 0;
        StorageTerminalBlockEntity terminal = resolveTerminal(player, inventoryStack);
        if (terminal == null) return 0;
        int found = 0;
        for (var entry : terminal.getStacks().entrySet()) {
            if (WandUtil.stackEquals(entry.getKey().getStack(), itemStack)) {
                found += entry.getValue();
                if (found >= Integer.MAX_VALUE) return Integer.MAX_VALUE;
            }
        }
        return found;
    }

    @Override
    public int useItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack, int count) {
        if (!(player instanceof ServerPlayer)) return count;
        StorageTerminalBlockEntity terminal = resolveTerminal(player, inventoryStack);
        if (terminal == null) return count;

        long extracted = terminal.pullStack(new StoredItemStack(itemStack, count), count).getQuantity();
        return count - (int)extracted;
    }

    private StorageTerminalBlockEntity resolveTerminal(Player player, ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return null;

        if (!tag.contains("BindX") || 
            !tag.contains("BindY") || 
            !tag.contains("BindZ") || 
            !tag.contains("BindDim")) {
            return null;
        }

        int x = tag.getInt("BindX");
        int y = tag.getInt("BindY");
        int z = tag.getInt("BindZ");
        BlockPos pos = new BlockPos(x, y, z);
        String dim = tag.getString("BindDim");

        Level termWorld = player.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(dim)));
        if (termWorld == null || !termWorld.isLoaded(pos)) return null;

        BlockEntity be = termWorld.getBlockEntity(pos);
        if (be instanceof StorageTerminalBlockEntity terminal) {
            terminal.updateServer();
            return canInteractWith(terminal, player) ? terminal : null;
        }
        return null;
    }

	private boolean canInteractWith(StorageTerminalBlockEntity terminal, Player player) {
		int d = 4;
        int termReach = Config.get().advWirelessRange;

        int beaconLevel = terminal.getBeaconLevel();
		if(Config.get().wirelessTermBeaconLvl != -1 && beaconLevel >= Config.get().wirelessTermBeaconLvl && termReach > 0) {
			if(Config.get().wirelessTermBeaconLvlDim != -1 && beaconLevel >= Config.get().wirelessTermBeaconLvlDim) return true;
			else return player.level() == terminal.getLevel();
		}

		d = Math.max(d, termReach);
		return player.level() == terminal.getLevel() && !(player.distanceToSqr(terminal.getBlockPos().getX() + 0.5D, terminal.getBlockPos().getY() + 0.5D, terminal.getBlockPos().getZ() + 0.5D) > d*2*d*2);
	}
}
