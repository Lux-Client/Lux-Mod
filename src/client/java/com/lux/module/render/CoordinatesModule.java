package com.lux.module.render;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.BooleanSetting;
import com.lux.setting.ColorSetting;
import com.lux.setting.EnumSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Direction;

public class CoordinatesModule extends Module {
    public enum Precision { INTEGER, ONE_DECIMAL, TWO_DECIMAL }
    private EnumSetting<Precision> precision;
    private BooleanSetting showDirection, showY, shadow;
    private ColorSetting textColor;

    public CoordinatesModule() { super("Coordinates", "Displays XYZ position", Category.RENDER); }

    @Override protected void init() {
        precision     = register(new EnumSetting<>("Precision", "Decimal places", Precision.INTEGER));
        showDirection = register(new BooleanSetting("Direction", "Show facing direction", true));
        showY         = register(new BooleanSetting("Show Y", "Include Y coordinate", true));
        shadow        = register(new BooleanSetting("Shadow", "Draw text shadow", true));
        textColor     = register(new ColorSetting("Color", "Text color", 0xFFFFFFFF));
    }

    @Override public void onRenderHUD(DrawContext ctx, float delta) {
        ClientPlayerEntity p = mc.player;
        if (p == null) return;
        String fmt = switch (precision.getValue()) {
            case ONE_DECIMAL -> "%.1f"; case TWO_DECIMAL -> "%.2f"; default -> "%.0f";
        };
        String coords = showY.getValue()
                ? "XYZ: " + String.format(fmt, p.getX()) + " / " + String.format(fmt, p.getY()) + " / " + String.format(fmt, p.getZ())
                : "XZ: " + String.format(fmt, p.getX()) + " / " + String.format(fmt, p.getZ());
        if (showDirection.getValue()) coords += " [" + p.getHorizontalFacing().getName().toUpperCase() + "]";
        if (shadow.getValue()) ctx.drawTextWithShadow(mc.textRenderer, coords, getHudX(), getHudY(), textColor.getValue());
        else ctx.drawText(mc.textRenderer, coords, getHudX(), getHudY(), textColor.getValue(), false);
    }
}
