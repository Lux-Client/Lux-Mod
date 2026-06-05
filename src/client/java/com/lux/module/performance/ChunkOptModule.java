package com.lux.module.performance;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.BooleanSetting;
import com.lux.setting.IntSetting;

public class ChunkOptModule extends Module {

    private IntSetting renderDistance;
    private BooleanSetting smoothChunkLoad;
    private BooleanSetting fastMath;

    public ChunkOptModule() {
        super("Chunk Optimizer", "Tweaks chunk rendering for better performance", Category.PERFORMANCE);
    }

    @Override
    protected void init() {
        renderDistance = register(new IntSetting("Smart Distance", "Override render distance when low FPS", 8, 2, 32));
        smoothChunkLoad= register(new BooleanSetting("Smooth Loading", "Gradually load chunks to prevent stutters", true));
        fastMath       = register(new BooleanSetting("Fast Math", "Use faster approximations for trig functions", true));
    }
}
