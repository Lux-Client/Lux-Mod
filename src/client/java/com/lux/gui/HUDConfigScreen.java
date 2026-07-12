package com.lux.gui;

import com.lux.config.HUDConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import java.util.Map;

public class HUDConfigScreen extends Screen {

    private final HUDConfig config = HUDConfig.getInstance();
    private HUDConfig.ModuleData draggingModule = null;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    public HUDConfigScreen() {
        super(Text.literal("HUD Layout Editor"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        context.fill(0, 0, this.width, this.height, 0x88000000);

        context.drawCenteredTextWithShadow(this.textRenderer, "Drag to reposition modules. Press ESC to close.", this.width / 2, 10, 0xAAAAAA);

        for (Map.Entry<String, HUDConfig.ModuleData> entry : config.getModules().entrySet()) {
            HUDConfig.ModuleData mod = entry.getValue();
            if (!mod.enabled) continue;

            int modWidth;
            int modHeight;

            if (mod.name.equals("Armor Status")) {
                modWidth = 60;
                modHeight = 18 * 4;
            } else if (mod.name.equals("CPS") || mod.name.equals("Ping") || mod.name.equals("Potion Effects")) {
                String displayText = "[" + mod.name + " (Mode: " + mod.mode + ")]";
                modWidth = this.textRenderer.getWidth(displayText) + 8;
                modHeight = 14;
            } else {
                String displayText = "[" + mod.name + "]";
                modWidth = this.textRenderer.getWidth(displayText) + 8;
                modHeight = 14;
            }

            if (draggingModule == mod) {
                context.fill(mod.x - 1, mod.y - 1, mod.x + modWidth + 1, mod.y + modHeight + 1, 0x8800FF00);
            }

            context.fill(mod.x, mod.y, mod.x + modWidth, mod.y + modHeight, 0xAA000000);

            if (mod.name.equals("Armor Status")) {
                ItemStack[] armorArray = new ItemStack[4];
                boolean hasAnyArmor = false;
                if (this.client != null && this.client.player != null) {
                    int idx = 3;
                    for (ItemStack stack : this.client.player.getArmorItems()) {
                        armorArray[idx--] = stack;
                        if (stack != null && !stack.isEmpty()) hasAnyArmor = true;
                    }
                }
                if (!hasAnyArmor) {
                    armorArray[3] = new ItemStack(Items.DIAMOND_HELMET);
                    armorArray[2] = new ItemStack(Items.DIAMOND_CHESTPLATE);
                    armorArray[1] = new ItemStack(Items.DIAMOND_LEGGINGS);
                    armorArray[0] = new ItemStack(Items.DIAMOND_BOOTS);
                }
                int currentY = mod.y;
                for (int i = 3; i >= 0; i--) {
                    ItemStack stack = armorArray[i];
                    if (stack != null && !stack.isEmpty()) {
                        context.drawItem(stack, mod.x + 2, currentY + 1);
                        context.drawTextWithShadow(this.textRenderer, "100%", mod.x + 22, currentY + 5, 0xFFFFFF);
                        currentY += 18;
                    }
                }
            } else if (mod.name.equals("CPS") || mod.name.equals("Ping") || mod.name.equals("Potion Effects")) {
                String displayText = "[" + mod.name + " (Mode: " + mod.mode + ")]";
                context.drawTextWithShadow(this.textRenderer, displayText, mod.x + 4, mod.y + 3, 0xFFFFFF);
            } else {
                String displayText = "[" + mod.name + "]";
                context.drawTextWithShadow(this.textRenderer, displayText, mod.x + 4, mod.y + 3, 0xFFFFFF);
            }
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (Map.Entry<String, HUDConfig.ModuleData> entry : config.getModules().entrySet()) {
                HUDConfig.ModuleData mod = entry.getValue();
                if (!mod.enabled) continue;

                int modWidth;
                int modHeight;
                if (mod.name.equals("Armor Status")) {
                    modWidth = 60;
                    modHeight = 18 * 4;
                } else {
                    modWidth = this.textRenderer.getWidth("[" + mod.name + "]") + 8;
                    modHeight = 14;
                }

                if (mouseX >= mod.x && mouseX <= mod.x + modWidth && mouseY >= mod.y && mouseY <= mod.y + modHeight) {
                    this.draggingModule = mod;
                    this.dragOffsetX = (int) (mouseX - mod.x);
                    this.dragOffsetY = (int) (mouseY - mod.y);
                    return true;
                }
            }
        } else if (button == 1) {
            for (Map.Entry<String, HUDConfig.ModuleData> entry : config.getModules().entrySet()) {
                HUDConfig.ModuleData mod = entry.getValue();
                if (!mod.enabled) continue;

                int modWidth;
                int modHeight;
                if (mod.name.equals("Armor Status")) {
                    modWidth = 60;
                    modHeight = 18 * 4;
                } else {
                    modWidth = this.textRenderer.getWidth("[" + mod.name + "]") + 8;
                    modHeight = 14;
                }

                if (mouseX >= mod.x && mouseX <= mod.x + modWidth && mouseY >= mod.y && mouseY <= mod.y + modHeight) {
                    if (mod.name.equals("CPS") || mod.name.equals("Ping") || mod.name.equals("Potion Effects")) {
                        mod.mode = (mod.mode + 1) % 2;
                        config.save();
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.draggingModule != null && button == 0) {
            int newX = (int) mouseX - dragOffsetX;
            int newY = (int) mouseY - dragOffsetY;
            newX = Math.max(0, Math.min(newX, this.width - 60));
            newY = Math.max(0, Math.min(newY, this.height - 18 * 4));
            this.draggingModule.x = newX;
            this.draggingModule.y = newY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.draggingModule != null && button == 0) {
            this.draggingModule = null;
            config.save();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
