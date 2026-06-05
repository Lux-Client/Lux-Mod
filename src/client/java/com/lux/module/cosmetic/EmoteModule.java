package com.lux.module.cosmetic;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.EnumSetting;
import com.lux.setting.IntSetting;

public class EmoteModule extends Module {

    public enum EmoteType { WAVE, DANCE, BOW, SALUTE, POINT }

    private EnumSetting<EmoteType> emote;
    private IntSetting duration;

    private boolean playing;
    private long startTime;

    public EmoteModule() {
        super("Emotes", "Play character animations with a keybind", Category.COSMETICS);
    }

    @Override
    protected void init() {
        emote    = register(new EnumSetting<>("Emote", "Active emote", EmoteType.WAVE));
        duration = register(new IntSetting("Duration", "Emote duration in ticks", 40, 10, 200));
    }

    @Override
    protected void onEnable() { play(); }

    public void play() {
        playing   = true;
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onTick() {
        if (playing && System.currentTimeMillis() - startTime > duration.getValue() * 50L) {
            playing = false;
            setEnabled(false);
        }
    }

    public boolean isPlaying()  { return playing; }
    public EmoteType getEmote() { return emote.getValue(); }
}
