package com.lux.module.render;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.BooleanSetting;
import com.lux.setting.EnumSetting;

public class FreelookModule extends Module {
    public enum Trigger { HOLD_BACK, HOLD_SPRINT, ALWAYS }
    private EnumSetting<Trigger> trigger;
    private BooleanSetting lockBody;

    public FreelookModule() { super("Freelook", "Look freely without rotating the player body", Category.RENDER); }

    @Override protected void init() {
        trigger  = register(new EnumSetting<>("Trigger", "Activation condition", Trigger.HOLD_BACK));
        lockBody = register(new BooleanSetting("Lock Body", "Keep player body facing forward", true));
    }

    public Trigger getTrigger() { return trigger.getValue(); }
    public boolean isBodyLocked() { return lockBody.getValue(); }
}
