package com.lux.module.performance;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.BooleanSetting;
import com.lux.setting.IntSetting;

public class EntityCullingModule extends Module {

    private IntSetting cullDistance;
    private BooleanSetting cullPlayers;
    private BooleanSetting cullPassive;
    private BooleanSetting cullHostile;

    public EntityCullingModule() {
        super("Entity Culling", "Skips rendering entities that are not visible", Category.PERFORMANCE);
    }

    @Override
    protected void init() {
        cullDistance = register(new IntSetting("Cull Distance", "Max render distance in blocks", 64, 16, 256));
        cullPlayers  = register(new BooleanSetting("Cull Players", "Cull other player entities", false));
        cullPassive  = register(new BooleanSetting("Cull Passive", "Cull passive mobs", true));
        cullHostile  = register(new BooleanSetting("Cull Hostile", "Cull hostile mobs", true));
    }

    public int getCullDistance()  { return cullDistance.getValue(); }
    public boolean cullsPlayers() { return cullPlayers.getValue(); }
    public boolean cullsPassive() { return cullPassive.getValue(); }
    public boolean cullsHostile() { return cullHostile.getValue(); }
}
