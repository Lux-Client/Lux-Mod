package com.lux.module.performance;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.BooleanSetting;
import com.lux.setting.IntSetting;

public class ParticleOptModule extends Module {

    private IntSetting maxParticles;
    private BooleanSetting disableExplosion;
    private BooleanSetting disableRedstone;
    private BooleanSetting disableAmbient;

    public ParticleOptModule() {
        super("Particle Optimizer", "Limits and filters particles to improve FPS", Category.PERFORMANCE);
    }

    @Override
    protected void init() {
        maxParticles     = register(new IntSetting("Max Particles", "Maximum simultaneous particles", 500, 50, 4000));
        disableExplosion = register(new BooleanSetting("No Explosions", "Hide explosion particles", false));
        disableRedstone  = register(new BooleanSetting("No Redstone", "Hide redstone particles", false));
        disableAmbient   = register(new BooleanSetting("No Ambient", "Hide ambient/effect particles", true));
    }

    public int getMaxParticles()       { return maxParticles.getValue(); }
    public boolean noExplosion()       { return disableExplosion.getValue(); }
    public boolean noRedstone()        { return disableRedstone.getValue(); }
    public boolean noAmbient()         { return disableAmbient.getValue(); }
}
