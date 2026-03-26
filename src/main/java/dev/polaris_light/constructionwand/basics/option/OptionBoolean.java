package dev.polaris_light.constructionwand.basics.option;

import net.minecraft.nbt.CompoundTag;

public class OptionBoolean implements IOption<Boolean>
{
    private final CompoundTag tag;
    private final String key;
    private final boolean enabled;
    private final Runnable onChanged;
    private boolean value;

    public OptionBoolean(CompoundTag tag, String key, boolean dval, boolean enabled, Runnable onChanged) {
        this.tag = tag;
        this.key = key;
        this.enabled = enabled;
        this.onChanged = onChanged;

        if(tag.contains(key)) value = tag.getBoolean(key).orElse(dval);
        else value = dval;
    }

    public OptionBoolean(CompoundTag tag, String key, boolean dval, boolean enabled) {
        this(tag, key, dval, enabled, null);
    }

    public OptionBoolean(CompoundTag tag, String key, boolean dval) {
        this(tag, key, dval, true);
    }

    public OptionBoolean(CompoundTag tag, String key, boolean dval, Runnable onChanged) {
        this(tag, key, dval, true, onChanged);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValueString() {
        return value ? "yes" : "no";
    }

    @Override
    public void setValueString(String val) {
        set(val.equals("yes"));
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void set(Boolean val) {
        if(!enabled) return;
        value = val;
        tag.putBoolean(key, value);
        if (onChanged != null) onChanged.run();
    }

    @Override
    public Boolean get() {
        return value;
    }

    @Override
    public Boolean next(boolean dir) {
        set(!value);
        return value;
    }
}
