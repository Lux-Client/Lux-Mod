package com.lux.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.Element;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends net.minecraft.client.gui.screen.Screen {

    protected TitleScreenMixin(net.minecraft.text.Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        TitleScreen screen = (TitleScreen) (Object) this;

        removeRealmsButton(screen.children());

        this.addDrawableChild(ButtonWidget.builder(
                net.minecraft.text.Text.literal("Mods"),
                button -> {
                    if (this.client != null) {
                        this.client.setScreen(new com.lux.gui.LunarSettingsScreen());
                    }
                }).dimensions(4, 4, 60, 20).build());
    }

    private void removeRealmsButton(Iterable<? extends Element> elements) {
        if (elements == null) return;
        for (Element element : elements) {
            if (element instanceof ButtonWidget btn
                    && btn.getMessage().getContent() instanceof TranslatableTextContent t
                    && t.getKey().equals("menu.online")) {
                btn.visible = false;
                btn.active = false;
                btn.setWidth(0);
                btn.setHeight(0);
            } else if (element instanceof net.minecraft.client.gui.ParentElement parent) {
                removeRealmsButton(parent.children());
            }
        }
    }
}
