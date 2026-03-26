package dev.polaris_light.constructionwand.basics.option;

import com.google.common.base.Enums;
import net.minecraft.nbt.CompoundTag;

public class OptionEnum<E extends Enum<E>> implements IOption<E>
{
    private final CompoundTag tag;
    private final String key;
    private final Class<E> enumClass;
    private final boolean enabled;
    private final E dval;
    private final Runnable onChanged;
    private E value;

    public OptionEnum(CompoundTag tag, String key, Class<E> enumClass, E dval, boolean enabled, Runnable onChanged) {
        this.tag = tag;
        this.key = key;
        this.enumClass = enumClass;
        this.enabled = enabled;
        this.dval = dval;
        this.onChanged = onChanged;

        value = Enums.getIfPresent(enumClass, tag.getString(key).orElse("").toUpperCase()).or(dval);
    }

    public OptionEnum(CompoundTag tag, String key, Class<E> enumClass, E dval, boolean enabled) {
        this(tag, key, enumClass, dval, enabled, null);
    }

    public OptionEnum(CompoundTag tag, String key, Class<E> enumClass, E dval) {
        this(tag, key, enumClass, dval, true);
    }

    public OptionEnum(CompoundTag tag, String key, Class<E> enumClass, E dval, Runnable onChanged) {
        this(tag, key, enumClass, dval, true, onChanged);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValueString() {
        return value.name().toLowerCase();
    }

    @Override
    public void setValueString(String val) {
        set(Enums.getIfPresent(enumClass, val.toUpperCase()).or(dval));
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void set(E val) {
        if(!enabled) return;
        value = val;
        tag.putString(key, getValueString());
        if (onChanged != null) onChanged.run();
    }

    @Override
    public E get() {
        return value;
    }

    @Override
    public E next(boolean dir) {
        E[] enumValues = enumClass.getEnumConstants();
        int i = value.ordinal() + (dir ? 1 : -1);
        if(i < 0) i += enumValues.length;
        set(enumValues[i % enumValues.length]);
        return value;
    }
}
