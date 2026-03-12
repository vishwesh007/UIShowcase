# UI Workspace Memory

## Overview
This workspace contains Android UI reference projects and the awesome-android-ui curated list.

## Workspace Structure
```
C:\Users\vishw\all_tools\UI\
├── alwaysdo.instructions.md    # MANDATORY - always read first. Contains workflow rules.
├── INSTRUCTIONS.txt            # Live communication file - agent polls this for user requests
├── MEMORY.md                   # THIS FILE - context for future sessions
├── awesome-android-ui/         # Cloned from github.com/wasabeef/awesome-android-ui
├── android-flutter/            # Flutter-based Android UI projects
├── android-jetpack-compose/    # Jetpack Compose UI projects
├── android-kotlin-multiplatform/ # Kotlin Multiplatform UI projects
└── .vscode/
    ├── mcp.json                # MCP servers (Figma MCP Bridge configured)
    └── settings.json           # VS Code settings, alwaysdo.instructions.md hooked
```

## MCP Servers Configured
- **figma-bridge**: Free Figma MCP using `@gethopp/figma-mcp-bridge`
  - No API key needed (bypasses Figma API rate limits via plugin + WebSocket)
  - User must install Figma plugin from: https://github.com/gethopp/figma-mcp-bridge/releases
  - In Figma: Plugins > Development > Import plugin from manifest

## Key Rules
- Always read `alwaysdo.instructions.md` first
- Always check `INSTRUCTIONS.txt` for live user requests
- Research before coding (web search → GitHub reference → implement → test)
- Push to GitHub after successful builds

## Animated Blob Menu Project
- **Path**: `android-jetpack-compose/animated-blob-menu/`
- **Package**: `com.ui.animatedmenu`
- **Build**: Gradle 8.5, Kotlin 1.9.22, AGP 8.2.0, Compose BOM 2024.01.00
- **Components (all verified on device)**:
  - `BlobBottomNavigation.kt` — Main screen + organic blob bottom nav (Home/Fire/Settings)
  - `AnimatedAppBar.kt` — Dark app bar with breathing wave Canvas decoration
  - `AnimatedSidebar.kt` — Multi-phase cascading reveal sidebar, breathing organic edge, staggered items
  - `AnimatedExpandableFab.kt` — Expandable FAB with 4 spring-animated colored options
  - `MainActivity.kt` — Entry point with edge-to-edge
  - `Theme.kt` — Amber (#FFC107) / Beige (#CDBBA7) / Dark (#1A1A1A) color scheme
- **Device**: ADB `9fa23325`, 1080x2246 @ 440dpi (2.75x), MIUI gesture nav
- **MIUI Notes**: Must set `forceDarkAllowed=false` in manifest + themes.xml

## ADB Tap Coordinates (440dpi device, 2.75x scale)
- Hamburger menu: x=110, y=188
- FAB center: x=932, y=1861
- Bottom nav Home: ~x=230, y=2100
- Bottom nav Fire: ~x=540, y=2100
- Bottom nav Settings: ~x=850, y=2100
