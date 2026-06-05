package com.lux.event;

public abstract class Event {
    private boolean cancelled;
    public boolean isCancelled() { return cancelled; }
    public void cancel() { cancelled = true; }
    public boolean isCancellable() { return false; }
}
