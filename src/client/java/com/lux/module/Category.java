package com.lux.module;

public enum Category {
    RENDER("Render",           0xFF4E9FE2),
    HUD("HUD",                 0xFF4EE2A1),
    QUALITY_OF_LIFE("QoL",    0xFFE2C84E),
    COSMETICS("Cosmetics",    0xFFE24E9F),
    PERFORMANCE("Performance", 0xFFE2714E);

    public final String displayName;
    public final int color;

    Category(String displayName, int color) {
        this.displayName = displayName;
        this.color       = color;
    }
}
