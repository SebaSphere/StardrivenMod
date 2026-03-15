# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Stardriven is a **Minecraft Fabric mod** (Java 17, Minecraft 1.21) that adds dimensional space ships — players can create and navigate space-themed dimensions. It's a multi-module Gradle project:
- `stardriven/` — Main mod
- `terrautil/` — Ported utility library (registry helpers, NBT file I/O)

## Common Commands

```bash
./gradlew build                     # Build all modules
./gradlew :stardriven:runClient     # Launch Minecraft client with the mod
./gradlew :stardriven:runServer     # Launch Minecraft server with the mod
./gradlew :stardriven:genSources    # Generate Minecraft sources for IDE navigation
```

There are no automated tests in this project.

## Architecture

### API / Implementation Separation

The mod exposes a clean public API via a singleton:
- `StardrivenAPI` (in `api/`) — entry point, holds `DimensionalShipManager` and `TeamManager`
- Concrete implementations live in `api_impl/` and are not exposed directly

Ship data is persisted via NBT; `NbtFileIO` (in `terrautil`) handles reading/writing NBT files to disk.

### Key Systems

**Dimensional Ships** (`api/ship/`, `api_impl/ship/`)
- `DimensionalShip` — represents one ship (wraps a Fabric dynamic dimension)
- `DimensionalShipManager` — CRUD for ships; ships stored in a `DimensionalShipSet` (TreeSet)
- Ships are created/moved via `ShipCreationUtils` and the Dynamic Dimensions library

**Commands** (`command/`)
- Registered under aliases `/stardriven`, `/s`, `/sd`
- `ICommand` interface — each command implements `register(CommandDispatcher)`
- Admin commands in `command/ship/admin/`: `CreateShipCommand`, `ShipListCommand`, `MoveShipCommand`, `ShipTeleportCommand`

**Client-side Rendering** (`client/render/`)
- Custom space sky renderer: `SpaceSkyRenderer` + `GalaxyStarRendererManager`
- Dimension effects registered in `StardrivenClient` via `SDDimensionEffects`
- Mixins in `mixin/` patch vanilla render paths for optimization

**Networking** (`networking/`)
- `S2CNetworking` — server-to-client packets (registered on client in `StardrivenClient`)
- Uses Fabric Networking API + BadPackets library

**Blocks** (`block/`)
- `DisplayBlock` / `DisplayWithEntity` — custom display blocks with block entity state
- Registered in `StardrivenBlocks`; render layers set in `StardrivenClient`

### Entry Points

| Class | Interface | Role |
|---|---|---|
| `Stardriven` | `ModInitializer` | Server-side init: registers dimensions, biomes, blocks, entities, commands, API |
| `StardrivenClient` | `ClientModInitializer` | Client-side init: rendering, keybindings, networking receivers |

### terrautil

Provides reusable helpers used by `stardriven`:
- `ModRegistry`, `BlockRegistry`, `ItemRegistry`, `ClientRegistry` — thin wrappers over Fabric's registry API
- `NbtFileIO` — saves/loads NBT `NbtCompound` to disk (used for ship persistence)
- `MinecraftServerMixin` — exposes server instance via duck interface

## Key Dependencies (gradle.properties)

- Minecraft 1.21 / Yarn mappings `1.21+build.3`
- Fabric Loader 0.15.11 / Fabric API 0.102.0+1.21
- Dynamic Dimensions 0.8.0+254 — runtime dimension creation/deletion
- Veil 1.0.0.43 — rendering utilities
- BadPackets 0.8.1 — networking helpers
