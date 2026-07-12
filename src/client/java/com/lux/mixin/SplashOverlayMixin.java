package com.lux.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.resource.ResourceReload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;

@Mixin(SplashOverlay.class)
public abstract class SplashOverlayMixin {

    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private ResourceReload reload;
    @Shadow private long reloadStartTime;
    @Shadow private long reloadCompleteTime;
    @Shadow private boolean reloading;

    private Consumer<Optional<Throwable>> lux$exceptionHandler;
    private float smoothedProgress = 0.0f;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initAdjustments(MinecraftClient client, ResourceReload reload,
            Consumer<Optional<Throwable>> exceptionHandler, boolean reloading, CallbackInfo ci) {
        this.lux$exceptionHandler = exceptionHandler;
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void renderCustomOverlay(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ci.cancel();

        if (this.client == null || this.reload == null) return;

        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();
        long time = Util.getMeasuringTimeMs();

        if (this.reloading && this.reloadStartTime == -1L) {
            this.reloadStartTime = time;
        }

        float fadeOut = this.reloadCompleteTime > -1L ? (float) (time - this.reloadCompleteTime) / 1000.0F : -1.0F;
        float fadeIn = this.reloadStartTime > -1L ? (float) (time - this.reloadStartTime) / 1000.0F : -1.0F;

        float rawProgress = this.reload.getProgress();
        if (rawProgress >= 0.95f) rawProgress = 1.0f;
        this.smoothedProgress = MathHelper.lerp(0.15f, this.smoothedProgress, rawProgress);
        if (this.smoothedProgress >= 0.99f) this.smoothedProgress = 1.0f;

        if (fadeOut >= 1.0F) {
            this.client.setOverlay(null);
        }

        if (this.reloadCompleteTime == -1L && this.reload.isComplete() && (!this.reloading || fadeIn >= 2.0F)) {
            try {
                this.reload.throwException();
                if (this.lux$exceptionHandler != null) {
                    this.lux$exceptionHandler.accept(Optional.empty());
                }
            } catch (Throwable var23) {
                if (this.lux$exceptionHandler != null) {
                    this.lux$exceptionHandler.accept(Optional.of(var23));
                }
            }
            this.reloadCompleteTime = time;

            if (this.client.currentScreen != null) {
                this.client.currentScreen.init(this.client, context.getScaledWindowWidth(),
                        context.getScaledWindowHeight());
            }
        }

        float alpha = 1.0F;
        if (fadeIn >= 0.0F && fadeOut < 0.0F) {
            alpha = MathHelper.clamp(fadeIn / 0.5f, 0.0F, 1.0F);
        } else if (fadeOut >= 0.0F) {
            alpha = 1.0F - MathHelper.clamp(fadeOut / 0.5F, 0.0F, 1.0F);
        }

        if (fadeOut >= 0.0f && this.client.currentScreen != null) {
            this.client.currentScreen.render(context, mouseX, mouseY, delta);
        }

        if (alpha > 0.0f) {
            com.mojang.blaze3d.systems.RenderSystem.enableBlend();

            int bgAlpha = (int) (alpha * 255);
            int bgTop = (bgAlpha << 24) | 0x21090A;
            int bgBot = (bgAlpha << 24) | 0x000000;
            context.fillGradient(0, 0, width, height, bgTop, bgBot);

            com.mojang.blaze3d.systems.RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);

            net.minecraft.text.Text title = net.minecraft.text.Text.literal("Lux")
                    .formatted(net.minecraft.util.Formatting.GREEN);
            int titleWidth = this.client.textRenderer.getWidth(title) * 2;
            context.getMatrices().push();
            context.getMatrices().translate(width / 2.0, height / 2.0 - 20, 0);
            context.getMatrices().scale(2.0f, 2.0f, 1.0f);
            context.drawCenteredTextWithShadow(this.client.textRenderer, title, 0, 0, 0xFFFFFF);
            context.getMatrices().pop();

            int barWidth = 200;
            int barHeight = 4;
            int barX = (width - barWidth) / 2;
            int barY = height / 2 + 20;

            int progressAlpha = (int) (alpha * 255);
            int bgColor = (progressAlpha << 24) | 0x333333;
            int fillColor = (progressAlpha << 24) | 0x55FF55;

            context.fill(barX, barY, barX + barWidth, barY + barHeight, bgColor);
            int fillWidth = (int) (barWidth * this.smoothedProgress);
            if (fillWidth > 0) {
                context.fill(barX, barY, barX + fillWidth, barY + barHeight, fillColor);
            }

            com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
            com.mojang.blaze3d.systems.RenderSystem.disableBlend();
        }
    }
}
