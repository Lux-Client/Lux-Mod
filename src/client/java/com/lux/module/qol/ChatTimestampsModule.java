package com.lux.module.qol;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.BooleanSetting;
import com.lux.setting.EnumSetting;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ChatTimestampsModule extends Module {
    public enum Format { HH_MM, HH_MM_SS }
    private EnumSetting<Format> format;
    private BooleanSetting brackets;

    public ChatTimestampsModule() { super("Chat Timestamps", "Prepends timestamps to chat messages", Category.QUALITY_OF_LIFE); }

    @Override protected void init() {
        format   = register(new EnumSetting<>("Format", "Timestamp format", Format.HH_MM));
        brackets = register(new BooleanSetting("Brackets", "Wrap timestamp in brackets", true));
    }

    public String stamp() {
        DateTimeFormatter fmt = format.getValue() == Format.HH_MM
                ? DateTimeFormatter.ofPattern("HH:mm")
                : DateTimeFormatter.ofPattern("HH:mm:ss");
        String ts = LocalTime.now().format(fmt);
        return brackets.getValue() ? "[" + ts + "] " : ts + " ";
    }
}
