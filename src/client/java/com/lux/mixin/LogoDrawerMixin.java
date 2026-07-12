package com.lux.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LogoDrawer.class)
public class LogoDrawerMixin {

    @Inject(method = "draw(Lnet/minecraft/client/gui/DrawContext;IFI)V", at = @At("HEAD"), cancellable = true)
    public void onDrawWithY(DrawContext context, int screenWidth, float alpha, int y, CallbackInfo ci) {
        ci.cancel();
        drawLuxLogo(context, screenWidth, y);
    }

    @Inject(method = "draw(Lnet/minecraft/client/gui/DrawContext;IF)V", at = @At("HEAD"), cancellable = true)
    public void onDraw(DrawContext context, int screenWidth, float alpha, CallbackInfo ci) {
        ci.cancel();
        drawLuxLogo(context, screenWidth, 30);
    }

    private void drawLuxLogo(DrawContext context, int screenWidth, int y) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.textRenderer == null) return;

        Text titleText = Text.literal("Lux").formatted(net.minecraft.util.Formatting.GREEN);

        int textWidth = client.textRenderer.getWidth(titleText) * 3;
        int bgWidth = textWidth + 40;
        int bgHeight = 50;
        int startX = (screenWidth - bgWidth) / 2;
        int startY = y - 10;

        context.fill(startX, startY, startX + bgWidth, startY + bgHeight, 0xCC000000);
        context.fill(startX + 1, startY + 1, startX + bgWidth - 1, startY + bgHeight - 1, 0xBB111111);

        context.getMatrices().push();
        context.getMatrices().scale(3.0f, 3.0f, 3.0f);
        context.drawCenteredTextWithShadow(client.textRenderer, titleText, screenWidth / 2 / 3, y / 3, 0xFFFFFF);
        context.getMatrices().pop();

        String version = net.minecraft.SharedConstants.getGameVersion().getName();
        context.drawTextWithShadow(client.textRenderer, Text.literal(version), startX + bgWidth - 30, startY + 6, 0xAAAAAA);
    }
}
