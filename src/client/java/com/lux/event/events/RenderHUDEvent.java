package com.lux.event.events;

import com.lux.event.Event;
import net.minecraft.client.gui.DrawContext;

public class RenderHUDEvent extends Event {
    public final DrawContext context;
    public final float delta;
    public RenderHUDEvent(DrawContext context, float delta) {
        this.context = context;
        this.delta   = delta;
    }
}
