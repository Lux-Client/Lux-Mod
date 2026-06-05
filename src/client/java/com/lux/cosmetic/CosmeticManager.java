package com.lux.cosmetic;

import java.util.HashMap;
import java.util.Map;

public class CosmeticManager {

    private static CosmeticManager instance;
    private final Map<String, Object> equipped = new HashMap<>();

    private CosmeticManager() {}

    public static void init() { instance = new CosmeticManager(); }
    public static CosmeticManager getInstance() { return instance; }

    public void equip(String slot, Object cosmetic) { equipped.put(slot, cosmetic); }
    public void unequip(String slot) { equipped.remove(slot); }
    public boolean hasEquipped(String slot) { return equipped.containsKey(slot); }
    public Object getEquipped(String slot) { return equipped.get(slot); }
}
