package com.lux.mixin;

import com.lux.module.ModuleManager;
import com.lux.module.render.FullbrightModule;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleOption.class)
public class FullbrightMixin {

    @Inject(method = "getValue", at = @At("RETURN"), cancellable = true)
    private void onGetValue(CallbackInfoReturnable<Object> cir) {
        ModuleManager mm = ModuleManager.getInstance();
        if (mm == null) return;
        FullbrightModule fb = mm.get(FullbrightModule.class);
        if (fb == null || !fb.isEnabled()) return;

        if ((Object) this == net.minecraft.client.MinecraftClient.getInstance().options.getGamma()) {
            cir.setReturnValue((double) fb.getBrightness());
        }
    }
}
