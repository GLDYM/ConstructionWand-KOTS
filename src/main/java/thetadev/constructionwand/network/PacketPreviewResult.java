package thetadev.constructionwand.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import thetadev.constructionwand.ConstructionWand;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;


public class PacketPreviewResult {
    public final Set<BlockPos> previewBlocks;

    public PacketPreviewResult(Set<BlockPos> previewBlocks) {
        this.previewBlocks = new HashSet<>(previewBlocks);
    }

    private PacketPreviewResult(HashSet<BlockPos> previewBlocks) {
        this.previewBlocks = previewBlocks;
    }

    public static void encode(PacketPreviewResult msg, FriendlyByteBuf buffer) {
        for (BlockPos pos : msg.previewBlocks) {
            buffer.writeBlockPos(pos);
        }
    }

    public static PacketPreviewResult decode(FriendlyByteBuf buffer) {
        HashSet<BlockPos> previewBlocks = new HashSet<>();
        while (buffer.isReadable()) {
            previewBlocks.add(buffer.readBlockPos());
        }
        return new PacketPreviewResult(previewBlocks);
    }

    public static class Handler {
        public static void handle(final PacketPreviewResult msg, final Supplier<NetworkEvent.Context> ctx) {
            if (!ctx.get().getDirection().getReceptionSide().isClient()) return;
            ConstructionWand.instance.renderBlockPreview.previewBlocks = msg.previewBlocks;
            ctx.get().setPacketHandled(true);
        }
    }
}
