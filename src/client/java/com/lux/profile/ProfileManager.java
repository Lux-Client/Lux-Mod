package com.lux.profile;

import com.google.gson.*;
import com.lux.config.ConfigManager;
import com.lux.module.Module;
import com.lux.module.ModuleManager;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ProfileManager {

    private static ProfileManager instance;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path profileDir;
    private final List<String> profiles = new ArrayList<>();
    private String activeProfile = "default";

    private ProfileManager() {
        profileDir = FabricLoader.getInstance().getConfigDir().resolve("lux").resolve("profiles");
        try {
            Files.createDirectories(profileDir);
            refreshList();
        } catch (IOException ignored) {}
    }

    public static void init() { instance = new ProfileManager(); }
    public static ProfileManager getInstance() { return instance; }

    private void refreshList() throws IOException {
        profiles.clear();
        try (var stream = Files.list(profileDir)) {
            stream.filter(p -> p.toString().endsWith(".json"))
                  .forEach(p -> profiles.add(p.getFileName().toString().replace(".json", "")));
        }
    }

    public void saveProfile(String name) {
        JsonObject obj = new JsonObject();
        for (Module m : ModuleManager.getInstance().getAll()) {
            JsonObject mod = new JsonObject();
            mod.addProperty("enabled", m.isEnabled());
            obj.add(m.getName(), mod);
        }
        Path file = profileDir.resolve(name + ".json");
        try (Writer w = Files.newBufferedWriter(file)) {
            GSON.toJson(obj, w);
            if (!profiles.contains(name)) profiles.add(name);
        } catch (IOException e) {
            System.err.println("[Lux] Failed to save profile: " + e.getMessage());
        }
    }

    public void loadProfile(String name) {
        Path file = profileDir.resolve(name + ".json");
        if (!Files.exists(file)) return;
        try (Reader r = Files.newBufferedReader(file)) {
            JsonObject obj = GSON.fromJson(r, JsonObject.class);
            if (obj == null) return;
            for (Module m : ModuleManager.getInstance().getAll()) {
                if (!obj.has(m.getName())) continue;
                JsonObject mod = obj.getAsJsonObject(m.getName());
                if (mod.has("enabled")) m.setEnabledSilent(mod.get("enabled").getAsBoolean());
            }
            activeProfile = name;
            ConfigManager.getInstance().save();
        } catch (IOException e) {
            System.err.println("[Lux] Failed to load profile: " + e.getMessage());
        }
    }

    public void deleteProfile(String name) {
        try {
            Files.deleteIfExists(profileDir.resolve(name + ".json"));
            profiles.remove(name);
        } catch (IOException ignored) {}
    }

    public List<String> getProfiles()     { return profiles; }
    public String getActiveProfile()      { return activeProfile; }
}
