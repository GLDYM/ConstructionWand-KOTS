package dev.polaris_light.constructionwand.compat.sable;

import dev.polaris_light.constructionwand.compat.ModCompat;
import dev.ryanhcode.sable.Sable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SableCompat implements ModCompat
{
    @Override
    public double blockDistance(Level world, Player player, BlockPos pos) {
        return Sable.HELPER.rectilinearDistanceWithSubLevels(world, player.position(), Vec3.atCenterOf(pos));
    }
}
