package com.lux.mixin;

import com.lux.module.ModuleManager;
import com.lux.module.render.ZoomModule;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class ZoomMixin {

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void onGetFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        ModuleManager mm = ModuleManager.getInstance();
        if (mm == null) return;
        ZoomModule zoom = mm.get(ZoomModule.class);
        if (zoom == null || !zoom.isEnabled()) return;
        float factor = zoom.getZoomFactor();
        if (factor > 1.0f) cir.setReturnValue(cir.getReturnValue() / factor);
    }
}
