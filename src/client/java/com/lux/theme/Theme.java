package com.lux.theme;

public class Theme {
    public final String name;
    public final int background;
    public final int card;
    public final int accent;
    public final int enabled;
    public final int disabled;
    public final int text;
    public final int textSecondary;
    public final int border;

    public Theme(String name, int background, int card, int accent, int enabled,
                 int disabled, int text, int textSecondary, int border) {
        this.name          = name;
        this.background    = background;
        this.card          = card;
        this.accent        = accent;
        this.enabled       = enabled;
        this.disabled      = disabled;
        this.text          = text;
        this.textSecondary = textSecondary;
        this.border        = border;
    }
}
