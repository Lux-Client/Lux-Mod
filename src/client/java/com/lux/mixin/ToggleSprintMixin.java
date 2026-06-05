package com.lux.mixin;

import com.lux.module.ModuleManager;
import com.lux.module.qol.ToggleSprintModule;
import com.lux.module.qol.ToggleSneakModule;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.input.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ToggleSprintMixin {

    @Shadow public Input input;

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void onTickMovement(CallbackInfo ci) {
        ModuleManager mm = ModuleManager.getInstance();
        if (mm == null) return;
        ClientPlayerEntity self = (ClientPlayerEntity)(Object)this;

        ToggleSprintModule sprint = mm.get(ToggleSprintModule.class);
        if (sprint != null && sprint.isEnabled()) {
            boolean forward = input != null && input.movementForward > 0.0f;
            if (!sprint.isOnlyForward() || forward) {
                if (!self.isSneaking()) self.setSprinting(true);
            }
        }

        ToggleSneakModule sneak = mm.get(ToggleSneakModule.class);
        if (sneak != null && sneak.isEnabled() && sneak.isSneaking() && input != null) {
            input.sneaking = true;
        }
    }
}
