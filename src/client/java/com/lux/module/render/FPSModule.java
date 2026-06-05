package com.lux.module.render;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.BooleanSetting;
import com.lux.setting.ColorSetting;
import com.lux.setting.EnumSetting;
import net.minecraft.client.gui.DrawContext;

public class FPSModule extends Module {
    public enum Mode { COMPACT, DETAILED }
    private BooleanSetting shadow;
    private ColorSetting textColor;
    private EnumSetting<Mode> mode;

    public FPSModule() { super("FPS Counter", "Displays your current frames per second", Category.RENDER); }

    @Override protected void init() {
        mode      = register(new EnumSetting<>("Mode", "Display mode", Mode.COMPACT));
        shadow    = register(new BooleanSetting("Shadow", "Draw text shadow", true));
        textColor = register(new ColorSetting("Color", "Text color", 0xFFFFFFFF));
    }

    @Override public void onRenderHUD(DrawContext ctx, float delta) {
        int fps  = mc.getCurrentFps();
        String t = mode.getValue() == Mode.COMPACT ? fps + " FPS" : "FPS: " + fps;
        if (shadow.getValue()) ctx.drawTextWithShadow(mc.textRenderer, t, getHudX(), getHudY(), textColor.getValue());
        else ctx.drawText(mc.textRenderer, t, getHudX(), getHudY(), textColor.getValue(), false);
    }
}
