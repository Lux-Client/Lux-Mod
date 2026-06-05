package com.lux.module.cosmetic;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.BooleanSetting;
import com.lux.setting.ColorSetting;
import com.lux.setting.EnumSetting;
import com.lux.setting.FloatSetting;

public class WingsModule extends Module {

    public enum WingStyle { ANGEL, DEVIL, BUTTERFLY, DRAGON }

    private EnumSetting<WingStyle> style;
    private ColorSetting tint;
    private FloatSetting scale;
    private BooleanSetting flapAnimation;

    public WingsModule() {
        super("Wings", "Adds decorative wings to your back", Category.COSMETICS);
    }

    @Override
    protected void init() {
        style         = register(new EnumSetting<>("Style", "Wing style", WingStyle.ANGEL));
        tint          = register(new ColorSetting("Tint", "Wing color tint", 0xFFFFFFFF));
        scale         = register(new FloatSetting("Scale", "Wing size multiplier", 1.0f, 0.5f, 2.0f));
        flapAnimation = register(new BooleanSetting("Flap", "Animate wing flapping", true));
    }

    public WingStyle getWingStyle() { return style.getValue(); }
    public int getTint()            { return tint.getValue(); }
    public float getScale()         { return scale.getValue(); }
    public boolean flaps()          { return flapAnimation.getValue(); }
}
