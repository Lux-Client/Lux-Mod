package com.lux.event.events;

import com.lux.event.Event;
import net.minecraft.text.Text;

public class ChatReceiveEvent extends Event {
    public final Text message;
    public final String plain;
    public ChatReceiveEvent(Text message) {
        this.message = message;
        this.plain   = message.getString();
    }
    @Override public boolean isCancellable() { return true; }
}
