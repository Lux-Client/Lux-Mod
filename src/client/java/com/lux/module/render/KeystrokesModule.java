package com.lux.module.render;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.BooleanSetting;
import com.lux.setting.ColorSetting;
import com.lux.setting.IntSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Util;

import java.util.Iterator;
import java.util.List;

public class KeystrokesModule extends Module {
    private IntSetting keySize;
    private ColorSetting pressedColor, releasedColor, textColor;
    private BooleanSetting showMouse;

    public KeystrokesModule() { super("Keystrokes", "Displays WASD and mouse button presses", Category.RENDER); }

    @Override protected void init() {
        keySize       = register(new IntSetting("Key Size", "Size of each key in px", 22, 14, 40));
        pressedColor  = register(new ColorSetting("Pressed Color", "Color when pressed", 0xCC4EE2A1));
        releasedColor = register(new ColorSetting("Released Color", "Color when released", 0xAA222222));
        textColor     = register(new ColorSetting("Text Color", "Label color", 0xFFFFFFFF));
        showMouse     = register(new BooleanSetting("Show Mouse", "Show LMB/RMB", true));
    }

    @Override public void onRenderHUD(DrawContext ctx, float delta) {
        if (mc.player == null) return;
        int sz = keySize.getValue(), gap = 2, ox = getHudX(), oy = getHudY();
        drawKey(ctx, ox + sz + gap, oy,               sz, "W", mc.options.forwardKey.isPressed());
        drawKey(ctx, ox,            oy + sz + gap,    sz, "A", mc.options.leftKey.isPressed());
        drawKey(ctx, ox + sz + gap, oy + sz + gap,    sz, "S", mc.options.backKey.isPressed());
        drawKey(ctx, ox+sz*2+gap*2, oy + sz + gap,    sz, "D", mc.options.rightKey.isPressed());
        if (showMouse.getValue()) {
            long now = Util.getMeasuringTimeMs();
            trim(CPSModule.leftClicks, now); trim(CPSModule.rightClicks, now);
            int my = oy + (sz + gap) * 2;
            drawKey(ctx, ox,            my, sz, "LMB", !CPSModule.leftClicks.isEmpty());
            drawKey(ctx, ox + sz + gap, my, sz, "RMB", !CPSModule.rightClicks.isEmpty());
        }
    }

    private void drawKey(DrawContext ctx, int x, int y, int sz, String label, boolean pressed) {
        ctx.fill(x, y, x + sz, y + sz, pressed ? pressedColor.getValue() : releasedColor.getValue());
        int lx = x + sz/2 - mc.textRenderer.getWidth(label)/2;
        int ly = y + sz/2 - mc.textRenderer.fontHeight/2;
        ctx.drawTextWithShadow(mc.textRenderer, label, lx, ly, textColor.getValue());
    }

    private static void trim(List<Long> list, long now) {
        Iterator<Long> it = list.iterator();
        while (it.hasNext()) if (now - it.next() > 1000) it.remove();
    }
}
