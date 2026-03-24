package dev.polaris_light.constructionwand.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import dev.polaris_light.constructionwand.ConstructionWand;

import java.util.function.Supplier;

public class PacketQueryUndo
{
    public boolean undoPressed;
    public boolean shiftPressed;

    public PacketQueryUndo(boolean undoPressed, boolean shiftPressed) {
        this.undoPressed = undoPressed;
        this.shiftPressed = shiftPressed;
    }

    public static void encode(PacketQueryUndo msg, FriendlyByteBuf buffer) {
        buffer.writeBoolean(msg.undoPressed);
        buffer.writeBoolean(msg.shiftPressed);
    }

    public static PacketQueryUndo decode(FriendlyByteBuf buffer) {
        return new PacketQueryUndo(buffer.readBoolean(), buffer.readBoolean());
    }

    public static class Handler
    {
        public static void handle(final PacketQueryUndo msg, final Supplier<NetworkEvent.Context> ctx) {
            if(!ctx.get().getDirection().getReceptionSide().isServer()) return;

            ServerPlayer player = ctx.get().getSender();
            if(player == null) return;

            ConstructionWand.instance.undoHistory.updateClient(player, msg.undoPressed, msg.shiftPressed);

            //ConstructionWand.LOGGER.debug("Undo queried");
        }
    }
}
