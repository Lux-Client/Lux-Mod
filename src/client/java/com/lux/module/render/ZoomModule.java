package com.lux.module.render;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.BooleanSetting;
import com.lux.setting.FloatSetting;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ZoomModule extends Module {
    private FloatSetting zoomFactor;
    private BooleanSetting smoothZoom;
    private float currentZoom = 1.0f;

    public ZoomModule() {
        super("Zoom", "Zoom in by holding the keybind (default: V)", Category.RENDER);
        setKeyCode(GLFW.GLFW_KEY_V);
    }

    @Override protected void init() {
        zoomFactor = register(new FloatSetting("Zoom Factor", "Zoom level", 3.0f, 1.5f, 10.0f));
        smoothZoom = register(new BooleanSetting("Smooth Zoom", "Interpolated zoom", true));
    }

    public boolean isZooming() {
        if (!isEnabled() || mc.getWindow() == null) return false;
        int k = getKeyCode() == -1 ? GLFW.GLFW_KEY_V : getKeyCode();
        return InputUtil.isKeyPressed(mc.getWindow().getHandle(), k);
    }

    public float getZoomFactor() {
        float target = isZooming() ? zoomFactor.getValue() : 1.0f;
        if (!smoothZoom.getValue()) { currentZoom = target; return target; }
        currentZoom += (target - currentZoom) * 0.15f;
        return currentZoom;
    }
}
