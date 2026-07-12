package com.lux.gui;

import com.lux.config.HUDConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ArmorStatusSettingsScreen extends Screen {

    private final Screen parent;
    private final HUDConfig.ModuleData armorMod;

    public ArmorStatusSettingsScreen(Screen parent) {
        super(Text.literal("Armor Status Settings"));
        this.parent = parent;
        this.armorMod = HUDConfig.getInstance().getModule("Armor Status");
    }

    @Override
    protected void init() {
        boolean showPercent = armorMod != null && armorMod.showPercentage;

        this.addDrawableChild(net.minecraft.client.gui.widget.ButtonWidget.builder(
                Text.literal("Exact (320/363)"), button -> {
                    if (armorMod != null) {
                        armorMod.showPercentage = false;
                        HUDConfig.getInstance().save();
                        this.init();
                    }
                }).dimensions(this.width / 2 - 115, this.height / 2 - 10, 110, 20).build());

        this.addDrawableChild(net.minecraft.client.gui.widget.ButtonWidget.builder(
                Text.literal("Percent (88%)"), button -> {
                    if (armorMod != null) {
                        armorMod.showPercentage = true;
                        HUDConfig.getInstance().save();
                        this.init();
                    }
                }).dimensions(this.width / 2 + 5, this.height / 2 - 10, 110, 20).build());

        this.addDrawableChild(net.minecraft.client.gui.widget.ButtonWidget.builder(
                Text.literal("Done"), button -> {
                    if (this.client != null) {
                        this.client.setScreen(this.parent);
                    }
                }).dimensions(this.width / 2 - 100, this.height / 2 + 30, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);

        context.drawCenteredTextWithShadow(this.textRenderer, "Durability Display:", this.width / 2, this.height / 2 - 30, 0xAAAAAA);

        if (armorMod != null) {
            String preview = armorMod.showPercentage ? "Preview: 87%" : "Preview: 317/363";
            int previewColor = armorMod.showPercentage ? 0x55FF55 : 0x55FF55;
            context.drawCenteredTextWithShadow(this.textRenderer, preview, this.width / 2, this.height / 2 + 60, previewColor);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
