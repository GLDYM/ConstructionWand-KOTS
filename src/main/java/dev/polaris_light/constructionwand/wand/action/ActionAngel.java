package dev.polaris_light.constructionwand.wand.action;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import dev.polaris_light.constructionwand.api.IWandAction;
import dev.polaris_light.constructionwand.api.IWandSupplier;
import dev.polaris_light.constructionwand.basics.ConfigServer;
import dev.polaris_light.constructionwand.basics.WandUtil;
import dev.polaris_light.constructionwand.basics.option.WandOptions;
import dev.polaris_light.constructionwand.wand.undo.ISnapshot;
import dev.polaris_light.constructionwand.wand.undo.PlaceSnapshot;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

public class ActionAngel implements IWandAction
{
    @Override
    public int getLimit(ItemStack wand) {
        return ConfigServer.getWandProperties(wand.getItem()).getAngel();
    }

    @Nonnull
    @Override
    public List<ISnapshot> getSnapshots(Level world, Player player, BlockHitResult rayTraceResult,
                                        ItemStack wand, WandOptions options, IWandSupplier supplier, int limit) {
        LinkedList<ISnapshot> placeSnapshots = new LinkedList<>();

        Direction placeDirection = rayTraceResult.getDirection();
        BlockPos currentPos = rayTraceResult.getBlockPos();
        BlockState supportingBlock = world.getBlockState(currentPos);

        for(int i = 0; i < limit; i++) {
            currentPos = currentPos.relative(placeDirection.getOpposite());

            PlaceSnapshot snapshot = supplier.getPlaceSnapshot(world, currentPos, rayTraceResult, supportingBlock);
            if(snapshot != null) {
                placeSnapshots.add(snapshot);
                break;
            }
        }
        return placeSnapshots;
    }

    @Nonnull
    @Override
    public List<ISnapshot> getSnapshotsFromAir(Level world, Player player, BlockHitResult rayTraceResult,
                                               ItemStack wand, WandOptions options, IWandSupplier supplier, int limit) {
        LinkedList<ISnapshot> placeSnapshots = new LinkedList<>();

        if(!player.isCreative() && !ConfigServer.ANGEL_FALLING.get() && player.fallDistance > 10) return placeSnapshots;

        Vec3 playerVec = WandUtil.entityPositionVec(player);
        Vec3 lookVec = player.getLookAngle().multiply(2, 2, 2);
        Vec3 placeVec = playerVec.add(lookVec);
        BlockPos currentPos = WandUtil.posFromVec(placeVec);

        PlaceSnapshot snapshot = supplier.getPlaceSnapshot(world, currentPos, rayTraceResult, null);
        if(snapshot != null) placeSnapshots.add(snapshot);

        return placeSnapshots;
    }
}
