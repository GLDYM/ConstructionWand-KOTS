package dev.polaris_light.constructionwand.network;

import dev.polaris_light.constructionwand.ConstructionWand;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModMessages {

    private ModMessages() {
    }

    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(ConstructionWand.MODID);

        registrar.playToClient(PacketUndoBlocks.ID, PacketUndoBlocks.CODEC, PacketUndoBlocks.Handler::handle);
        registrar.playToServer(PacketQueryUndo.ID, PacketQueryUndo.CODEC, PacketQueryUndo.Handler::handle);
        registrar.playToServer(PacketWandOption.ID, PacketWandOption.CODEC, PacketWandOption.Handler::handle);
        registrar.playToServer(PacketRequestPreview.ID, PacketRequestPreview.CODEC, PacketRequestPreview.Handler::handle);
        registrar.playToClient(PacketPreviewResult.ID, PacketPreviewResult.CODEC, PacketPreviewResult.Handler::handle);
    }

    public static void sendToPlayer(CustomPacketPayload message, ServerPlayer player) {
        player.connection.send(message);
    }
}