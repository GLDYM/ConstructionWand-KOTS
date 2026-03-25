package dev.polaris_light.constructionwand.network;

import dev.polaris_light.constructionwand.ConstructionWand;
import dev.polaris_light.constructionwand.client.ClientHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashSet;
import java.util.Set;

public record PacketPreviewResult(HashSet<BlockPos> previewBlocks) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, PacketPreviewResult> CODEC = CustomPacketPayload.codec(
            PacketPreviewResult::encode,
            PacketPreviewResult::new);
    public static final Type<PacketPreviewResult> ID = new Type<>(ConstructionWand.loc("preview_result"));

    public PacketPreviewResult(FriendlyByteBuf buffer) {
        this(getSet(buffer));
    }

    public PacketPreviewResult(Set<BlockPos> previewBlocks) {
        this(new HashSet<>(previewBlocks));
    }

    private static HashSet<BlockPos> getSet(FriendlyByteBuf buffer) {
        HashSet<BlockPos> previewBlocks = new HashSet<>();
        while (buffer.isReadable()) {
            previewBlocks.add(buffer.readBlockPos());
        }
        return previewBlocks;
    }

    public void encode(FriendlyByteBuf buffer) {
        for (BlockPos pos : previewBlocks) {
            buffer.writeBlockPos(pos);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static class Handler {
        public static void handle(final PacketPreviewResult msg, final IPayloadContext ctx) {
            ctx.enqueueWork(() -> ClientHandler.renderBlockPreview.previewBlocks = msg.previewBlocks)
                    .exceptionally(e -> {
                        ctx.disconnect(Component.translatable("constructionwand.networking.preview_result.failed", e.getMessage()));
                        return null;
                    });
        }
    }
}
