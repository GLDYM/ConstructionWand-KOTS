package dev.polaris_light.constructionwand.client;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Set;

public class RenderBlockPreview {
    private BlockHitResult lastRayTraceResult = null;
    private ItemStack lastWand = ItemStack.EMPTY;
    public Set<BlockPos> undoBlocks;
    public Set<BlockPos> previewBlocks;

    // TODO: fix this
    // @SubscribeEvent
    // public void renderBlockHighlight(RenderHighlightEvent.Block event) {
    //     if(event.getTarget().getType() != HitResult.Type.BLOCK) return;

    //     BlockHitResult rtr = event.getTarget();
    //     Entity entity = event.getCamera().getEntity();
    //     if(!(entity instanceof Player player)) return;
    //     Set<BlockPos> blocks;
    //     float colorR = 0, colorG = 0, colorB = 0;

    //     ItemStack wand = WandUtil.holdingWand(player);
    //     if(wand == null) return;

    //     if (KeybindHandler.isOptKeyDown()) {
    //         blocks = undoBlocks;
    //         colorG = 1;
    //     }
    //     else {
    //         // Use cached previews of the same target pos/dir
    //         // Exception: always update if blockCount < 2 to prevent 1-block previews when block updates
    //         // from the last placement are lagging
    //         if(lastRayTraceResult == null || !compareRTR(lastRayTraceResult, rtr) || lastWand.equals(wand)
    //             || previewBlocks == null || previewBlocks.size() < 2) {
    //             lastRayTraceResult = rtr;
    //             lastWand = wand;
    //             ModMessages.sendToServer(new PacketRequestPreview(rtr, wand));
    //         }
    //         blocks = previewBlocks;
    //     }

    //     if(blocks == null || blocks.isEmpty()) return;

    //     PoseStack ms = event.getPoseStack();
    //     MultiBufferSource buffer = event.getMultiBufferSource();
    //     VertexConsumer lineBuilder = buffer.getBuffer(RenderType.LINES);

    //     Vec3 cameraPos = event.getCamera().getPosition();
    //     double d0 = cameraPos.x;
    //     double d1 = cameraPos.y;
    //     double d2 = cameraPos.z;

    //     for(BlockPos block : blocks) {
    //         AABB aabb = new AABB(block).move(-d0, -d1, -d2);
    //         LevelRenderer.renderLineBox(ms, lineBuilder, aabb, colorR, colorG, colorB, 0.4F);
    //     }

    //     event.setCanceled(true);
    // }

    public void reset() {
        lastRayTraceResult = null;
        lastWand = ItemStack.EMPTY;
        previewBlocks = null;
    }

    private static boolean compareRTR(BlockHitResult rtr1, BlockHitResult rtr2) {
        return rtr1.getBlockPos().equals(rtr2.getBlockPos()) && rtr1.getDirection().equals(rtr2.getDirection());
    }
}
