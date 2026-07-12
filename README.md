# Lux Client

A lightweight Minecraft client mod for Fabric.

## Features

- **Title Screen**: Custom "Lux" branding with green theme
- **Splash Screen**: Custom gradient background with animated progress bar
- **HUD**: FPS, CPS, Armor Status, Ping, Potion Effects (toggle with Right Shift)
- **Fullbright**: Built-in fullbright with Sodium/OptiFine/Iris compatibility

## Supported Loaders

| Loader | MC Version | Status |
|--------|------------|--------|
| Fabric | 1.21.1 | ✅ Supported |
| Quilt | 1.21.1 | ✅ Via Fabric compatibility |

## Build

```bash
./gradlew build
```

Output: `fabric/build/libs/lux-fabric-1.0.0.jar`

## Compatibility

**Known incompatible mods:**
- Lunar Client, Badlion Client
- FPS Reducer
- Custom loading screen mods

**Compatible:**
- Sodium, OptiFine, Iris (Fullbright auto-disables)