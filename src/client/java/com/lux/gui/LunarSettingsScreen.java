package com.lux.gui;

import com.lux.config.HUDConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LunarSettingsScreen extends Screen {

    private final List<ModuleEntry> modules = new ArrayList<>();
    private double targetScrollOffset = 0;
    private double currentScrollOffset = 0;
    private int contentHeight = 0;

    public LunarSettingsScreen() {
        super(Text.literal("Lux - Mods"));
        HUDConfig config = HUDConfig.getInstance();
        for (Map.Entry<String, HUDConfig.ModuleData> entry : config.getModules().entrySet()) {
            modules.add(new ModuleEntry(entry.getValue()));
        }
    }

    @Override
    protected void init() {
        this.addDrawableChild(net.minecraft.client.gui.widget.ButtonWidget.builder(
                Text.literal("Done"), button -> {
                    if (this.client != null) this.client.setScreen(null);
                }).dimensions(this.width / 2 - 100, this.height - 28, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);

        int listTop = 32;
        int listBottom = this.height - 40;
        int entryHeight = 26;
        int listWidth = Math.min(340, this.width - 50);
        int listX = (this.width - listWidth) / 2;

        this.contentHeight = modules.size() * entryHeight;
        int maxScroll = Math.max(0, this.contentHeight - (listBottom - listTop));
        this.targetScrollOffset = Math.max(0, Math.min(maxScroll, this.targetScrollOffset));
        this.currentScrollOffset += (this.targetScrollOffset - this.currentScrollOffset) * 0.3;
        if (Math.abs(this.currentScrollOffset - this.targetScrollOffset) < 0.5) {
            this.currentScrollOffset = this.targetScrollOffset;
        }

        int scrollY = (int) this.currentScrollOffset;
        context.enableScissor(listX, listTop, listX + listWidth, listBottom);

        for (int i = 0; i < modules.size(); i++) {
            ModuleEntry mod = modules.get(i);
            int entryY = listTop + i * entryHeight - scrollY;

            if (entryY + entryHeight < listTop || entryY > listBottom) continue;

            context.fill(listX, entryY, listX + listWidth, entryY + entryHeight - 2, 0x88000000);

            context.drawTextWithShadow(this.textRenderer, mod.data.name, listX + 8, entryY + 9, 0xFFFFFF);

            String stateText = mod.data.enabled ? "ON" : "OFF";
            int stateColor = mod.data.enabled ? 0x55FF55 : 0xFF5555;
            int stateTextWidth = this.textRenderer.getWidth(stateText);
            int toggleX = listX + listWidth - 50;
            context.fill(toggleX, entryY + 4, toggleX + 40, entryY + 20, mod.data.enabled ? 0xFF337733 : 0xFF773333);
            context.drawTextWithShadow(this.textRenderer, stateText, toggleX + (40 - stateTextWidth) / 2, entryY + 9, stateColor);
        }

        context.disableScissor();

        if (maxScroll > 0) {
            int scrollbarX = listX + listWidth + 4;
            int scrollbarHeight = listBottom - listTop;
            context.fill(scrollbarX, listTop, scrollbarX + 2, listBottom, 0x44FFFFFF);
            int handleHeight = Math.max(20, (int) ((float) (listBottom - listTop) * (listBottom - listTop) / this.contentHeight));
            int handleY = listTop + (int) ((scrollbarHeight - handleHeight) * ((float) this.currentScrollOffset / maxScroll));
            context.fill(scrollbarX, handleY, scrollbarX + 2, handleY + handleHeight, 0xAAFFFFFF);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        this.targetScrollOffset -= verticalAmount * 24;
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        int listTop = 32;
        int listBottom = this.height - 40;
        int entryHeight = 26;
        int listWidth = Math.min(340, this.width - 50);
        int listX = (this.width - listWidth) / 2;
        int scrollY = (int) this.currentScrollOffset;

        for (int i = 0; i < modules.size(); i++) {
            ModuleEntry mod = modules.get(i);
            int entryY = listTop + i * entryHeight - scrollY;

            if (mouseY >= entryY && mouseY <= entryY + entryHeight - 2 && mouseX >= listX && mouseX <= listX + listWidth) {
                int toggleX = listX + listWidth - 50;
                if (mouseX >= toggleX && mouseX <= toggleX + 40) {
                    mod.data.enabled = !mod.data.enabled;
                    HUDConfig.getInstance().save();
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        if (this.client != null) this.client.setScreen(null);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private static class ModuleEntry {
        final HUDConfig.ModuleData data;

        ModuleEntry(HUDConfig.ModuleData data) {
            this.data = data;
        }
    }
}
