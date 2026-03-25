package dev.polaris_light.constructionwand.network;

import dev.polaris_light.constructionwand.ConstructionWand;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketQueryUndo(boolean undoPressed, boolean shiftPressed) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, PacketQueryUndo> CODEC = CustomPacketPayload.codec(
            PacketQueryUndo::encode,
            PacketQueryUndo::new);
    public static final Type<PacketQueryUndo> ID = new Type<>(ConstructionWand.loc("query_undo"));

    public PacketQueryUndo(FriendlyByteBuf buffer) {
        this(buffer.readBoolean(), buffer.readBoolean());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(undoPressed);
        buffer.writeBoolean(shiftPressed);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static class Handler {
        public static void handle(final PacketQueryUndo msg, final IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                        if (ctx.flow().isServerbound() && ctx.player() instanceof ServerPlayer player) {
                            ConstructionWand.undoHistory.updateClient(player, msg.undoPressed, msg.shiftPressed);
                        }
                    })
                    .exceptionally(e -> {
                        ctx.disconnect(Component.translatable("constructionwand.networking.query_undo.failed", e.getMessage()));
                        return null;
                    });
        }
    }
}
