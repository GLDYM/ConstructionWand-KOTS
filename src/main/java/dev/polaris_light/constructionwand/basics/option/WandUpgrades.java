package dev.polaris_light.constructionwand.basics.option;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import dev.polaris_light.constructionwand.ConstructionWand;
import dev.polaris_light.constructionwand.api.IWandUpgrade;

import java.util.ArrayList;

public class WandUpgrades<T extends IWandUpgrade>
{
    protected final CompoundTag tag;
    protected final String key;
    protected final ArrayList<T> upgrades;
    protected final T dval;
    protected final Runnable onChanged;

    public WandUpgrades(CompoundTag tag, String key, T dval, Runnable onChanged) {
        this.tag = tag;
        this.key = key;
        this.dval = dval;
        this.onChanged = onChanged;

        upgrades = new ArrayList<>();
        if(dval != null) upgrades.add(0, dval);

        deserialize();
    }

    public WandUpgrades(CompoundTag tag, String key, T dval) {
        this(tag, key, dval, null);
    }

    protected void deserialize() {
        ListTag listnbt = tag.getList(key).orElseGet(ListTag::new);
        boolean require_fix = false;

        for(int i = 0; i < listnbt.size(); i++) {
            String str = listnbt.getString(i).orElse("");
            Item item = BuiltInRegistries.ITEM.get(Identifier.tryParse(str))
                    .map(holder -> holder.value())
                    .orElse(null);
            if (item == null) {
                require_fix = true;
                continue;
            }

            T data;
            try {
                //noinspection unchecked
                data = (T) item;
                upgrades.add(data);
            } catch(ClassCastException e) {
                ConstructionWand.LOGGER.warn("Invalid wand upgrade: " + str);
                require_fix = true;
            }
        }
        if(require_fix) serialize();
    }

    protected void serialize() {
        ListTag listnbt = new ListTag();

        for(T item : upgrades) {
            if(item == dval) continue;
            listnbt.add(StringTag.valueOf(item.getRegistryName().toString()));
        }
        tag.put(key, listnbt);
        if (onChanged != null) onChanged.run();
    }

    public boolean addUpgrade(T upgrade) {
        if(hasUpgrade(upgrade)) return false;

        upgrades.add(upgrade);
        serialize();
        return true;
    }

    public boolean hasUpgrade(T upgrade) {
        return upgrades.contains(upgrade);
    }

    public ArrayList<T> getUpgrades() {
        return upgrades;
    }
}
