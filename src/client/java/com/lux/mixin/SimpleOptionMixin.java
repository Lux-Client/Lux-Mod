package com.lux.mixin;

import com.lux.HUDConfig;
import com.lux.ModCompat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleOption.class)
public class SimpleOptionMixin<T> {

    @Inject(method = "getValue", at = @At("HEAD"), cancellable = true)
    private void onGetValue(CallbackInfoReturnable<T> cir) {
        if (!ModCompat.isFullbrightAvailable()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.options != null && client.options.getGamma() == (Object) this) {
            HUDConfig.ModuleData fbModule = HUDConfig.getInstance().getModule("Fullbright");
            if (fbModule != null && fbModule.enabled) {
                @SuppressWarnings("unchecked")
                T fullbrightValue = (T) Double.valueOf(100.0);
                cir.setReturnValue(fullbrightValue);
            }
        }
    }
}
