package dev.polaris_light.constructionwand.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.polaris_light.constructionwand.basics.WandUtil;
import dev.polaris_light.constructionwand.network.PacketRequestPreview;
import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ExtractBlockOutlineRenderStateEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.Set;

public class RenderBlockPreview {
    private BlockHitResult lastRayTraceResult = null;
    private ItemStack lastWand = ItemStack.EMPTY;
    public Set<BlockPos> undoBlocks;
    public Set<BlockPos> previewBlocks;

    @SubscribeEvent
    public void renderBlockHighlight(ExtractBlockOutlineRenderStateEvent event) {
        BlockHitResult rtr = event.getHitResult();
        Entity entity = event.getCamera().entity();
        if (!(entity instanceof Player player)) {
            return;
        }

        Set<BlockPos> blocks;
        final int color;

        ItemStack wand = WandUtil.holdingWand(player);
        if (wand == null) {
            return;
        }

        if (KeybindHandler.isOptKeyDown()) {
            blocks = undoBlocks;
            color = ARGB.color(102, 0, 255, 0);
        } else {
            // Request a refresh if target/wand changed or cached preview is too small.
            if (lastRayTraceResult == null
                    || !compareRTR(lastRayTraceResult, rtr)
                    || !WandUtil.stackEquals(lastWand, wand)
                    || previewBlocks == null
                    || previewBlocks.size() < 2) {
                lastRayTraceResult = rtr;
                lastWand = wand.copy();
                ClientPacketDistributor.sendToServer(new PacketRequestPreview(rtr, wand));
            }
            blocks = previewBlocks;
            color = ARGB.color(102, 0, 0, 0);
        }

        if (blocks == null || blocks.isEmpty()) {
            return;
        }

        Vec3 cameraPos = event.getCamera().position();
        double d0 = cameraPos.x;
        double d1 = cameraPos.y;
        double d2 = cameraPos.z;

        event.addCustomRenderer((renderState, buffer, poseStack, translucentPass, levelRenderState) -> {
            if (translucentPass != renderState.isTranslucent()) {
                return false;
            }

            VertexConsumer lineBuilder = buffer.getBuffer(RenderTypes.lines());
            for (BlockPos block : blocks) {
                ShapeRenderer.renderShape(
                        poseStack,
                        lineBuilder,
                        Shapes.block(),
                        block.getX() - d0,
                        block.getY() - d1,
                        block.getZ() - d2,
                        color,
                        4.0F
                );
            }
            return true;
        });
    }

    public void reset() {
        lastRayTraceResult = null;
        lastWand = ItemStack.EMPTY;
        previewBlocks = null;
    }

    private static boolean compareRTR(BlockHitResult rtr1, BlockHitResult rtr2) {
        return rtr1.getBlockPos().equals(rtr2.getBlockPos()) && rtr1.getDirection().equals(rtr2.getDirection());
    }
}
