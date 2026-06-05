package com.lux.module;

import com.lux.module.cosmetic.CapeModule;
import com.lux.module.cosmetic.EmoteModule;
import com.lux.module.cosmetic.WingsModule;
import com.lux.module.performance.*;
import com.lux.module.qol.*;
import com.lux.module.render.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModuleManager {

    private static ModuleManager instance;
    private final Map<String, Module> modules = new LinkedHashMap<>();

    private ModuleManager() {
        register(
            // Render
            new FPSModule(),
            new CPSModule(),
            new CoordinatesModule(),
            new KeystrokesModule(),
            new ArmorStatusModule(),
            new PotionStatusModule(),
            new ZoomModule(),
            new FullbrightModule(),
            new CrosshairModule(),
            new HitColorModule(),
            new DamageTintModule(),
            new NametagModule(),
            new FreelookModule(),
            new ItemPhysicsModule(),
            new MotionBlurModule(),
            // QoL
            new ToggleSprintModule(),
            new ToggleSneakModule(),
            new AutoGGModule(),
            new AutoTipModule(),
            new ChatFilterModule(),
            new ChatTimestampsModule(),
            new NickHiderModule(),
            new ItemCounterModule(),
            new WaypointsModule(),
            // Cosmetics
            new CapeModule(),
            new WingsModule(),
            new EmoteModule(),
            // Performance
            new FPSOptModule(),
            new EntityCullingModule(),
            new ParticleOptModule(),
            new ChunkOptModule(),
            new MemoryOptModule()
        );
    }

    private void register(Module... mods) {
        for (Module m : mods) modules.put(m.getName(), m);
    }

    public static void init() { instance = new ModuleManager(); }
    public static ModuleManager getInstance() { return instance; }

    @SuppressWarnings("unchecked")
    public <T extends Module> T get(Class<T> clazz) {
        return (T) modules.values().stream()
                .filter(m -> m.getClass() == clazz)
                .findFirst().orElse(null);
    }

    public Module get(String name) { return modules.get(name); }

    public List<Module> getAll() { return new ArrayList<>(modules.values()); }

    public List<Module> getByCategory(Category cat) {
        List<Module> list = new ArrayList<>();
        modules.values().forEach(m -> { if (m.getCategory() == cat) list.add(m); });
        return list;
    }

    public void onTick() {
        modules.values().forEach(m -> { if (m.isEnabled()) m.onTick(); });
    }
}
