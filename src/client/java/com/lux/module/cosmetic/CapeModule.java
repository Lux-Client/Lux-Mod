package com.lux.module.cosmetic;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.BooleanSetting;
import com.lux.setting.EnumSetting;

public class CapeModule extends Module {

    public enum CapeStyle { CLASSIC, OPTIFINE, LUNAR, BADLION, CUSTOM }

    private EnumSetting<CapeStyle> style;
    private BooleanSetting flipWhenSprinting;
    private BooleanSetting animated;

    public CapeModule() {
        super("Cape", "Displays a cosmetic cape on your character", Category.COSMETICS);
    }

    @Override
    protected void init() {
        style              = register(new EnumSetting<>("Style", "Cape style preset", CapeStyle.CLASSIC));
        flipWhenSprinting  = register(new BooleanSetting("Flip on Sprint", "Flip cape while sprinting", true));
        animated           = register(new BooleanSetting("Animated", "Animate cape texture", false));
    }

    public CapeStyle getCapeStyle()     { return style.getValue(); }
    public boolean flipsWhenSprinting() { return flipWhenSprinting.getValue(); }
    public boolean isAnimated()         { return animated.getValue(); }
}
