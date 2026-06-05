package com.lux.module.qol;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.BooleanSetting;

public class ToggleSneakModule extends Module {
    private boolean sneaking;
    private BooleanSetting toggleMode;

    public ToggleSneakModule() { super("Toggle Sneak", "Toggle sneak on/off without holding Shift", Category.QUALITY_OF_LIFE); }

    @Override protected void init() {
        toggleMode = register(new BooleanSetting("Toggle Mode", "Click to toggle vs hold", true));
    }

    public boolean isSneaking()        { return sneaking; }
    public void setSneaking(boolean s) { this.sneaking = s; }
    public boolean isToggleMode()      { return toggleMode.getValue(); }
}
