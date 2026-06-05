package com.lux.module.render;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.BooleanSetting;
import com.lux.setting.EnumSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;

import java.util.Collection;

public class PotionStatusModule extends Module {
    public enum Mode { COMPACT, DETAILED }
    private EnumSetting<Mode> mode;
    private BooleanSetting showAmbient;

    public PotionStatusModule() { super("Potion Status", "Shows active status effects", Category.RENDER); }

    @Override protected void init() {
        mode        = register(new EnumSetting<>("Mode", "Display mode", Mode.COMPACT));
        showAmbient = register(new BooleanSetting("Ambient", "Show ambient effects", false));
    }

    @Override public void onRenderHUD(DrawContext ctx, float delta) {
        ClientPlayerEntity p = mc.player;
        if (p == null) return;
        Collection<StatusEffectInstance> effects = p.getStatusEffects();
        if (effects.isEmpty()) return;
        int x = getHudX(), y = getHudY(), spacing = mode.getValue() == Mode.COMPACT ? 12 : 20;
        for (StatusEffectInstance eff : effects) {
            if (eff.isAmbient() && !showAmbient.getValue()) continue;
            RegistryEntry<StatusEffect> type = eff.getEffectType();
            String name = Text.translatable(type.value().getTranslationKey()).getString();
            if (eff.getAmplifier() > 0) name += " " + toRoman(eff.getAmplifier() + 1);
            String dur = eff.getDuration() == Integer.MAX_VALUE ? "∞" : StringHelper.formatTicks(eff.getDuration(), 20);
            int color = type.value().getColor() | 0xFF000000;
            String display = mode.getValue() == Mode.DETAILED ? name + " " + dur
                    : name.substring(0, Math.min(4, name.length())) + " " + dur;
            ctx.drawTextWithShadow(mc.textRenderer, display, x, y, color);
            y += spacing;
        }
    }

    private static String toRoman(int n) {
        return switch (n) { case 2->"II"; case 3->"III"; case 4->"IV"; case 5->"V"; default->String.valueOf(n); };
    }
}
