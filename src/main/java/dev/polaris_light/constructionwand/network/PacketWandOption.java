package dev.polaris_light.constructionwand.network;

import dev.polaris_light.constructionwand.ConstructionWand;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import dev.polaris_light.constructionwand.basics.WandUtil;
import dev.polaris_light.constructionwand.basics.option.IOption;
import dev.polaris_light.constructionwand.basics.option.WandOptions;
import dev.polaris_light.constructionwand.items.wand.ItemWand;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketWandOption(String key, String value, boolean shouldNotify) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, PacketWandOption> CODEC = CustomPacketPayload.codec(
            PacketWandOption::encode,
            PacketWandOption::new);
    public static final Type<PacketWandOption> ID = new Type<>(ConstructionWand.loc("wand_option"));

    public PacketWandOption(IOption<?> option, boolean shouldNotify) {
        this(option.getKey(), option.getValueString(), shouldNotify);
    }

    public PacketWandOption(FriendlyByteBuf buffer) {
        this(buffer.readUtf(100), buffer.readUtf(100), buffer.readBoolean());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(key);
        buffer.writeUtf(value);
        buffer.writeBoolean(shouldNotify);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static class Handler {
        public static void handle(final PacketWandOption msg, final IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                        if (ctx.flow().isServerbound() && ctx.player() instanceof ServerPlayer player) {
                            ItemStack wand = WandUtil.holdingWand(player);
                            if (wand == null) return;
                            WandOptions options = new WandOptions(wand);

                            IOption<?> option = options.get(msg.key);
                            if (option == null) return;
                            option.setValueString(msg.value);

                            if (msg.shouldNotify) ItemWand.optionMessage(player, option);
                            player.getInventory().setChanged();
                        }
                    })
                    .exceptionally(e -> {
                        ctx.disconnect(Component.translatable("constructionwand.networking.wand_option.failed", e.getMessage()));
                        return null;
                    });
        }
    }
}
