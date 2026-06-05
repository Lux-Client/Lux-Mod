package com.lux.setting;

public class IntSetting extends Setting<Integer> {
    private final int min, max;
    public IntSetting(String name, String desc, int def, int min, int max) {
        super(name, desc, def); this.min = min; this.max = max;
    }
    @Override public void setValue(Integer v) { super.setValue(Math.max(min, Math.min(max, v))); }
    public int getMin() { return min; }
    public int getMax() { return max; }
    public float getPercent() { return (float)(value - min) / (max - min); }
    @Override public String getDisplayValue() { return String.valueOf(value); }
}
