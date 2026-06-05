package com.lux.module.render;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.BooleanSetting;
import com.lux.setting.FloatSetting;

public class ItemPhysicsModule extends Module {
    private FloatSetting rotationSpeed;
    private BooleanSetting randomRotation, affectBlocks;

    public ItemPhysicsModule() { super("Item Physics", "Makes dropped items tumble and rotate", Category.RENDER); }

    @Override protected void init() {
        rotationSpeed  = register(new FloatSetting("Rotation Speed", "Degrees per tick", 4.0f, 0.5f, 15.0f));
        randomRotation = register(new BooleanSetting("Random Axis", "Randomize rotation axis", true));
        affectBlocks   = register(new BooleanSetting("Affect Blocks", "Also rotate block items", true));
    }

    public float getRotationSpeed()   { return rotationSpeed.getValue(); }
    public boolean isRandomRotation() { return randomRotation.getValue(); }
    public boolean affectsBlocks()    { return affectBlocks.getValue(); }
}
