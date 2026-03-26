package dev.polaris_light.constructionwand.client.property;

import com.mojang.serialization.MapCodec;
import dev.polaris_light.constructionwand.basics.option.WandOptions;
import dev.polaris_light.constructionwand.items.wand.ItemWand;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public final class CoreColorTintSource implements ItemTintSource {
    public static final CoreColorTintSource INSTANCE = new CoreColorTintSource();
    public static final MapCodec<CoreColorTintSource> MAP_CODEC = MapCodec.unit(INSTANCE);

    private CoreColorTintSource() {
    }

    @Override
    public int calculate(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        if (!(itemStack.getItem() instanceof ItemWand)) {
            return -1;
        }

        return new WandOptions(itemStack).cores.get().getColor();
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return MAP_CODEC;
    }
}
