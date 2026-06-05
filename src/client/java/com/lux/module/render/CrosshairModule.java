package com.lux.module.render;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.*;
import net.minecraft.client.gui.DrawContext;

public class CrosshairModule extends Module {
    public enum Style { CROSS, DOT, CIRCLE, PLUS }
    private EnumSetting<Style> style;
    private ColorSetting color;
    private IntSetting thickness, size;

    public CrosshairModule() { super("Crosshair Editor", "Customize your crosshair", Category.RENDER); }

    @Override protected void init() {
        style     = register(new EnumSetting<>("Style", "Crosshair style", Style.CROSS));
        color     = register(new ColorSetting("Color", "Crosshair color", 0xFFFFFFFF));
        thickness = register(new IntSetting("Thickness", "Line thickness", 2, 1, 4));
        size      = register(new IntSetting("Size", "Half-size in px", 5, 2, 15));
    }

    @Override public void onRenderHUD(DrawContext ctx, float delta) {
        int w = mc.getWindow().getScaledWidth(), h = mc.getWindow().getScaledHeight();
        int cx = w/2, cy = h/2, sz = size.getValue(), th = thickness.getValue(), c = color.getValue();
        switch (style.getValue()) {
            case CROSS -> {
                ctx.fill(cx - sz, cy - th/2, cx + sz, cy + (th+1)/2, c);
                ctx.fill(cx - th/2, cy - sz, cx + (th+1)/2, cy + sz, c);
            }
            case DOT   -> ctx.fill(cx - th, cy - th, cx + th, cy + th, c);
            case PLUS  -> {
                ctx.fill(cx - sz, cy - th/2, cx - th, cy + (th+1)/2, c);
                ctx.fill(cx + th, cy - th/2, cx + sz, cy + (th+1)/2, c);
                ctx.fill(cx - th/2, cy - sz, cx + (th+1)/2, cy - th, c);
                ctx.fill(cx - th/2, cy + th, cx + (th+1)/2, cy + sz, c);
            }
            case CIRCLE -> {
                for (int i = 0; i < 360; i += 5) {
                    int px = (int)(cx + Math.cos(Math.toRadians(i)) * sz);
                    int py = (int)(cy + Math.sin(Math.toRadians(i)) * sz);
                    ctx.fill(px - 1, py - 1, px + 1, py + 1, c);
                }
            }
        }
    }
}
