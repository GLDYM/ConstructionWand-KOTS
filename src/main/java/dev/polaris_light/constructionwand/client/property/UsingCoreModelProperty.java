package dev.polaris_light.constructionwand.client.property;

import com.mojang.serialization.MapCodec;
import dev.polaris_light.constructionwand.basics.option.WandOptions;
import dev.polaris_light.constructionwand.items.wand.ItemWand;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public final class UsingCoreModelProperty implements RangeSelectItemModelProperty {
    public static final UsingCoreModelProperty INSTANCE = new UsingCoreModelProperty();
    public static final MapCodec<UsingCoreModelProperty> MAP_CODEC = MapCodec.unit(INSTANCE);

    private UsingCoreModelProperty() {
    }

    @Override
    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        if (!(stack.getItem() instanceof ItemWand)) {
            return 0.0F;
        }

        return new WandOptions(stack).cores.get().getColor() != -1 ? 1.0F : 0.0F;
    }

    @Override
    public MapCodec<? extends RangeSelectItemModelProperty> type() {
        return MAP_CODEC;
    }
}
