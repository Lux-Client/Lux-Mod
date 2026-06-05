package com.lux.module.render;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.ColorSetting;
import com.lux.setting.FloatSetting;

public class HitColorModule extends Module {
    private ColorSetting hitColor;
    private FloatSetting duration;

    public HitColorModule() { super("Hit Color", "Changes entity flash color when hit", Category.RENDER); }

    @Override protected void init() {
        hitColor = register(new ColorSetting("Hit Color", "Flash color when hit", 0xFFFF0000));
        duration = register(new FloatSetting("Duration", "Flash duration in seconds", 0.4f, 0.1f, 2.0f));
    }

    public int getHitColor()   { return hitColor.getValue(); }
    public float getDuration() { return duration.getValue(); }
}
