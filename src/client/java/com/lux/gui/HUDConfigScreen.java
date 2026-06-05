package com.lux.gui;

import com.lux.config.ConfigManager;
import com.lux.module.Module;
import com.lux.module.ModuleManager;
import com.lux.theme.Theme;
import com.lux.theme.ThemeManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public class HUDConfigScreen extends Screen {

    private Module dragging = null;
    private int dragOffX, dragOffY;
    private static final int SNAP = 2;

    public HUDConfigScreen() { super(Text.empty()); }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        Theme t = ThemeManager.getInstance().getActive();

        // Translucent overlay
        ctx.fill(0, 0, width, height, 0x88000000);

        // Grid dots (visual helper)
        for (int x = 0; x < width; x += 20)
            for (int y = 0; y < height; y += 20)
                ctx.fill(x, y, x + 1, y + 1, 0x33FFFFFF);

        // Render all modules' HUD + highlight boxes
        List<Module> mods = ModuleManager.getInstance().getAll();
        for (Module m : mods) {
            if (!m.isEnabled()) continue;
            m.onRenderHUD(ctx, delta);

            // Bounding highlight
            int bx = m.getHudX() - 2, by = m.getHudY() - 2;
            int bw = 80, bh = 14;
            boolean selected = m == dragging;
            boolean hover = mx >= bx && mx <= bx + bw && my >= by && my <= by + bh;
            int borderColor = selected ? t.accent : hover ? GuiUtil.alpha(t.accent, 0.6f) : GuiUtil.alpha(t.border, 0.5f);
            if (hover || selected) drawDashedRect(ctx, bx, by, bw, bh, borderColor);
        }

        // Bottom bar
        int barY = height - 26;
        ctx.fill(0, barY, width, height, 0xCC000000);
        ctx.fill(0, barY, width, barY + 1, t.border);
        ctx.drawCenteredTextWithShadow(textRenderer,
                "§7Drag to reposition  §b[Modules]§7 to open settings  §bESC§7 to save & close",
                width / 2, barY + 7, 0xFFCCCCCC);

        // Modules button
        int btnW = 80, btnH = 18;
        int btnX = (width - btnW) / 2 - 50, btnY = barY + 4;
        GuiUtil.fillRounded(ctx, btnX, btnY, btnX + btnW, btnY + btnH, 3, GuiUtil.alpha(t.accent, 0.2f));
        GuiUtil.drawRoundedBorder(ctx, btnX, btnY, btnX + btnW, btnY + btnH, 3, t.accent);
        ctx.drawCenteredTextWithShadow(textRenderer, "§bModules", btnX + btnW / 2, btnY + 5, t.text);

        super.render(ctx, mx, my, delta);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        Theme t = ThemeManager.getInstance().getActive();
        int barY = height - 26;

        // Modules button
        int btnW = 80, btnX = (width - btnW) / 2 - 50, btnY = barY + 4;
        if (mx >= btnX && mx <= btnX + btnW && my >= btnY && my <= btnY + 18) {
            client.setScreen(new ClickGuiScreen());
            return true;
        }

        for (Module m : ModuleManager.getInstance().getAll()) {
            if (!m.isEnabled()) continue;
            int bx = m.getHudX() - 2, by = m.getHudY() - 2;
            if (mx >= bx && mx <= bx + 80 && my >= by && my <= by + 14) {
                if (btn == 0) {
                    dragging = m;
                    dragOffX = (int)(mx - m.getHudX());
                    dragOffY = (int)(my - m.getHudY());
                } else if (btn == 1) {
                    client.setScreen(new ModuleSettingsScreen(m, this));
                }
                return true;
            }
        }
        return super.mouseClicked(mx, my, btn);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int btn, double dx, double dy) {
        if (dragging != null && btn == 0) {
            int nx = snap((int)(mx - dragOffX));
            int ny = snap((int)(my - dragOffY));
            nx = Math.max(0, Math.min(width - 80, nx));
            ny = Math.max(0, Math.min(height - 20, ny));
            dragging.setHudPos(nx, ny);
        }
        return super.mouseDragged(mx, my, btn, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int btn) {
        if (btn == 0) dragging = null;
        return super.mouseReleased(mx, my, btn);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            ConfigManager.getInstance().save();
            client.setScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() { return false; }

    private int snap(int v) { return (v / SNAP) * SNAP; }

    private void drawDashedRect(DrawContext ctx, int x, int y, int w, int h, int color) {
        int dash = 4;
        for (int i = x; i < x + w; i += dash * 2) { ctx.fill(i, y, Math.min(i + dash, x + w), y + 1, color); ctx.fill(i, y + h, Math.min(i + dash, x + w), y + h + 1, color); }
        for (int i = y; i < y + h; i += dash * 2) { ctx.fill(x, i, x + 1, Math.min(i + dash, y + h), color); ctx.fill(x + w, i, x + w + 1, Math.min(i + dash, y + h), color); }
    }
}
