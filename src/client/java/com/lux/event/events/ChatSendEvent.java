package com.lux.event.events;

import com.lux.event.Event;

public class ChatSendEvent extends Event {
    private String message;
    public ChatSendEvent(String message) { this.message = message; }
    public String getMessage() { return message; }
    public void setMessage(String m) { this.message = m; }
    @Override public boolean isCancellable() { return true; }
}
