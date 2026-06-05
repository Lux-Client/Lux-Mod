package com.lux.module.render;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.FloatSetting;

public class FullbrightModule extends Module {
    private FloatSetting brightness;

    public FullbrightModule() { super("Fullbright", "Maximizes in-game brightness", Category.RENDER); }

    @Override protected void init() {
        brightness = register(new FloatSetting("Brightness", "Gamma override value", 100.0f, 1.0f, 100.0f));
    }

    public float getBrightness() { return brightness.getValue(); }
}
