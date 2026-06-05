package com.lux.config;

import com.google.gson.*;
import com.lux.module.Module;
import com.lux.module.ModuleManager;
import com.lux.module.qol.WaypointsModule;
import com.lux.setting.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.*;

public class ConfigManager {

    private static ConfigManager instance;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path configFile;
    private boolean loading = false;

    private ConfigManager() {
        configFile = FabricLoader.getInstance().getConfigDir().resolve("lux").resolve("config.json");
        try { Files.createDirectories(configFile.getParent()); } catch (IOException ignored) {}
    }

    public static void init() { instance = new ConfigManager(); }
    public static ConfigManager getInstance() { return instance; }

    public void save() {
        if (loading) return;
        JsonObject root = new JsonObject();
        JsonObject modulesObj = new JsonObject();

        for (Module m : ModuleManager.getInstance().getAll()) {
            JsonObject mod = new JsonObject();
            mod.addProperty("enabled", m.isEnabled());
            mod.addProperty("keyCode", m.getKeyCode());
            mod.addProperty("hudX", m.getHudX());
            mod.addProperty("hudY", m.getHudY());

            JsonObject settingsObj = new JsonObject();
            for (Setting<?> s : m.getSettings()) {
                Object val = s.getValue();
                if (val instanceof Boolean b) settingsObj.addProperty(s.getName(), b);
                else if (val instanceof Integer i) settingsObj.addProperty(s.getName(), i);
                else if (val instanceof Float f)   settingsObj.addProperty(s.getName(), f);
                else if (val instanceof Enum<?> e) settingsObj.addProperty(s.getName(), e.name());
                else if (val instanceof String str) settingsObj.addProperty(s.getName(), str);
            }
            mod.add("settings", settingsObj);
            modulesObj.add(m.getName(), mod);
        }

        root.add("modules", modulesObj);
        try (Writer w = Files.newBufferedWriter(configFile)) {
            GSON.toJson(root, w);
        } catch (IOException e) {
            System.err.println("[Lux] Failed to save config: " + e.getMessage());
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void load() {
        if (!Files.exists(configFile)) return;
        loading = true;
        try (Reader r = Files.newBufferedReader(configFile)) {
            JsonObject root = GSON.fromJson(r, JsonObject.class);
            if (root == null || !root.has("modules")) return;
            JsonObject modulesObj = root.getAsJsonObject("modules");

            for (Module m : ModuleManager.getInstance().getAll()) {
                if (!modulesObj.has(m.getName())) continue;
                JsonObject mod = modulesObj.getAsJsonObject(m.getName());

                m.setEnabledSilent(mod.has("enabled") && mod.get("enabled").getAsBoolean());
                if (mod.has("keyCode")) m.setKeyCode(mod.get("keyCode").getAsInt());
                if (mod.has("hudX") && mod.has("hudY"))
                    m.setHudPos(mod.get("hudX").getAsInt(), mod.get("hudY").getAsInt());

                if (!mod.has("settings")) continue;
                JsonObject settingsObj = mod.getAsJsonObject("settings");

                for (Setting<?> s : m.getSettings()) {
                    if (!settingsObj.has(s.getName())) continue;
                    JsonElement el = settingsObj.get(s.getName());
                    try {
                        if (s instanceof BooleanSetting bs) bs.setValueSilent(el.getAsBoolean());
                        else if (s instanceof IntSetting  is) is.setValueSilent(el.getAsInt());
                        else if (s instanceof FloatSetting fs) fs.setValueSilent(el.getAsFloat());
                        else if (s instanceof ColorSetting  cs) cs.setValueSilent(el.getAsInt());
                        else if (s instanceof EnumSetting es) {
                            String name = el.getAsString();
                            for (Object val : es.getValues()) {
                                if (((Enum)val).name().equals(name)) {
                                    es.setValueSilent(val);
                                    break;
                                }
                            }
                        }
                    } catch (Exception ignored) {}
                }
            }
        } catch (IOException e) {
            System.err.println("[Lux] Failed to load config: " + e.getMessage());
        } finally {
            loading = false;
        }
    }
}
