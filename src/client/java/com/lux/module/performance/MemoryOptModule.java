package com.lux.module.performance;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.BooleanSetting;
import com.lux.setting.IntSetting;

public class MemoryOptModule extends Module {

    private IntSetting gcInterval;
    private BooleanSetting aggressiveGC;
    private BooleanSetting logGC;

    public MemoryOptModule() {
        super("Memory Optimizer", "Periodically triggers GC to reduce memory spikes", Category.PERFORMANCE);
    }

    @Override
    protected void init() {
        gcInterval  = register(new IntSetting("GC Interval", "Seconds between GC calls", 30, 10, 300));
        aggressiveGC= register(new BooleanSetting("Aggressive GC", "Run GC twice per cycle", false));
        logGC       = register(new BooleanSetting("Log GC", "Print GC info to chat", false));
    }

    private long lastGC = 0;

    @Override
    public void onTick() {
        long now = System.currentTimeMillis();
        if (now - lastGC > gcInterval.getValue() * 1000L) {
            lastGC = now;
            System.gc();
            if (aggressiveGC.getValue()) System.gc();
            if (logGC.getValue() && mc.player != null) {
                long free  = Runtime.getRuntime().freeMemory() / 1024 / 1024;
                long total = Runtime.getRuntime().totalMemory() / 1024 / 1024;
                mc.player.sendMessage(net.minecraft.text.Text.of("[Lux] Memory: " + free + "/" + total + " MB"), false);
            }
        }
    }
}
