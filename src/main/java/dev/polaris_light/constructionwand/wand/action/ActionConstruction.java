package dev.polaris_light.constructionwand.wand.action;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import dev.polaris_light.constructionwand.api.IWandAction;
import dev.polaris_light.constructionwand.api.IWandSupplier;
import dev.polaris_light.constructionwand.basics.ConfigServer;
import dev.polaris_light.constructionwand.basics.option.WandOptions;
import dev.polaris_light.constructionwand.wand.undo.ISnapshot;
import dev.polaris_light.constructionwand.wand.undo.PlaceSnapshot;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Default WandAction. Extends your building on the side you're facing.
 */
public class ActionConstruction implements IWandAction
{
    @Override
    public int getLimit(ItemStack wand) {
        return ConfigServer.getWandProperties(wand.getItem()).getLimit();
    }

    @Nonnull
    @Override
    public List<ISnapshot> getSnapshots(Level world, Player player, BlockHitResult rayTraceResult,
                                        ItemStack wand, WandOptions options, IWandSupplier supplier, int limit) {
        LinkedList<ISnapshot> placeSnapshots = new LinkedList<>();
        LinkedList<BlockPos> candidates = new LinkedList<>();
        HashSet<BlockPos> allCandidates = new HashSet<>();

        Direction placeDirection = rayTraceResult.getDirection();
        BlockState targetBlock = world.getBlockState(rayTraceResult.getBlockPos());
        BlockPos startingPoint = rayTraceResult.getBlockPos().relative(placeDirection);

        // Is place direction allowed by lock?
        if(placeDirection == Direction.UP || placeDirection == Direction.DOWN) {
            if(options.testLock(WandOptions.LOCK.NORTHSOUTH) || options.testLock(WandOptions.LOCK.EASTWEST))
                candidates.add(startingPoint);
        }
        else if(options.testLock(WandOptions.LOCK.HORIZONTAL) || options.testLock(WandOptions.LOCK.VERTICAL))
            candidates.add(startingPoint);

        while(!candidates.isEmpty() && placeSnapshots.size() < limit) {
            BlockPos currentCandidate = candidates.removeFirst();
            try {
                BlockPos supportingPoint = currentCandidate.relative(placeDirection.getOpposite());
                BlockState candidateSupportingBlock = world.getBlockState(supportingPoint);

                if(options.matchBlocks(targetBlock.getBlock(), candidateSupportingBlock.getBlock()) &&
                        allCandidates.add(currentCandidate)) {
                    PlaceSnapshot snapshot = supplier.getPlaceSnapshot(world, currentCandidate, rayTraceResult, candidateSupportingBlock);
                    if(snapshot == null) continue;
                    placeSnapshots.add(snapshot);

                    switch(placeDirection) {
                        case DOWN:
                        case UP:
                            if(options.testLock(WandOptions.LOCK.NORTHSOUTH)) {
                                candidates.add(currentCandidate.relative(Direction.NORTH));
                                candidates.add(currentCandidate.relative(Direction.SOUTH));
                            }
                            if(options.testLock(WandOptions.LOCK.EASTWEST)) {
                                candidates.add(currentCandidate.relative(Direction.EAST));
                                candidates.add(currentCandidate.relative(Direction.WEST));
                            }
                            if(options.testLock(WandOptions.LOCK.NORTHSOUTH) && options.testLock(WandOptions.LOCK.EASTWEST)) {
                                candidates.add(currentCandidate.relative(Direction.NORTH).relative(Direction.EAST));
                                candidates.add(currentCandidate.relative(Direction.NORTH).relative(Direction.WEST));
                                candidates.add(currentCandidate.relative(Direction.SOUTH).relative(Direction.EAST));
                                candidates.add(currentCandidate.relative(Direction.SOUTH).relative(Direction.WEST));
                            }
                            break;
                        case NORTH:
                        case SOUTH:
                            if(options.testLock(WandOptions.LOCK.HORIZONTAL)) {
                                candidates.add(currentCandidate.relative(Direction.EAST));
                                candidates.add(currentCandidate.relative(Direction.WEST));
                            }
                            if(options.testLock(WandOptions.LOCK.VERTICAL)) {
                                candidates.add(currentCandidate.relative(Direction.UP));
                                candidates.add(currentCandidate.relative(Direction.DOWN));
                            }
                            if(options.testLock(WandOptions.LOCK.HORIZONTAL) && options.testLock(WandOptions.LOCK.VERTICAL)) {
                                candidates.add(currentCandidate.relative(Direction.UP).relative(Direction.EAST));
                                candidates.add(currentCandidate.relative(Direction.UP).relative(Direction.WEST));
                                candidates.add(currentCandidate.relative(Direction.DOWN).relative(Direction.EAST));
                                candidates.add(currentCandidate.relative(Direction.DOWN).relative(Direction.WEST));
                            }
                            break;
                        case EAST:
                        case WEST:
                            if(options.testLock(WandOptions.LOCK.HORIZONTAL)) {
                                candidates.add(currentCandidate.relative(Direction.NORTH));
                                candidates.add(currentCandidate.relative(Direction.SOUTH));
                            }
                            if(options.testLock(WandOptions.LOCK.VERTICAL)) {
                                candidates.add(currentCandidate.relative(Direction.UP));
                                candidates.add(currentCandidate.relative(Direction.DOWN));
                            }
                            if(options.testLock(WandOptions.LOCK.HORIZONTAL) && options.testLock(WandOptions.LOCK.VERTICAL)) {
                                candidates.add(currentCandidate.relative(Direction.UP).relative(Direction.NORTH));
                                candidates.add(currentCandidate.relative(Direction.UP).relative(Direction.SOUTH));
                                candidates.add(currentCandidate.relative(Direction.DOWN).relative(Direction.NORTH));
                                candidates.add(currentCandidate.relative(Direction.DOWN).relative(Direction.SOUTH));
                            }
                            break;
                    }
                }
            } catch(Exception e) {
                // Can't do anything, could be anything.
                // Skip if anything goes wrong.
            }
        }
        return placeSnapshots;
    }

    @Nonnull
    @Override
    public List<ISnapshot> getSnapshotsFromAir(Level world, Player player, BlockHitResult rayTraceResult,
                                               ItemStack wand, WandOptions options, IWandSupplier supplier, int limit) {
        return new ArrayList<>();
    }
}
