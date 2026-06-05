package com.lux.module.render;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.*;

public class NametagModule extends Module {
    private FloatSetting scale;
    private BooleanSetting showHealth, showPing, showDistance;
    private ColorSetting background;

    public NametagModule() { super("Nametag", "Improves nametag display above players", Category.RENDER); }

    @Override protected void init() {
        scale       = register(new FloatSetting("Scale", "Nametag size multiplier", 1.0f, 0.5f, 3.0f));
        showHealth  = register(new BooleanSetting("Health", "Display player HP", true));
        showPing    = register(new BooleanSetting("Ping", "Display latency", false));
        showDistance= register(new BooleanSetting("Distance", "Display distance", false));
        background  = register(new ColorSetting("Background", "Background plate color", 0x66000000));
    }

    public float getScale()         { return scale.getValue(); }
    public boolean showsHealth()    { return showHealth.getValue(); }
    public boolean showsPing()      { return showPing.getValue(); }
    public boolean showsDistance()  { return showDistance.getValue(); }
    public int getBackground()      { return background.getValue(); }
}
