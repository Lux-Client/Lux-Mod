package com.lux.module.qol;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.BooleanSetting;

import java.util.ArrayList;
import java.util.List;

public class ChatFilterModule extends Module {
    private final List<String> filters = new ArrayList<>(List.of("You are AFK", "Welcome to"));
    private BooleanSetting caseSensitive;

    public ChatFilterModule() { super("Chat Filter", "Hides chat messages matching patterns", Category.QUALITY_OF_LIFE); }

    @Override protected void init() {
        caseSensitive = register(new BooleanSetting("Case Sensitive", "Case-sensitive matching", false));
    }

    public boolean shouldFilter(String msg) {
        if (!isEnabled()) return false;
        String m = caseSensitive.getValue() ? msg : msg.toLowerCase();
        return filters.stream().anyMatch(f -> m.contains(caseSensitive.getValue() ? f : f.toLowerCase()));
    }

    public List<String> getFilters() { return filters; }
    public void addFilter(String f)  { filters.add(f); }
    public void removeFilter(String f) { filters.remove(f); }
}
