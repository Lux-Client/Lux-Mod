package com.lux.gui;

import net.minecraft.client.gui.DrawContext;

public final class GuiUtil {

    private GuiUtil() {}

    public static void fillRounded(DrawContext ctx, int x1, int y1, int x2, int y2, int r, int color) {
        ctx.fill(x1 + r, y1, x2 - r, y2, color);
        ctx.fill(x1, y1 + r, x1 + r, y2 - r, color);
        ctx.fill(x2 - r, y1 + r, x2, y2 - r, color);
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < r; j++) {
                double dist = Math.sqrt((r - i - 0.5) * (r - i - 0.5) + (r - j - 0.5) * (r - j - 0.5));
                if (dist <= r) {
                    ctx.fill(x1 + i, y1 + j, x1 + i + 1, y1 + j + 1, color);
                    ctx.fill(x2 - i - 1, y1 + j, x2 - i, y1 + j + 1, color);
                    ctx.fill(x1 + i, y2 - j - 1, x1 + i + 1, y2 - j, color);
                    ctx.fill(x2 - i - 1, y2 - j - 1, x2 - i, y2 - j, color);
                }
            }
        }
    }

    public static void drawRoundedBorder(DrawContext ctx, int x1, int y1, int x2, int y2, int r, int color) {
        ctx.fill(x1 + r, y1, x2 - r, y1 + 1, color);
        ctx.fill(x1 + r, y2 - 1, x2 - r, y2, color);
        ctx.fill(x1, y1 + r, x1 + 1, y2 - r, color);
        ctx.fill(x2 - 1, y1 + r, x2, y2 - r, color);
    }

    public static int lerpColor(int a, int b, float t) {
        int aa = (a >> 24) & 0xFF, ra = (a >> 16) & 0xFF, ga = (a >> 8) & 0xFF, ba2 = a & 0xFF;
        int ab = (b >> 24) & 0xFF, rb = (b >> 16) & 0xFF, gb = (b >> 8) & 0xFF, bb2 = b & 0xFF;
        return (lerp(aa, ab, t) << 24) | (lerp(ra, rb, t) << 16) | (lerp(ga, gb, t) << 8) | lerp(ba2, bb2, t);
    }

    private static int lerp(int a, int b, float t) { return (int)(a + (b - a) * t); }

    public static int alpha(int color, float a) {
        return (color & 0x00FFFFFF) | ((int)(a * 255) << 24);
    }

    public static int darken(int color, float amount) {
        int r = Math.max(0, (int)(((color >> 16) & 0xFF) * (1 - amount)));
        int g = Math.max(0, (int)(((color >> 8)  & 0xFF) * (1 - amount)));
        int b = Math.max(0, (int)((color & 0xFF)          * (1 - amount)));
        return (color & 0xFF000000) | (r << 16) | (g << 8) | b;
    }

    public static int brighten(int color, float amount) {
        int r = Math.min(255, (int)(((color >> 16) & 0xFF) + 255 * amount));
        int g = Math.min(255, (int)(((color >> 8)  & 0xFF) + 255 * amount));
        int b = Math.min(255, (int)((color & 0xFF)          + 255 * amount));
        return (color & 0xFF000000) | (r << 16) | (g << 8) | b;
    }

    public static void drawVerticalGradient(DrawContext ctx, int x1, int y1, int x2, int y2, int top, int bottom) {
        int steps = y2 - y1;
        for (int i = 0; i < steps; i++) {
            float t = (float) i / steps;
            ctx.fill(x1, y1 + i, x2, y1 + i + 1, lerpColor(top, bottom, t));
        }
    }
}
