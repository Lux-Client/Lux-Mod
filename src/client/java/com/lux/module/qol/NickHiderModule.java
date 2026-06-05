package com.lux.module.qol;

import com.lux.module.Category;
import com.lux.module.Module;
import com.lux.setting.BooleanSetting;

public class NickHiderModule extends Module {
    private String nick = "Steve";
    private BooleanSetting hideInTab, hideInChat;

    public NickHiderModule() { super("Nick Hider", "Replaces your username with a custom nick locally", Category.QUALITY_OF_LIFE); }

    @Override protected void init() {
        hideInTab  = register(new BooleanSetting("Hide in Tab", "Hide real name in tab list", true));
        hideInChat = register(new BooleanSetting("Hide in Chat", "Replace name in chat", true));
    }

    public String getNick()           { return nick; }
    public void setNick(String nick)  { this.nick = nick; }
    public boolean hidesInTab()       { return isEnabled() && hideInTab.getValue(); }
    public boolean hidesInChat()      { return isEnabled() && hideInChat.getValue(); }
}
