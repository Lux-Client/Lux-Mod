package com.lux.module.performance;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.BooleanSetting;
import com.lux.setting.IntSetting;

public class FPSOptModule extends Module {

    private BooleanSetting disableWeather;
    private BooleanSetting disableSky;
    private BooleanSetting limitBackground;
    private IntSetting backgroundFPS;

    public FPSOptModule() {
        super("FPS Optimizer", "Disables costly visual effects to maximize FPS", Category.PERFORMANCE);
    }

    @Override
    protected void init() {
        disableWeather  = register(new BooleanSetting("No Weather", "Disable rain and snow visuals", false));
        disableSky      = register(new BooleanSetting("No Sky", "Disable sky rendering", false));
        limitBackground = register(new BooleanSetting("Limit Background FPS", "Cap FPS when unfocused", true));
        backgroundFPS   = register(new IntSetting("Background FPS Cap", "Max FPS when window is unfocused", 10, 1, 60));
    }

    public boolean noWeather()        { return isEnabled() && disableWeather.getValue(); }
    public boolean noSky()            { return isEnabled() && disableSky.getValue(); }
    public boolean limitsBackground() { return isEnabled() && limitBackground.getValue(); }
    public int getBackgroundFPS()     { return backgroundFPS.getValue(); }
}
