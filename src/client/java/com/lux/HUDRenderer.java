package com.lux;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;
import net.minecraft.util.Identifier;
import net.minecraft.client.network.PlayerListEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HUDRenderer {

    public static final List<Long> leftClicks = new ArrayList<>();
    public static final List<Long> rightClicks = new ArrayList<>();

    public static void addLeftClick() {
        leftClicks.add(Util.getMeasuringTimeMs());
    }

    public static void addRightClick() {
        rightClicks.add(Util.getMeasuringTimeMs());
    }

    public static void render(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options.hudHidden || client.player == null) return;

        HUDConfig config = HUDConfig.getInstance();

        HUDConfig.ModuleData fpsMod = config.getModule("FPS Counter");
        if (fpsMod != null && fpsMod.enabled) {
            String fpsText = "FPS: " + client.getCurrentFps();
            drawContext.drawTextWithShadow(client.textRenderer, fpsText, fpsMod.x, fpsMod.y, 0xFFFFFFFF);
        }

        HUDConfig.ModuleData cpsMod = config.getModule("CPS");
        if (cpsMod != null && cpsMod.enabled) {
            long time = Util.getMeasuringTimeMs();
            leftClicks.removeIf(t -> time - t > 1000);
            rightClicks.removeIf(t -> time - t > 1000);

            int lCps = leftClicks.size();
            int rCps = rightClicks.size();

            String cpsText;
            if (cpsMod.mode == 0) {
                cpsText = lCps + " CPS | " + rCps + " CPS";
            } else {
                cpsText = lCps + " | " + rCps;
            }

            int textWidth = client.textRenderer.getWidth(cpsText);
            drawContext.fill(cpsMod.x - 2, cpsMod.y - 2, cpsMod.x + textWidth + 2, cpsMod.y + 10, 0x55000000);
            drawContext.drawTextWithShadow(client.textRenderer, cpsText, cpsMod.x, cpsMod.y, 0xFFFFFFFF);
        }

        HUDConfig.ModuleData armorMod = config.getModule("Armor Status");
        if (armorMod != null && armorMod.enabled) {
            int currentY = armorMod.y;
            ItemStack[] armorArray = new ItemStack[4];
            armorArray[0] = client.player.getEquippedStack(EquipmentSlot.FEET);
            armorArray[1] = client.player.getEquippedStack(EquipmentSlot.LEGS);
            armorArray[2] = client.player.getEquippedStack(EquipmentSlot.CHEST);
            armorArray[3] = client.player.getEquippedStack(EquipmentSlot.HEAD);

            for (int i = 3; i >= 0; i--) {
                ItemStack stack = armorArray[i];
                if (stack != null && !stack.isEmpty()) {
                    drawContext.drawItem(stack, armorMod.x, currentY);

                    int color = 0xFFFFFFFF;
                    String text = "";

                    if (stack.isDamageable()) {
                        int maxDamage = stack.getMaxDamage();
                        int damage = stack.getDamage();
                        int remaining = maxDamage - damage;

                        float percentage = (float) remaining / maxDamage;
                        if (percentage > 0.7f) {
                            color = 0xFF55FF55;
                        } else if (percentage > 0.3f) {
                            color = 0xFFFFFF55;
                        } else {
                            color = 0xFFFF5555;
                        }

                        if (armorMod.showPercentage) {
                            text = (int) (percentage * 100) + "%";
                        } else {
                            text = remaining + "/" + maxDamage;
                        }
                    } else {
                        if (stack.getCount() > 1) {
                            text = String.valueOf(stack.getCount());
                        }
                    }

                    if (!text.isEmpty()) {
                        drawContext.drawTextWithShadow(client.textRenderer, text, armorMod.x + 20, currentY + 4, color);
                    }

                    currentY += 18;
                }
            }
        }

        HUDConfig.ModuleData pingMod = config.getModule("Ping");
        if (pingMod != null && pingMod.enabled && client.getNetworkHandler() != null && client.player != null) {
            PlayerListEntry entry = client.getNetworkHandler()
                    .getPlayerListEntry(client.player.getUuid());
            if (entry != null) {
                int latency = entry.getLatency();
                if (pingMod.mode == 0) {
                    String pingText = "Ping: " + latency + " ms";
                    int color = latency < 50 ? 0xFF00FF00 : latency < 150 ? 0xFFFFFF00 : 0xFFFF0000;
                    drawContext.drawTextWithShadow(client.textRenderer, pingText, pingMod.x, pingMod.y, color);
                } else {
                    drawContext.drawGuiTexture(
                            Identifier.of("minecraft",
                                    "icon/ping_" + (latency < 50 ? "5" : latency < 100 ? "4" : latency < 150 ? "3" : latency < 300 ? "2" : "1")),
                            pingMod.x, pingMod.y, 10, 8);
                    int barWidth = 20;
                    int barHeight = 2;
                    int fillWidth = Math.max(1, (int) (barWidth * (1.0f - Math.min(latency, 300) / 300.0f)));
                    int color = latency < 50 ? 0xFF00FF00 : latency < 150 ? 0xFFFFFF00 : 0xFFFF0000;
                    drawContext.fill(pingMod.x, pingMod.y + 10, pingMod.x + barWidth, pingMod.y + 10 + barHeight, 0xFF444444);
                    drawContext.fill(pingMod.x, pingMod.y + 10, pingMod.x + fillWidth, pingMod.y + 10 + barHeight, color);
                }
            }
        }

        HUDConfig.ModuleData potionMod = config.getModule("Potion Effects");
        if (potionMod != null && potionMod.enabled && client.player != null) {
            Collection<StatusEffectInstance> effects = client.player.getStatusEffects();
            if (!effects.isEmpty()) {
                int currentY = potionMod.y;
                int currentX = potionMod.x;

                for (StatusEffectInstance effect : effects) {
                    RegistryEntry<StatusEffect> effectType = effect.getEffectType();
                    Sprite sprite = client.getStatusEffectSpriteManager().getSprite(effectType);

                    com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(0, sprite.getAtlasId());
                    drawContext.drawSprite(currentX, currentY, 0, 18, 18, sprite);

                    if (potionMod.mode == 0) {
                        int durationTicks = effect.getDuration();
                        String timeText = StringHelper.formatTicks(durationTicks, client.world.getTickManager().getTickRate());
                        String nameText = effectType.value().getName().getString()
                                + (effect.getAmplifier() > 0 ? " " + (effect.getAmplifier() + 1) : "");

                        drawContext.drawTextWithShadow(client.textRenderer, nameText, currentX + 22, currentY + 1, 0xFFFFFF);
                        drawContext.drawTextWithShadow(client.textRenderer, timeText, currentX + 22, currentY + 11, 0xAAAAAA);
                        currentY += 22;
                    } else {
                        int durationTicks = effect.getDuration();
                        int maxDuration = 1200;
                        float perc = Math.min(1.0f, (float) durationTicks / maxDuration);
                        int barWidth = 18;
                        int fillWidth = (int) (barWidth * perc);

                        drawContext.fill(currentX, currentY + 20, currentX + barWidth, currentY + 22, 0xFF444444);
                        drawContext.fill(currentX, currentY + 20, currentX + fillWidth, currentY + 22, 0xFF55FFFF);

                        currentX += 24;
                    }
                }
            }
        }
    }
}
