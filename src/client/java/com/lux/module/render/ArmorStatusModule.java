package com.lux.module.render;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.BooleanSetting;
import com.lux.setting.EnumSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

public class ArmorStatusModule extends Module {
    public enum Format { PERCENT, EXACT }
    private EnumSetting<Format> format;
    private BooleanSetting showUnbreakable;

    public ArmorStatusModule() { super("Armor Status", "Shows armor durability", Category.RENDER); }

    @Override protected void init() {
        format          = register(new EnumSetting<>("Format", "Durability format", Format.PERCENT));
        showUnbreakable = register(new BooleanSetting("Unbreakable", "Show unbreakable items", true));
    }

    @Override public void onRenderHUD(DrawContext ctx, float delta) {
        ClientPlayerEntity p = mc.player;
        if (p == null) return;
        int x = getHudX(), y = getHudY(), spacing = 18;
        ItemStack[] armor = {
            p.getInventory().getArmorStack(3),
            p.getInventory().getArmorStack(2),
            p.getInventory().getArmorStack(1),
            p.getInventory().getArmorStack(0)
        };
        for (ItemStack stack : armor) {
            if (!stack.isEmpty()) {
                ctx.drawItem(stack, x, y);
                ctx.drawItemInSlot(mc.textRenderer, stack, x, y);
                if (stack.isDamageable()) {
                    int max = stack.getMaxDamage(), cur = max - stack.getDamage();
                    float pct = (float) cur / max;
                    int color = pct > 0.7f ? 0xFF55FF55 : pct > 0.3f ? 0xFFFFFF55 : 0xFFFF5555;
                    String d = format.getValue() == Format.PERCENT ? Math.round(pct * 100) + "%" : cur + "/" + max;
                    ctx.drawTextWithShadow(mc.textRenderer, d, x + 18, y + 4, color);
                } else if (showUnbreakable.getValue()) {
                    ctx.drawTextWithShadow(mc.textRenderer, "∞", x + 18, y + 4, 0xFFFFFFFF);
                }
            }
            y += spacing;
        }
    }
}
