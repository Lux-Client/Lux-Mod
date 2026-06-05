package com.lux.setting;

public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, String description, boolean def) { super(name, description, def); }
    public void toggle() { setValue(!value); }
    @Override public String getDisplayValue() { return value ? "ON" : "OFF"; }
}
