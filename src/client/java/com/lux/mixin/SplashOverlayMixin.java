package com.lux.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.resource.ResourceReload;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashOverlay.class)
public class SplashOverlayMixin {

    @Shadow @Final private ResourceReload reload;

    private static long luxSplashStart = -1L;
    private static final long MIN_DISPLAY_MS = 1500L;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void luxRender(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (luxSplashStart < 0) luxSplashStart = System.currentTimeMillis();
        long elapsed = System.currentTimeMillis() - luxSplashStart;

        // Once loading is done AND minimum display time has passed,
        // let vanilla handle the fade-out and screen transition.
        // Without this guard, vanilla never sets reloadCompleteTime and the overlay never exits.
        if (reload.isComplete() && elapsed >= MIN_DISPLAY_MS) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        int w = mc.getWindow().getScaledWidth();
        int h = mc.getWindow().getScaledHeight();

        // Dark background
        ctx.fill(0, 0, w, h, 0xFF080B12);

        // Only draw text after 400ms — font glyph textures aren't uploaded during early loading.
        if (elapsed > 400L) {
            ctx.getMatrices().push();
            ctx.getMatrices().translate(w / 2f, h / 2f - 40, 0);
            ctx.getMatrices().scale(2.5f, 2.5f, 1f);
            ctx.drawCenteredTextWithShadow(mc.textRenderer, "Lux Client",
                    0, -mc.textRenderer.fontHeight / 2, 0xFF9D50BB);
            ctx.getMatrices().pop();

            ctx.drawCenteredTextWithShadow(mc.textRenderer, "Loading resources...",
                    w / 2, h / 2 - 4, 0xFF7755AA);
        }

        // Real progress bar via ResourceReload.getProgress()
        float prog = reload.getProgress();
        int barW = w / 3, barX = (w - barW) / 2, barY = h / 2 + 20;
        ctx.fill(barX - 1, barY - 1, barX + barW + 1, barY + 7, 0xFF1A1A2E);
        int filled = (int)(prog * barW);
        if (filled > 0) {
            ctx.fill(barX, barY, barX + filled, barY + 6, 0xFF9D50BB);
            ctx.fill(barX, barY, barX + filled, barY + 2, 0xFFD080FF);
        }

        ci.cancel();
    }
}
