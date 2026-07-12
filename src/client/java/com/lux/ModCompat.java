package com.lux;

import com.lux.platform.PlatformHelper;

public class ModCompat {

    private static Boolean sodiumLoaded = null;
    private static Boolean optifineLoaded = null;
    private static Boolean irisLoaded = null;

    public static boolean isSodiumLoaded() {
        if (sodiumLoaded == null) {
            sodiumLoaded = PlatformHelper.getInstance() != null && PlatformHelper.getInstance().isModLoaded("sodium");
        }
        return sodiumLoaded;
    }

    public static boolean isOptiFineLoaded() {
        if (optifineLoaded == null) {
            optifineLoaded = PlatformHelper.getInstance() != null && PlatformHelper.getInstance().isModLoaded("optifine");
        }
        return optifineLoaded;
    }

    public static boolean isIrisLoaded() {
        if (irisLoaded == null) {
            irisLoaded = PlatformHelper.getInstance() != null && PlatformHelper.getInstance().isModLoaded("iris");
        }
        return irisLoaded;
    }

    public static boolean isFullbrightAvailable() {
        return !isSodiumLoaded() && !isOptiFineLoaded() && !isIrisLoaded();
    }

    public static String getFullbrightIncompatMessage() {
        if (isOptiFineLoaded()) return "OptiFine has built-in fullbright";
        if (isSodiumLoaded()) return "Sodium has built-in fullbright option";
        if (isIrisLoaded()) return "Iris (Sodium) has built-in fullbright option";
        return null;
    }
}
