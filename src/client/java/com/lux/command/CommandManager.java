package com.lux.command;

import com.lux.module.Module;
import com.lux.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class CommandManager {

    private static final String PREFIX = ".";
    private static CommandManager instance;

    private CommandManager() {}

    public static void init() { instance = new CommandManager(); }
    public static CommandManager getInstance() { return instance; }

    public boolean handle(String message) {
        if (!message.startsWith(PREFIX)) return false;
        String[] parts = message.substring(PREFIX.length()).trim().split("\\s+");
        if (parts.length == 0) return false;

        String cmd = parts[0].toLowerCase();
        MinecraftClient mc = MinecraftClient.getInstance();

        switch (cmd) {
            case "toggle" -> {
                if (parts.length < 2) { info("Usage: .toggle <module>"); return true; }
                String name = String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
                Module m = findModule(name);
                if (m == null) { info("Module not found: " + name); return true; }
                m.toggle();
                info(m.getName() + " " + (m.isEnabled() ? "§aenabled" : "§cdisabled"));
            }
            case "bind" -> {
                if (parts.length < 3) { info("Usage: .bind <module> <key> | .bind <module> none"); return true; }
                String name = String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length - 1));
                Module m = findModule(name);
                if (m == null) { info("Module not found: " + name); return true; }
                String keyStr = parts[parts.length - 1].toUpperCase();
                if (keyStr.equals("NONE")) {
                    m.setKeyCode(-1);
                    info("Unbound " + m.getName());
                } else {
                    try {
                        int key = org.lwjgl.glfw.GLFW.class.getField("GLFW_KEY_" + keyStr).getInt(null);
                        m.setKeyCode(key);
                        info("Bound " + m.getName() + " to " + keyStr);
                        com.lux.config.ConfigManager.getInstance().save();
                    } catch (Exception e) { info("Unknown key: " + keyStr); }
                }
            }
            case "modules" -> {
                StringBuilder sb = new StringBuilder("§6Modules: §r");
                ModuleManager.getInstance().getAll().forEach(m ->
                        sb.append(m.isEnabled() ? "§a" : "§c").append(m.getName()).append("§r, "));
                info(sb.toString());
            }
            case "help" -> {
                info("§6Lux Commands: §r.toggle, .bind, .modules, .help");
            }
            default -> info("Unknown command. Use §e.help");
        }
        return true;
    }

    private Module findModule(String name) {
        Module exact = ModuleManager.getInstance().get(name);
        if (exact != null) return exact;
        String lower = name.toLowerCase();
        return ModuleManager.getInstance().getAll().stream()
                .filter(m -> m.getName().toLowerCase().contains(lower))
                .findFirst().orElse(null);
    }

    private void info(String msg) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null)
            mc.player.sendMessage(Text.of("§7[§bLux§7] §r" + msg), false);
    }
}
