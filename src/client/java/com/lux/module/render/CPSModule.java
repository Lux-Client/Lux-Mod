package com.lux.module.render;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.BooleanSetting;
import com.lux.setting.ColorSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CPSModule extends Module {
    public static final List<Long> leftClicks  = new ArrayList<>();
    public static final List<Long> rightClicks = new ArrayList<>();

    private BooleanSetting shadow;
    private ColorSetting textColor;
    private BooleanSetting showRight;

    public CPSModule() { super("CPS Counter", "Shows clicks per second", Category.RENDER); }

    @Override protected void init() {
        shadow    = register(new BooleanSetting("Shadow", "Draw text shadow", true));
        textColor = register(new ColorSetting("Color", "Text color", 0xFFFFFFFF));
        showRight = register(new BooleanSetting("Show Right", "Include right CPS", true));
    }

    @Override public void onRenderHUD(DrawContext ctx, float delta) {
        long now = Util.getMeasuringTimeMs();
        trim(leftClicks, now); trim(rightClicks, now);
        String t = showRight.getValue()
                ? leftClicks.size() + " | " + rightClicks.size() + " CPS"
                : leftClicks.size() + " CPS";
        if (shadow.getValue()) ctx.drawTextWithShadow(mc.textRenderer, t, getHudX(), getHudY(), textColor.getValue());
        else ctx.drawText(mc.textRenderer, t, getHudX(), getHudY(), textColor.getValue(), false);
    }

    private static void trim(List<Long> list, long now) {
        Iterator<Long> it = list.iterator();
        while (it.hasNext()) if (now - it.next() > 1000) it.remove();
    }
}
