package com.lux.setting;

import java.util.function.Consumer;

public abstract class Setting<T> {
    protected final String name;
    protected final String description;
    protected T value;
    private Consumer<T> listener;

    protected Setting(String name, String description, T defaultValue) {
        this.name        = name;
        this.description = description;
        this.value       = defaultValue;
    }

    public String getName()        { return name; }
    public String getDescription() { return description; }
    public T getValue()            { return value; }

    public void setValue(T value) {
        this.value = value;
        if (listener != null) listener.accept(value);
    }

    public void setValueSilent(T value) { this.value = value; }

    public Setting<T> onChange(Consumer<T> listener) { this.listener = listener; return this; }

    public abstract String getDisplayValue();
}
