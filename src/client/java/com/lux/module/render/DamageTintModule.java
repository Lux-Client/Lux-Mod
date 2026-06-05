package com.lux.module.render;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.BooleanSetting;
import com.lux.setting.ColorSetting;

public class DamageTintModule extends Module {
    private ColorSetting tintColor;
    private BooleanSetting replaceVanilla;

    public DamageTintModule() { super("Damage Tint", "Custom damage screen overlay color", Category.RENDER); }

    @Override protected void init() {
        tintColor      = register(new ColorSetting("Tint Color", "Screen tint on damage", 0x66FF0000));
        replaceVanilla = register(new BooleanSetting("Replace Vanilla", "Disable red vignette", true));
    }

    public int getTintColor()      { return tintColor.getValue(); }
    public boolean replacesVanilla() { return replaceVanilla.getValue(); }
}
