package com.lux.gui;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.module.ModuleManager;
import com.lux.theme.Theme;
import com.lux.theme.ThemeManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public class ClickGuiScreen extends Screen {

    /* ── layout constants ──────────────────────────────────────────── */
    private static final int SIDEBAR_W = 100;
    private static final int CARD_W    = 118;
    private static final int CARD_H    = 64;
    private static final int CARD_GAP  = 6;
    private static final int HEADER_H  = 36;
    private static final int PADDING   = 12;

    private Category activeCategory = Category.RENDER;
    private int scrollOffset = 0;
    private int targetScroll = 0;

    private final float[] hoverAnim = new float[64];
    private float openAnim = 0f;

    public ClickGuiScreen() { super(Text.empty()); }

    /* ── panel size helpers ────────────────────────────────────────── */
    private int getPanelW(int screenW, float ease) {
        int maxW = screenW - 20;
        int natural = SIDEBAR_W + 520;
        return (int)(Math.min(maxW, natural) * Math.min(1f, ease * 1.2f));
    }

    private int getPanelH(int screenH, float ease) {
        return (int)(screenH * 0.82f * ease);
    }

    /* ── no blur, no vanilla background ───────────────────────────── */
    @Override
    public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // We draw our own background; skip vanilla blur + darkening.
    }

    @Override
    protected void applyBlur(float delta) {
        // Intentionally empty — no background blur for this GUI.
    }

    /* ── rendering ─────────────────────────────────────────────────── */
    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        Theme t = ThemeManager.getInstance().getActive();
        int w = width, h = height;

        openAnim = Math.min(1f, openAnim + delta * 0.12f);
        float ease = 1f - (1f - openAnim) * (1f - openAnim);

        int panelH = getPanelH(h, ease);
        int panelW = getPanelW(w, ease);
        int px = (w - panelW) / 2;
        int py = (h - panelH) / 2;

        // Full-screen dark dim (replaces vanilla background)
        ctx.fill(0, 0, w, h, 0xAA000000);

        // Panel shadow
        drawShadow(ctx, px - 6, py - 6, panelW + 12, panelH + 12, 0x44000000);

        // Background + sidebar
        ctx.fill(px, py, px + panelW, py + panelH, t.background);
        ctx.fill(px, py, px + SIDEBAR_W, py + panelH, t.card);

        // Header strip
        ctx.fill(px, py, px + panelW, py + HEADER_H, GuiUtil.darken(t.background, 0.4f));

        // Title
        ctx.drawTextWithShadow(textRenderer, "§bLux §7Client", px + PADDING, py + 10, t.text);

        // Close button
        int cbx = px + panelW - 18, cby = py + 9;
        boolean onClose = mx >= cbx && mx <= cbx + 13 && my >= cby && my <= cby + 13;
        ctx.fill(cbx, cby, cbx + 13, cby + 13, onClose ? 0xFFE25C5C : 0x44E25C5C);
        ctx.drawCenteredTextWithShadow(textRenderer, "✕", cbx + 6, cby + 2, t.text);

        // Category tabs
        int tabY = py + HEADER_H + 6;
        for (Category cat : Category.values()) {
            boolean sel = cat == activeCategory;
            boolean hover = mx >= px && mx <= px + SIDEBAR_W && my >= tabY && my <= tabY + 22;
            int tabBg = sel ? GuiUtil.alpha(t.accent, 0.18f) : (hover ? GuiUtil.alpha(t.accent, 0.08f) : 0);
            if (tabBg != 0) ctx.fill(px, tabY, px + SIDEBAR_W, tabY + 22, tabBg);
            if (sel) ctx.fill(px, tabY, px + 3, tabY + 22, t.accent);
            ctx.drawTextWithShadow(textRenderer, cat.displayName, px + 11, tabY + 7,
                    sel ? t.text : t.textSecondary);
            tabY += 25;
        }

        // Module cards
        List<Module> mods = ModuleManager.getInstance().getByCategory(activeCategory);
        scrollOffset += (targetScroll - scrollOffset) * 0.2f;

        int cols   = Math.max(1, (panelW - SIDEBAR_W - PADDING * 2) / (CARD_W + CARD_GAP));
        int startX = px + SIDEBAR_W + PADDING;
        int startY = py + HEADER_H + PADDING - scrollOffset;

        ctx.enableScissor(px + SIDEBAR_W, py + HEADER_H, px + panelW, py + panelH);

        for (int i = 0; i < mods.size(); i++) {
            Module m  = mods.get(i);
            int col   = i % cols;
            int row   = i / cols;
            int cx    = startX + col * (CARD_W + CARD_GAP);
            int cy    = startY + row * (CARD_H + CARD_GAP);

            boolean hover = mx >= cx && mx <= cx + CARD_W && my >= cy && my <= cy + CARD_H;
            hoverAnim[i] = Math.max(0, Math.min(1, hoverAnim[i] + (hover ? delta * 0.15f : -delta * 0.15f)));
            float ha = hoverAnim[i];

            int cardBg = GuiUtil.lerpColor(t.card, GuiUtil.brighten(t.card, 0.07f), ha);
            int border = GuiUtil.lerpColor(t.border, t.accent, ha * 0.55f);

            GuiUtil.fillRounded(ctx, cx, cy, cx + CARD_W, cy + CARD_H, 4, cardBg);
            GuiUtil.drawRoundedBorder(ctx, cx, cy, cx + CARD_W, cy + CARD_H, 4, border);

            ctx.drawTextWithShadow(textRenderer, m.getName(), cx + 7, cy + 7, t.text);

            // Category chip
            int chipColor = GuiUtil.alpha(m.getCategory().color, 0.22f);
            int chipW = textRenderer.getWidth(m.getCategory().displayName) + 6;
            ctx.fill(cx + 7, cy + 18, cx + 7 + chipW, cy + 28, chipColor);
            ctx.drawText(textRenderer, m.getCategory().displayName, cx + 10, cy + 19,
                    m.getCategory().color, false);

            // Description
            String desc = m.getDescription();
            if (desc.length() > 20) desc = desc.substring(0, 18) + "…";
            ctx.drawText(textRenderer, desc, cx + 7, cy + 31, t.textSecondary, false);

            // Toggle
            boolean en = m.isEnabled();
            int btnX = cx + 7, btnY = cy + CARD_H - 17;
            int btnColor = en ? t.enabled : t.disabled;
            GuiUtil.fillRounded(ctx, btnX, btnY, btnX + 36, btnY + 11, 3, GuiUtil.alpha(btnColor, 0.22f));
            GuiUtil.drawRoundedBorder(ctx, btnX, btnY, btnX + 36, btnY + 11, 3, btnColor);
            ctx.drawCenteredTextWithShadow(textRenderer, en ? "§aON" : "§cOFF", btnX + 18, btnY + 1,
                    0xFFFFFFFF);

            // Gear
            int gearX = cx + CARD_W - 18, gearY = cy + CARD_H - 17;
            boolean onGear = mx >= gearX && mx <= gearX + 13 && my >= gearY && my <= gearY + 11;
            ctx.fill(gearX, gearY, gearX + 13, gearY + 11,
                    onGear ? GuiUtil.alpha(t.accent, 0.4f) : GuiUtil.alpha(t.border, 0.4f));
            ctx.drawCenteredTextWithShadow(textRenderer, "⚙", gearX + 6, gearY + 1,
                    onGear ? t.accent : t.textSecondary);
        }

        ctx.disableScissor();
        // Note: intentionally NOT calling super.render() to avoid vanilla blur + background
    }

    /* ── input ──────────────────────────────────────────────────────── */
    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        int w = width, h = height;
        int panelH = getPanelH(h, 1f);
        int panelW = getPanelW(w, 1f);
        int px = (w - panelW) / 2, py = (h - panelH) / 2;

        // Close
        int cbx = px + panelW - 18, cby = py + 9;
        if (mx >= cbx && mx <= cbx + 13 && my >= cby && my <= cby + 13) {
            client.setScreen(null); return true;
        }

        // Category tabs
        int tabY = py + HEADER_H + 6;
        for (Category cat : Category.values()) {
            if (mx >= px && mx <= px + SIDEBAR_W && my >= tabY && my <= tabY + 22) {
                activeCategory = cat;
                scrollOffset = 0; targetScroll = 0;
                return true;
            }
            tabY += 25;
        }

        // Module cards
        List<Module> mods = ModuleManager.getInstance().getByCategory(activeCategory);
        int cols   = Math.max(1, (panelW - SIDEBAR_W - PADDING * 2) / (CARD_W + CARD_GAP));
        int startX = px + SIDEBAR_W + PADDING;
        int startY = py + HEADER_H + PADDING - scrollOffset;

        for (int i = 0; i < mods.size(); i++) {
            Module m  = mods.get(i);
            int col   = i % cols, row = i / cols;
            int cx    = startX + col * (CARD_W + CARD_GAP);
            int cy    = startY + row * (CARD_H + CARD_GAP);

            if (mx < cx || mx > cx + CARD_W || my < cy || my > cy + CARD_H) continue;

            int gearX = cx + CARD_W - 18, gearY = cy + CARD_H - 17;
            if (mx >= gearX && mx <= gearX + 13 && my >= gearY && my <= gearY + 11) {
                client.setScreen(new ModuleSettingsScreen(m, this)); return true;
            }
            int btnX = cx + 7, btnY = cy + CARD_H - 17;
            if (mx >= btnX && mx <= btnX + 36 && my >= btnY && my <= btnY + 11) {
                m.toggle(); return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double hAmount, double vAmount) {
        int step = CARD_H + CARD_GAP;
        targetScroll = Math.max(0, targetScroll - (int)(vAmount * step));
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) { client.setScreen(null); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() { return false; }

    /* ── helpers ────────────────────────────────────────────────────── */
    private void drawShadow(DrawContext ctx, int x, int y, int w, int h, int color) {
        for (int i = 0; i < 6; i++) {
            int a = (int)((color >> 24 & 0xFF) * (1f - (float)i / 6));
            ctx.fill(x + i, y + i, x + w - i, y + h - i, (a << 24) | (color & 0x00FFFFFF));
        }
    }
}
