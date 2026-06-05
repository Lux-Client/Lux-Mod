package com.lux.gui;

import com.lux.module.Module;
import com.lux.setting.*;
import com.lux.theme.Theme;
import com.lux.theme.ThemeManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ModuleSettingsScreen extends Screen {

    private static final int PANEL_W  = 320;
    private static final int ROW_H    = 28;
    private static final int PADDING  = 14;

    private final Module module;
    private final Screen parent;

    private int scrollOffset = 0, targetScroll = 0;
    private boolean listeningForKey = false;
    private float openAnim = 0f;

    /* slider drag state */
    private Setting<?> draggingSetting = null;
    private int dragStartX = 0;

    public ModuleSettingsScreen(Module module, Screen parent) {
        super(Text.empty());
        this.module = module;
        this.parent = parent;
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        Theme t = ThemeManager.getInstance().getActive();

        openAnim = Math.min(1f, openAnim + delta * 0.14f);
        float ease = 1f - (1f - openAnim) * (1f - openAnim);

        int panelH = Math.min((int)(height * 0.85f), PADDING * 3 + 44 + module.getSettings().size() * (ROW_H + 4));
        int px = (width - PANEL_W) / 2;
        int py = (int)((height - panelH) / 2 + (1f - ease) * 20);

        // Dim background
        ctx.fill(0, 0, width, height, (int)(0xAA000000 * ease));

        // Panel
        GuiUtil.fillRounded(ctx, px, py, px + PANEL_W, py + panelH, 6, t.card);
        GuiUtil.drawRoundedBorder(ctx, px, py, px + PANEL_W, py + panelH, 6, t.border);

        // Header
        ctx.fill(px, py, px + PANEL_W, py + 36, GuiUtil.darken(t.card, 0.3f));

        // Back arrow
        boolean onBack = mx >= px + 8 && mx <= px + 26 && my >= py + 8 && my <= py + 22;
        ctx.drawTextWithShadow(textRenderer, onBack ? "§b←" : "§7←", px + 10, py + 12, 0xFFFFFFFF);

        // Module name + category
        ctx.drawTextWithShadow(textRenderer, "§f" + module.getName(), px + 32, py + 8, t.text);
        String catLabel = module.getCategory().displayName;
        int chipW = textRenderer.getWidth(catLabel) + 8;
        ctx.fill(px + 32, py + 18, px + 32 + chipW, py + 28, GuiUtil.alpha(module.getCategory().color, 0.3f));
        ctx.drawText(textRenderer, catLabel, px + 36, py + 19, module.getCategory().color, false);

        // Enabled toggle (big)
        int toggleX = px + PANEL_W - 58, toggleY = py + 10;
        boolean en = module.isEnabled();
        int toggleColor = en ? t.enabled : t.disabled;
        GuiUtil.fillRounded(ctx, toggleX, toggleY, toggleX + 48, toggleY + 16, 4, GuiUtil.alpha(toggleColor, 0.2f));
        GuiUtil.drawRoundedBorder(ctx, toggleX, toggleY, toggleX + 48, toggleY + 16, 4, toggleColor);
        ctx.drawCenteredTextWithShadow(textRenderer, en ? "§aON" : "§cOFF", toggleX + 24, toggleY + 4, 0xFFFFFFFF);

        // Divider
        ctx.fill(px + PADDING, py + 36, px + PANEL_W - PADDING, py + 37, t.border);

        // Settings list
        int contentY = py + 44;
        scrollOffset += (targetScroll - scrollOffset) * 0.2f;
        ctx.enableScissor(px, contentY, px + PANEL_W, py + panelH - 36);

        List<Setting<?>> settings = module.getSettings();
        int ry = contentY - scrollOffset;
        for (int i = 0; i < settings.size(); i++) {
            Setting<?> s = settings.get(i);
            renderSetting(ctx, s, px + PADDING, ry, PANEL_W - PADDING * 2, mx, my, t);
            ry += ROW_H + 4;
        }

        ctx.disableScissor();

        // Keybind row
        int keyY = py + panelH - 30;
        ctx.fill(px, keyY - 1, px + PANEL_W, keyY, t.border);
        String keyLabel = listeningForKey ? "§ePress a key..." : "Key: " + keyName(module.getKeyCode());
        int keyBtnColor = listeningForKey ? GuiUtil.alpha(t.accent, 0.3f) : GuiUtil.alpha(t.card, 0.5f);
        ctx.fill(px + PADDING, keyY + 4, px + PANEL_W - PADDING, keyY + 22, keyBtnColor);
        ctx.drawCenteredTextWithShadow(textRenderer, keyLabel, px + PANEL_W / 2, keyY + 9, listeningForKey ? t.accent : t.textSecondary);

        super.render(ctx, mx, my, delta);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void renderSetting(DrawContext ctx, Setting<?> s, int x, int y, int w, int mx, int my, Theme t) {
        ctx.drawTextWithShadow(textRenderer, s.getName(), x, y + 2, t.text);

        if (s instanceof BooleanSetting bs) {
            boolean val = bs.getValue();
            int bx = x + w - 42, by2 = y;
            int bc = val ? t.enabled : t.disabled;
            GuiUtil.fillRounded(ctx, bx, by2, bx + 36, by2 + ROW_H - 6, 4, GuiUtil.alpha(bc, 0.2f));
            GuiUtil.drawRoundedBorder(ctx, bx, by2, bx + 36, by2 + ROW_H - 6, 4, bc);
            ctx.drawCenteredTextWithShadow(textRenderer, val ? "§aON" : "§cOFF", bx + 18, by2 + (ROW_H - 6) / 2 - 4, 0xFFFFFFFF);

        } else if (s instanceof IntSetting is) {
            renderSlider(ctx, x, y, w, is.getPercent(), s.getDisplayValue(), mx, my, t);

        } else if (s instanceof FloatSetting fs) {
            renderSlider(ctx, x, y, w, fs.getPercent(), s.getDisplayValue(), mx, my, t);

        } else if (s instanceof ColorSetting cs) {
            int colorVal = cs.getValue();
            int px2 = x + w - 28;
            ctx.fill(px2, y + 2, px2 + 22, y + ROW_H - 4, 0xFF333333);
            ctx.fill(px2 + 1, y + 3, px2 + 21, y + ROW_H - 5, colorVal);
            ctx.drawText(textRenderer, cs.getDisplayValue(), px2 - textRenderer.getWidth(cs.getDisplayValue()) - 4, y + 2, t.textSecondary, false);

        } else if (s instanceof EnumSetting es) {
            int bx = x + w - 80, by2 = y;
            boolean hover = mx >= bx && mx <= bx + 74 && my >= by2 && my <= by2 + ROW_H - 6;
            int bc = hover ? t.accent : t.border;
            GuiUtil.fillRounded(ctx, bx, by2, bx + 74, by2 + ROW_H - 6, 3, GuiUtil.alpha(bc, 0.15f));
            GuiUtil.drawRoundedBorder(ctx, bx, by2, bx + 74, by2 + ROW_H - 6, 3, bc);
            ctx.drawCenteredTextWithShadow(textRenderer, "◀ " + es.getDisplayValue() + " ▶", bx + 37, by2 + (ROW_H - 6) / 2 - 4, t.text);
        }
    }

    private void renderSlider(DrawContext ctx, int x, int y, int w, float pct, String val, int mx, int my, Theme t) {
        int sx = x + w / 2, sw = w / 2 - 4, sy = y + (ROW_H - 6) / 2;
        ctx.fill(sx, sy, sx + sw, sy + 4, t.border);
        int filled = (int)(pct * sw);
        ctx.fill(sx, sy, sx + filled, sy + 4, t.accent);
        int knobX = sx + filled - 3;
        ctx.fill(knobX, sy - 3, knobX + 6, sy + 7, t.text);
        ctx.drawText(textRenderer, val, sx + sw + 4, y + 2, t.textSecondary, false);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        Theme t = ThemeManager.getInstance().getActive();
        int panelH = Math.min((int)(height * 0.85f), PADDING * 3 + 44 + module.getSettings().size() * (ROW_H + 4));
        int px = (width - PANEL_W) / 2, py = (height - panelH) / 2;

        // Back
        if (mx >= px + 8 && mx <= px + 26 && my >= py + 8 && my <= py + 22) {
            client.setScreen(parent); return true;
        }

        // Enable toggle
        int toggleX = px + PANEL_W - 58, toggleY = py + 10;
        if (mx >= toggleX && mx <= toggleX + 48 && my >= toggleY && my <= toggleY + 16) {
            module.toggle(); return true;
        }

        // Keybind
        int keyY = py + panelH - 30;
        if (mx >= px + PADDING && mx <= px + PANEL_W - PADDING && my >= keyY + 4 && my <= keyY + 22) {
            listeningForKey = true; return true;
        }

        // Settings
        List<Setting<?>> settings = module.getSettings();
        int contentY = py + 44;
        int ry = contentY - scrollOffset;
        for (Setting<?> s : settings) {
            int rowBottom = ry + ROW_H;
            if (my >= ry && my <= rowBottom) {
                int sx = px + PADDING, sw = PANEL_W - PADDING * 2;
                handleSettingClick(s, sx, ry, sw, (int)mx, (int)my, btn);
            }
            ry += ROW_H + 4;
        }
        return super.mouseClicked(mx, my, btn);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void handleSettingClick(Setting<?> s, int x, int y, int w, int mx, int my, int btn) {
        if (s instanceof BooleanSetting bs) {
            int bx = x + w - 42;
            if (mx >= bx && mx <= bx + 36) bs.toggle();
        } else if (s instanceof EnumSetting es) {
            int bx = x + w - 80;
            if (mx >= bx && mx <= bx + 74) {
                if (btn == 0) es.cycle();
                else { /* reverse cycle */ }
            }
        } else if (s instanceof IntSetting || s instanceof FloatSetting) {
            draggingSetting = s;
            dragStartX = mx;
        } else if (s instanceof ColorSetting cs) {
            int nextColor = switch ((cs.getValue() & 0x00FFFFFF)) {
                case 0xFFFFFF -> 0xFFFF5555;
                case 0xFF5555 -> 0xFF55FF55;
                case 0x55FF55 -> 0xFF5555FF;
                case 0x5555FF -> 0xFFFFAA00;
                default       -> 0xFFFFFFFF;
            };
            cs.setValue((cs.getValue() & 0xFF000000) | nextColor);
        }
    }

    @Override
    public boolean mouseDragged(double mx, double my, int btn, double dx, double dy) {
        if (draggingSetting instanceof IntSetting is) {
            int range = is.getMax() - is.getMin();
            int sliderW = (PANEL_W - PADDING * 2) / 2 - 4;
            int delta = (int)(dx * range / sliderW);
            is.setValue(is.getValue() + delta);
        } else if (draggingSetting instanceof FloatSetting fs) {
            float range = fs.getMax() - fs.getMin();
            int sliderW = (PANEL_W - PADDING * 2) / 2 - 4;
            float delta = (float)(dx * range / sliderW);
            fs.setValue(fs.getValue() + delta);
        }
        return super.mouseDragged(mx, my, btn, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int btn) {
        draggingSetting = null;
        return super.mouseReleased(mx, my, btn);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double hAmount, double vAmount) {
        targetScroll = Math.max(0, targetScroll - (int)(vAmount * (ROW_H + 4)));
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (listeningForKey) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) { module.setKeyCode(-1); }
            else { module.setKeyCode(keyCode); }
            listeningForKey = false;
            com.lux.config.ConfigManager.getInstance().save();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) { client.setScreen(parent); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() { return false; }

    private static String keyName(int code) {
        if (code == -1) return "None";
        return GLFW.glfwGetKeyName(code, 0) != null ? GLFW.glfwGetKeyName(code, 0).toUpperCase() : "Key " + code;
    }
}
