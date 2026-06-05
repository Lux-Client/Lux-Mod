package com.lux.setting;

public class EnumSetting<E extends Enum<E>> extends Setting<E> {
    private final E[] values;

    @SuppressWarnings("unchecked")
    public EnumSetting(String name, String desc, E def) {
        super(name, desc, def);
        this.values = (E[]) def.getDeclaringClass().getEnumConstants();
    }

    public void cycle() { setValue(values[(value.ordinal() + 1) % values.length]); }
    public E[] getValues() { return values; }

    @Override
    public String getDisplayValue() {
        String s = value.name().toLowerCase().replace('_', ' ');
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
