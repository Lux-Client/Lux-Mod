package com.lux.hud;

import com.lux.module.Module;
import com.lux.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

public class HUDManager {

    private static HUDManager instance;

    private HUDManager() {}

    public static void init() { instance = new HUDManager(); }
    public static HUDManager getInstance() { return instance; }

    public void renderAll(DrawContext ctx, float delta) {
        for (Module m : ModuleManager.getInstance().getAll()) {
            if (m.isEnabled()) {
                try { m.onRenderHUD(ctx, delta); }
                catch (Exception e) {
                    System.err.println("[Lux] HUD render error in " + m.getName() + ": " + e.getMessage());
                }
            }
        }
    }

    public List<Module> getHUDModules() {
        return ModuleManager.getInstance().getAll().stream()
                .filter(Module::isEnabled)
                .toList();
    }
}
