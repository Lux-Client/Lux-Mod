package com.lux.module.qol;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.BooleanSetting;
import com.lux.setting.IntSetting;

import java.util.List;

public class AutoTipModule extends Module {
    private BooleanSetting onWin;
    private IntSetting delay;
    private static final List<String> TRIGGERS = List.of("Winner:", "You won", "Victory");

    public AutoTipModule() { super("Auto Tip", "Tips boosters automatically after a game (Hypixel)", Category.QUALITY_OF_LIFE); }

    @Override protected void init() {
        onWin = register(new BooleanSetting("Only On Win", "Only tip on a win", false));
        delay = register(new IntSetting("Delay", "Delay before tipping in ms", 2000, 500, 10000));
    }

    public void handleMessage(String text) {
        if (!isEnabled()) return;
        if (TRIGGERS.stream().noneMatch(text::contains)) return;
        new Thread(() -> {
            try { Thread.sleep(delay.getValue()); } catch (InterruptedException ignored) {}
            mc.execute(() -> { if (mc.player != null) mc.player.networkHandler.sendChatMessage("/tip all"); });
        }).start();
    }
}
