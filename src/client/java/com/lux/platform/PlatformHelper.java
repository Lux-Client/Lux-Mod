package com.lux.platform;

import java.nio.file.Path;

public interface PlatformHelper {
    Path getConfigDirectory();

    boolean isModLoaded(String modId);

    static PlatformHelper getInstance() {
        return PlatformHelperHolder.INSTANCE;
    }

    static void setInstance(PlatformHelper instance) {
        PlatformHelperHolder.INSTANCE = instance;
    }

    final class PlatformHelperHolder {
        private static PlatformHelper INSTANCE;
    }
}
