package com.lux.setting;

public class FloatSetting extends Setting<Float> {
    private final float min, max;
    public FloatSetting(String name, String desc, float def, float min, float max) {
        super(name, desc, def); this.min = min; this.max = max;
    }
    @Override public void setValue(Float v) { super.setValue(Math.max(min, Math.min(max, v))); }
    public float getMin() { return min; }
    public float getMax() { return max; }
    public float getPercent() { return (value - min) / (max - min); }
    @Override public String getDisplayValue() { return String.format("%.2f", value); }
}
