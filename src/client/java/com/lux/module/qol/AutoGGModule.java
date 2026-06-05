package com.lux.module.qol;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.BooleanSetting;
import com.lux.setting.EnumSetting;
import com.lux.setting.IntSetting;

import java.util.List;

public class AutoGGModule extends Module {
    public enum GGMessage { GG, GG_EZ, GOOD_GAME }
    private EnumSetting<GGMessage> message;
    private IntSetting delay;
    private BooleanSetting onLoss;

    private static final List<String> WIN_TRIGGERS  = List.of("Winner:", "1st place", "You won", "Congratulations", "Victory");
    private static final List<String> LOSS_TRIGGERS = List.of("Game Over", "You lost", "Better luck");

    public AutoGGModule() { super("Auto GG", "Sends GG at the end of a game", Category.QUALITY_OF_LIFE); }

    @Override protected void init() {
        message = register(new EnumSetting<>("Message", "Message to send", GGMessage.GG));
        delay   = register(new IntSetting("Delay", "Delay in ms before sending", 500, 0, 5000));
        onLoss  = register(new BooleanSetting("On Loss", "Send GG when you lose", true));
    }

    public void handleMessage(String text) {
        if (!isEnabled()) return;
        boolean win  = WIN_TRIGGERS.stream().anyMatch(text::contains);
        boolean loss = onLoss.getValue() && LOSS_TRIGGERS.stream().anyMatch(text::contains);
        if (!win && !loss) return;
        String msg = switch (message.getValue()) { case GG_EZ -> "gg ez"; case GOOD_GAME -> "Good game!"; default -> "gg"; };
        new Thread(() -> {
            try { Thread.sleep(delay.getValue()); } catch (InterruptedException ignored) {}
            mc.execute(() -> { if (mc.player != null) mc.player.networkHandler.sendChatMessage(msg); });
        }).start();
    }
}
