package dev.polaris_light.constructionwand.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface ModCompat
{
    ModCompat DEFAULT = new ModCompat() {};

    default double blockDistance(Level world, Player player, BlockPos pos) {
        return Math.max(Math.abs(player.getBlockX() - pos.getX()), Math.abs(player.getBlockZ() - pos.getZ()));
    }
}
