package com.lux.module.render;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.FloatSetting;

public class MotionBlurModule extends Module {
    private FloatSetting strength;

    public MotionBlurModule() { super("Motion Blur", "Adds trailing blur to fast camera movement", Category.RENDER); }

    @Override protected void init() {
        strength = register(new FloatSetting("Strength", "Blend factor (lower = more blur)", 0.5f, 0.1f, 0.95f));
    }

    public float getStrength() { return strength.getValue(); }
}
