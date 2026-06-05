package com.lux.setting;

public class ColorSetting extends Setting<Integer> {
    public ColorSetting(String name, String desc, int def) { super(name, desc, def); }
    public int getAlpha() { return (value >> 24) & 0xFF; }
    public int getRed()   { return (value >> 16) & 0xFF; }
    public int getGreen() { return (value >> 8)  & 0xFF; }
    public int getBlue()  { return  value        & 0xFF; }
    public void setRGBA(int r, int g, int b, int a) { setValue((a << 24) | (r << 16) | (g << 8) | b); }
    @Override public String getDisplayValue() { return String.format("#%08X", value); }
}
