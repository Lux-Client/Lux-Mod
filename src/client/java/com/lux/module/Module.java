package com.lux.module;

import com.lux.config.ConfigManager;
import com.lux.setting.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled;
    private int keyCode = -1;
    private int hudX    = 10;
    private int hudY    = 10;

    protected final List<Setting<?>> settings = new ArrayList<>();

    protected Module(String name, String description, Category category) {
        this.name        = name;
        this.description = description;
        this.category    = category;
        init();
    }

    protected void init() {}

    public final void toggle() { setEnabled(!enabled); }

    public final void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;
        if (enabled) onEnable(); else onDisable();
        ConfigManager.getInstance().save();
    }

    public final void setEnabledSilent(boolean enabled) { this.enabled = enabled; }

    protected void onEnable()  {}
    protected void onDisable() {}

    public void onTick() {}
    public void onRenderHUD(DrawContext ctx, float delta) {}

    protected <T extends Setting<?>> T register(T s) { settings.add(s); return s; }

    public String getName()        { return name; }
    public String getDescription() { return description; }
    public Category getCategory()  { return category; }
    public boolean isEnabled()     { return enabled; }
    public int getKeyCode()        { return keyCode; }
    public void setKeyCode(int k)  { this.keyCode = k; }
    public int getHudX()           { return hudX; }
    public int getHudY()           { return hudY; }
    public void setHudPos(int x, int y) { this.hudX = x; this.hudY = y; }
    public List<Setting<?>> getSettings() { return settings; }
}
