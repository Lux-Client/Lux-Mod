package com.lux.event;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventBus {
    private static final Map<Class<?>, List<Consumer<Object>>> LISTENERS = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> void subscribe(Class<T> type, Consumer<T> listener) {
        LISTENERS.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>())
                 .add(event -> listener.accept((T) event));
    }

    public static <T extends Event> T post(T event) {
        List<Consumer<Object>> list = LISTENERS.get(event.getClass());
        if (list != null) {
            for (Consumer<Object> l : list) {
                l.accept(event);
                if (event.isCancellable() && event.isCancelled()) break;
            }
        }
        return event;
    }
}
