package com.lux.mixin;

import com.lux.module.render.CPSModule;
import net.minecraft.client.Mouse;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (action != 1) return;
        long now = Util.getMeasuringTimeMs();
        if (button == 0) CPSModule.leftClicks.add(now);
        else if (button == 1) CPSModule.rightClicks.add(now);
    }
}
