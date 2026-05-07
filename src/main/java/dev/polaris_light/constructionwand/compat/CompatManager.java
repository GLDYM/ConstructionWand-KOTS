package dev.polaris_light.constructionwand.compat;

import dev.polaris_light.constructionwand.compat.sable.SableCompat;
import net.neoforged.fml.ModList;

public final class CompatManager
{
    private static final ModCompat ACTIVE = create();

    private CompatManager() {
    }

    private static ModCompat create() {
        if(ModList.get().isLoaded("sable")) {
            return new SableCompat();
        }
        return ModCompat.DEFAULT;
    }

    public static ModCompat active() {
        return ACTIVE;
    }
}
