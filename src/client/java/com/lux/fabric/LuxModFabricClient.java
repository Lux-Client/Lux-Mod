package com.lux.fabric;

import com.lux.HUDConfig;
import com.lux.HUDRenderer;
import com.lux.ModCompat;
import com.lux.platform.PlatformHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.stb.STBImage;

import java.io.InputStream;
import java.nio.ByteBuffer;

public class LuxModFabricClient implements ClientModInitializer {

    private static KeyBinding OPEN_GUI_KEY;

    @Override
    public void onInitializeClient() {
        PlatformHelper.setInstance(new FabricPlatformHelper());
        HUDConfig.reload();

        OPEN_GUI_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.lux.open_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.lux.general"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_GUI_KEY.wasPressed()) {
                if (client.currentScreen == null) {
                    try {
                        client.setScreen(new com.lux.gui.HUDConfigScreen());
                    } catch (Exception ignored) {
                    }
                }
            }
        });

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            try (InputStream streamIn = getClass().getResourceAsStream("/assets/lux/icon.png")) {
                if (streamIn == null) return;
                byte[] bytes = streamIn.readAllBytes();
                ByteBuffer rawBuf = MemoryUtil.memAlloc(bytes.length);
                rawBuf.put(bytes).flip();

                int[] w = {0}, h = {0}, channels = {0};
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
            HUDRenderer.render(drawContext, tickDelta);
        });
    }
}
