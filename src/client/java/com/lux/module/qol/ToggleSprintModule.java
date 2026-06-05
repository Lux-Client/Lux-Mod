package com.lux.module.qol;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.BooleanSetting;

public class ToggleSprintModule extends Module {
    private BooleanSetting onlyForward;

    public ToggleSprintModule() { super("Toggle Sprint", "Sprints automatically when moving forward", Category.QUALITY_OF_LIFE); }

    @Override protected void init() {
        onlyForward = register(new BooleanSetting("Only Forward", "Only sprint when pressing W", true));
    }

    public boolean isOnlyForward() { return onlyForward.getValue(); }
}
