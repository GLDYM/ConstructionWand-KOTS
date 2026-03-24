package dev.polaris_light.constructionwand.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import dev.polaris_light.constructionwand.items.wand.ItemWand;
import dev.polaris_light.constructionwand.wand.WandJob;
import dev.polaris_light.constructionwand.ConstructionWand;

import java.util.Set;
import java.util.function.Supplier;


public class PacketRequestPreview {
    private final BlockHitResult rtr;
    private final ItemStack wand;

    public PacketRequestPreview(BlockHitResult rtr, ItemStack wand) {
        this.rtr = rtr;
        this.wand = wand;
    }

    public static void encode(PacketRequestPreview msg, FriendlyByteBuf buf) {
        buf.writeItem(msg.wand);
        buf.writeBlockHitResult(msg.rtr);
    }

    public static PacketRequestPreview decode(FriendlyByteBuf buf) {
        ItemStack wand = buf.readItem();
        BlockHitResult rtr = buf.readBlockHitResult();
        return new PacketRequestPreview(rtr, wand);
    }

    public static class Handler
    {
        public static void handle(PacketRequestPreview msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player == null) return;

                WandJob job = ItemWand.getWandJob(player, player.level(), msg.rtr, msg.wand);
                Set<BlockPos> blocks = job.getBlockPositions();

                ConstructionWand.instance.HANDLER.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new PacketPreviewResult(blocks)
                );
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
