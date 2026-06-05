package com.lux.event.events;

import com.lux.event.Event;

public class KeyInputEvent extends Event {
    public final int key;
    public final int action;
    public KeyInputEvent(int key, int action) { this.key = key; this.action = action; }
    @Override public boolean isCancellable() { return true; }
}
