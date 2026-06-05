package com.lux.module.qol;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.BooleanSetting;
import com.lux.setting.ColorSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

public class ItemCounterModule extends Module {
    private BooleanSetting shadow;
    private ColorSetting textColor;

    public ItemCounterModule() { super("Item Counter", "Shows count of items in your inventory", Category.QUALITY_OF_LIFE); }

    @Override protected void init() {
        shadow    = register(new BooleanSetting("Shadow", "Draw text shadow", true));
        textColor = register(new ColorSetting("Color", "Text color", 0xFFFFFFFF));
    }

    @Override public void onRenderHUD(DrawContext ctx, float delta) {
        ClientPlayerEntity p = mc.player;
        if (p == null) return;
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (int i = 0; i < p.getInventory().size(); i++) {
            ItemStack s = p.getInventory().getStack(i);
            if (s.isEmpty()) continue;
            counts.merge(s.getName().getString(), s.getCount(), Integer::sum);
        }
        int x = getHudX(), y = getHudY();
        for (Map.Entry<String, Integer> e : counts.entrySet()) {
            String line = e.getKey() + ": " + e.getValue();
            if (shadow.getValue()) ctx.drawTextWithShadow(mc.textRenderer, line, x, y, textColor.getValue());
            else ctx.drawText(mc.textRenderer, line, x, y, textColor.getValue(), false);
            y += 10;
        }
    }
}
