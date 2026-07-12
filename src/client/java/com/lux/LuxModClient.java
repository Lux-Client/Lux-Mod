package com.lux;

import com.lux.config.HUDConfig;
import com.lux.gui.HUDConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.stb.STBImage;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class LuxModClient implements ClientModInitializer {

    public static final List<Long> leftClicks = new ArrayList<>();
    public static final List<Long> rightClicks = new ArrayList<>();

    public static void addLeftClick() {
        leftClicks.add(net.minecraft.util.Util.getMeasuringTimeMs());
    }

    public static void addRightClick() {
        rightClicks.add(net.minecraft.util.Util.getMeasuringTimeMs());
    }

    private static KeyBinding OPEN_GUI_KEY;

    @Override
    public void onInitializeClient() {
        OPEN_GUI_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.lux.open_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.lux.general"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_GUI_KEY.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new HUDConfigScreen());
                }
            }
        });

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            try (InputStream streamIn = getClass().getResourceAsStream("/assets/lux/textures/gui/icon.png")) {
                if (streamIn == null) return;
                byte[] bytes = streamIn.readAllBytes();
                ByteBuffer rawBuf = MemoryUtil.memAlloc(bytes.length);
                rawBuf.put(bytes).flip();

                int[] w = { 0 }, h = { 0 }, channels = { 0 };
                ByteBuffer pixels = STBImage.stbi_load_from_memory(rawBuf, w, h, channels, 4);
                MemoryUtil.memFree(rawBuf);

                if (pixels == null) return;
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    GLFWImage.Buffer icons = GLFWImage.malloc(1, stack);
                    icons.position(0).width(w[0]).height(h[0]).pixels(pixels);
                    icons.position(0);
                    long handle = client.getWindow().getHandle();
                    GLFW.glfwSetWindowIcon(handle, icons);
                    String version = net.minecraft.SharedConstants.getGameVersion().getName();
                    GLFW.glfwSetWindowTitle(handle, "Lux Client " + version);
                } finally {
                    STBImage.stbi_image_free(pixels);
                }
            } catch (Exception e) {
                System.err.println("Lux: Failed to set window icon: " + e.getMessage());
            }
        });

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
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
                long time = net.minecraft.util.Util.getMeasuringTimeMs();
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
                armorArray[0] = client.player.getEquippedStack(net.minecraft.entity.EquipmentSlot.FEET);
                armorArray[1] = client.player.getEquippedStack(net.minecraft.entity.EquipmentSlot.LEGS);
                armorArray[2] = client.player.getEquippedStack(net.minecraft.entity.EquipmentSlot.CHEST);
                armorArray[3] = client.player.getEquippedStack(net.minecraft.entity.EquipmentSlot.HEAD);

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
                net.minecraft.client.network.PlayerListEntry entry = client.getNetworkHandler()
                        .getPlayerListEntry(client.player.getUuid());
                if (entry != null) {
                    int latency = entry.getLatency();
                    if (pingMod.mode == 0) {
                        String pingText = "Ping: " + latency + " ms";
                        int color = latency < 50 ? 0xFF00FF00 : latency < 150 ? 0xFFFFFF00 : 0xFFFF0000;
                        drawContext.drawTextWithShadow(client.textRenderer, pingText, pingMod.x, pingMod.y, color);
                    } else {
                        drawContext.drawGuiTexture(
                                net.minecraft.util.Identifier.of("minecraft",
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
                java.util.Collection<net.minecraft.entity.effect.StatusEffectInstance> effects = client.player.getStatusEffects();
                if (!effects.isEmpty()) {
                    int currentY = potionMod.y;
                    int currentX = potionMod.x;

                    for (net.minecraft.entity.effect.StatusEffectInstance effect : effects) {
                        net.minecraft.registry.entry.RegistryEntry<net.minecraft.entity.effect.StatusEffect> effectType = effect.getEffectType();
                        net.minecraft.client.texture.Sprite sprite = client.getStatusEffectSpriteManager().getSprite(effectType);

                        com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(0, sprite.getAtlasId());
                        drawContext.drawSprite(currentX, currentY, 0, 18, 18, sprite);

                        if (potionMod.mode == 0) {
                            int durationTicks = effect.getDuration();
                            String timeText = net.minecraft.util.StringHelper.formatTicks(durationTicks, client.world.getTickManager().getTickRate());
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
        });
    }
}
