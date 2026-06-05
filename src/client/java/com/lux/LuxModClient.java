package com.lux;

import com.lux.command.CommandManager;
import com.lux.config.ConfigManager;
import com.lux.cosmetic.CosmeticManager;
import com.lux.gui.ClickGuiScreen;
import com.lux.gui.HUDConfigScreen;
import com.lux.hud.HUDManager;
import com.lux.module.ModuleManager;
import com.lux.module.qol.ToggleSneakModule;
import com.lux.profile.ProfileManager;
import com.lux.theme.ThemeManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import com.lux.module.qol.AutoGGModule;
import com.lux.module.qol.AutoTipModule;
import com.lux.module.qol.ChatFilterModule;
import com.lux.module.qol.ChatTimestampsModule;

public class LuxModClient implements ClientModInitializer {

    public static final String MOD_ID = "lux";

    private static KeyBinding openGuiKey;
    private static KeyBinding openHudKey;

    @Override
    public void onInitializeClient() {
        // ── 1. Core managers ──────────────────────────────────────────
        ThemeManager.init();
        ModuleManager.init();
        ConfigManager.init();
        ConfigManager.getInstance().load();
        HUDManager.init();
        CommandManager.init();
        CosmeticManager.init();
        ProfileManager.init();

        // ── 2. Keybinds ───────────────────────────────────────────────
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.lux.open_gui", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT, "key.categories.lux"));

        openHudKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.lux.open_hud", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F7, "key.categories.lux"));

        // ── 3. HUD rendering ─────────────────────────────────────────
        HudRenderCallback.EVENT.register((ctx, tickCounter) -> {
            if (net.minecraft.client.MinecraftClient.getInstance().options.hudHidden) return;
            float delta = tickCounter.getTickDelta(true);
            HUDManager.getInstance().renderAll(ctx, delta);
        });

        // ── 4. Tick events ────────────────────────────────────────────
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Module tick
            ModuleManager.getInstance().onTick();

            // Open GUI keybind
            while (openGuiKey.wasPressed()) {
                client.setScreen(new ClickGuiScreen());
            }
            while (openHudKey.wasPressed()) {
                client.setScreen(new HUDConfigScreen());
            }

            // Per-module keybind checks
            if (client.getWindow() != null) {
                for (var m : ModuleManager.getInstance().getAll()) {
                    int key = m.getKeyCode();
                    if (key == -1) continue;
                    if (InputUtil.isKeyPressed(client.getWindow().getHandle(), key)) {
                        // Debounce: only fire on press (not hold), handled via wasPressed on KeyBinding
                        // For simple toggle, we use a separate pressed-state tracker
                    }
                }
            }

            // Toggle Sneak keybind (needs special press-once handling)
            ToggleSneakModule ts = ModuleManager.getInstance().get(ToggleSneakModule.class);
            if (ts != null && ts.isEnabled() && ts.isToggleMode()) {
                if (client.options.sneakKey.wasPressed()) {
                    ts.setSneaking(!ts.isSneaking());
                }
            }
        });

        // ── 5. Chat command interception ─────────────────────────────
        ClientSendMessageEvents.ALLOW_CHAT.register(message -> {
            if (CommandManager.getInstance().handle(message)) return false;
            return true;
        });

        // ── 6. Incoming chat events ───────────────────────────────────
        ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> {
            if (overlay) return true;
            String plain = message.getString();

            ChatFilterModule filter = ModuleManager.getInstance().get(ChatFilterModule.class);
            if (filter != null && filter.shouldFilter(plain)) return false;

            AutoGGModule autoGG = ModuleManager.getInstance().get(AutoGGModule.class);
            if (autoGG != null) autoGG.handleMessage(plain);

            AutoTipModule autoTip = ModuleManager.getInstance().get(AutoTipModule.class);
            if (autoTip != null) autoTip.handleMessage(plain);

            return true;
        });

        ClientReceiveMessageEvents.MODIFY_GAME.register((message, overlay) -> {
            if (overlay) return message;
            ChatTimestampsModule ts2 = ModuleManager.getInstance().get(ChatTimestampsModule.class);
            if (ts2 == null || !ts2.isEnabled()) return message;
            return Text.of(ts2.stamp() + message.getString());
        });

        System.out.println("[Lux] Module system loaded — " +
                ModuleManager.getInstance().getAll().size() + " modules registered.");
    }
}
