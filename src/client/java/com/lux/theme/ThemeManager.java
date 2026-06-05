package com.lux.theme;

import java.util.LinkedHashMap;
import java.util.Map;

public class ThemeManager {

    private static ThemeManager instance;
    private final Map<String, Theme> themes = new LinkedHashMap<>();
    private Theme active;

    private ThemeManager() {
        register(new Theme("Dark",
                0xFF0D0F12, 0xFF181A1E, 0xFF4E9FE2, 0xFF4EE2A1, 0xFFE25C5C,
                0xFFF0F2F5, 0xFFA0A5B0, 0xFF2A2D33));
        register(new Theme("Midnight",
                0xFF080B10, 0xFF0F1320, 0xFF7B5CF0, 0xFF7B5CF0, 0xFFE25C5C,
                0xFFEEEEFF, 0xFF8888AA, 0xFF1E2234));
        register(new Theme("Ocean",
                0xFF0A1628, 0xFF0F2040, 0xFF00B4D8, 0xFF48CAE4, 0xFFE25C5C,
                0xFFEEF6FF, 0xFF6CA0BC, 0xFF1A3A5C));
        register(new Theme("Sakura",
                0xFF1A0F12, 0xFF2A1820, 0xFFFF6B9D, 0xFFFF85A1, 0xFFFF4D4D,
                0xFFFFEEF3, 0xFFCC8899, 0xFF3D1E28));
        register(new Theme("Forest",
                0xFF0A1208, 0xFF0F1E0C, 0xFF4CAF50, 0xFF66BB6A, 0xFFEF5350,
                0xFFE8F5E9, 0xFF81C784, 0xFF1B3A1A));

        active = themes.get("Dark");
    }

    private void register(Theme t) { themes.put(t.name, t); }

    public static void init() { instance = new ThemeManager(); }
    public static ThemeManager getInstance() { return instance; }

    public Theme getActive()                { return active; }
    public void setActive(String name)      { if (themes.containsKey(name)) active = themes.get(name); }
    public Map<String, Theme> getThemes()   { return themes; }
}
