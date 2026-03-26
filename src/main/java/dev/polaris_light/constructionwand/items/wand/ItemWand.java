package dev.polaris_light.constructionwand.items.wand;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import dev.polaris_light.constructionwand.ConstructionWand;
import dev.polaris_light.constructionwand.api.IWandCore;
import dev.polaris_light.constructionwand.basics.WandUtil;
import dev.polaris_light.constructionwand.basics.option.IOption;
import dev.polaris_light.constructionwand.basics.option.WandOptions;
import dev.polaris_light.constructionwand.wand.WandJob;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class ItemWand extends Item
{
    public ItemWand(Properties properties) {
        super(properties);
    }

    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        Level world = context.getLevel();

        if(world.isClientSide() || player == null) return InteractionResult.FAIL;

        ItemStack stack = player.getItemInHand(hand);

        if(ConstructionWand.undoHistory.isUndoActive(player)) {
            return ConstructionWand.undoHistory.undo(player, world, context.getClickedPos()) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        }
        else {
            WandJob job = getWandJob(player, world, new BlockHitResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), false), stack);
            return job.doIt() ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        }
    }

    @Nonnull
    @Override
    public InteractionResult use(@Nonnull Level world, Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if(!ConstructionWand.undoHistory.isUndoActive(player)) {
            if(world.isClientSide()) return InteractionResult.FAIL;

            // Right click: Place angel block
            WandJob job = getWandJob(player, world, BlockHitResult.miss(player.getLookAngle(),
                    WandUtil.fromVector(player.getLookAngle()), player.blockPosition()), stack);
            return job.doIt() ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        }
        return InteractionResult.FAIL;
    }

    public static WandJob getWandJob(Player player, Level world, @Nullable BlockHitResult rayTraceResult, ItemStack wand) {
        WandJob wandJob = new WandJob(player, world, rayTraceResult, wand);
        wandJob.getSnapshots();

        return wandJob;
    }

    public boolean isCorrectToolForDrops(ItemStack stack, BlockState blockIn) {
        return false;
    }

    public boolean isValidRepairItem(@Nonnull ItemStack toRepair, @Nonnull ItemStack repair) {
        return false;
    }

    public int remainingDurability(ItemStack stack) {
        return Integer.MAX_VALUE;
    }

    public static void appendWandTooltip(ItemStack itemStack, List<Component> lines, TooltipFlag flag) {
        WandOptions options = new WandOptions(itemStack);
        int limit = options.cores.get().getWandAction().getLimit(itemStack);

        String langTooltip = ConstructionWand.MODID + ".tooltip.";

        // +SHIFT tooltip: show all options + installed cores
        if(flag.hasShiftDown()) {
            for(int i = 1; i < options.allOptions.length; i++) {
                IOption<?> opt = options.allOptions[i];
                lines.add(Component.translatable(opt.getKeyTranslation()).withStyle(ChatFormatting.AQUA)
                        .append(Component.translatable(opt.getValueTranslation()).withStyle(ChatFormatting.GRAY))
                );
            }
            if(!options.cores.getUpgrades().isEmpty()) {
                lines.add(Component.literal(""));
                lines.add(Component.translatable(langTooltip + "cores").withStyle(ChatFormatting.GRAY));

                for(IWandCore core : options.cores.getUpgrades()) {
                    lines.add(Component.translatable(options.cores.getKeyTranslation() + "." + core.getRegistryName().toString()));
                }
            }
        }
        // Default tooltip: show block limit + active wand core
        else {
            IOption<?> opt = options.allOptions[0];
            lines.add(Component.translatable(langTooltip + "blocks", limit).withStyle(ChatFormatting.GRAY));
            lines.add(Component.translatable(opt.getKeyTranslation()).withStyle(ChatFormatting.AQUA)
                    .append(Component.translatable(opt.getValueTranslation()).withStyle(ChatFormatting.WHITE)));
            lines.add(Component.translatable(langTooltip + "shift").withStyle(ChatFormatting.AQUA));
        }
    }

    public static void optionMessage(Player player, IOption<?> option) {
        player.sendOverlayMessage(
                        Component.translatable(option.getKeyTranslation()).withStyle(ChatFormatting.AQUA)
                        .append(Component.translatable(option.getValueTranslation()).withStyle(ChatFormatting.WHITE))
                        .append(Component.literal(" - ").withStyle(ChatFormatting.GRAY))
                .append(Component.translatable(option.getDescTranslation()).withStyle(ChatFormatting.WHITE)));
    }

}
