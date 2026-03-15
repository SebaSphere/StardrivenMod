# Stardriven Architecture

This document describes the exact internal architecture of Stardriven — how classes relate, how data flows, and how each system works at the code level.

---

## Module Structure

```
Stardriven/
├── stardriven/          dev.sebastianb.stardriven     Main mod
└── terrautil/           net.terradevelopment.terrautil Reusable utility library
```

Both modules are separate Gradle subprojects. `stardriven` depends on `terrautil` as a compile dependency. TerraUtil is also a standalone Fabric mod that initializes alongside Stardriven at runtime.

---

## Key Dependencies

| Dependency | Version | Purpose |
|---|---|---|
| Minecraft | 1.21 (Yarn `1.21+build.3`) | Base game |
| Fabric Loader | 0.15.11 | Mod loading |
| Fabric API | 0.102.0+1.21 | Hooks, events, registries |
| Dynamic Dimensions | 0.8.0+254 | Runtime dimension creation/deletion |
| Veil | 1.0.0.43 | Rendering utilities (sky/dimension effects) |
| BadPackets | 0.8.1 | Networking (S2C/C2S channels) |

---

## Entry Points

### Server-side (`Stardriven.java`)
Implements `ModInitializer.onInitialize()`:
1. Calls `StardrivenAPI._init(StardrivenAPIImpl.INSTANCE)` — stores the API singleton
2. `StardrivenDimensions.register()` — registers the space chunk generator
3. `StardrivenBiomes.register()` — stub (no-op)
4. `StardrivenBlocks.register()` — registers display blocks and item group
5. `StardrivenBlockEntities.register()` — registers the display block entity
6. `StardrivenCommands.register()` — hooks `CommandRegistrationCallback`

### Client-side (`StardrivenClient.java`)
Implements `ClientModInitializer.onInitializeClient()`:
1. `SDDimensionEffects.register()` — registers sky renderer, cloud renderer, dimension effects
2. `KeybindControl.register()` — registers Z-key thrust binding
3. `BlockRenderLayerMap.INSTANCE.putBlock(DISPLAY..., TRANSLUCENT)` — both display blocks render on the translucent layer
4. `S2CNetworking.registerS2CReceiver()` — registers S2C packet handlers

---

## API Layer

The mod exposes a singleton API used internally to access ship management.

### `StardrivenAPI` (interface + static holder)
```java
StardrivenAPI.api()                    // Returns the stored StardrivenAPI instance
  .getDimensionalShipManager()         // Returns DimensionalShipManager
  .getTeamManager()                    // Returns TeamManager (stub)
```
`_init(StardrivenAPI api)` is called once in `Stardriven.onInitialize()` to inject the implementation.

### `StardrivenAPIImpl` (enum singleton, `api_impl/`)
```java
enum StardrivenAPIImpl implements StardrivenAPI {
  INSTANCE;
  getDimensionalShipManager() → DimensionalShipManagerImpl.INSTANCE
  getTeamManager()            → TeamManagerImpl.INSTANCE (stub)
}
```

### `DimensionalShip` (interface)
Represents a single ship/dimension. Core methods:
- `getName()` / `setDimensionShipName(String, MinecraftServer)`
- `getPosition()` / `setDimensionShipPosition(double x, y, z, MinecraftServer)`
- `getUUID()`, `getTeam()`, `getWorld()`
- `attachedNBT()` — returns the ship's `NbtFileIO` instance

Extends `Comparable<DimensionalShip>` for TreeSet ordering.

### `DimensionalShipManager` (interface)
CRUD for ships:
- `init(ServerWorld, UUID, boolean)` — load or register an existing ship
- `createDimensionalShip(ServerWorld, UUID, String)` — create a new ship entry
- `deleteDimensionalShip(UUID)` — TODO, not implemented
- `getDimensionalShip(UUID)` — single lookup
- `getAllDimensionalShips()` — returns `TreeMap<UUID, DimensionalShip>`

---

## Ship Management Implementation

### `DimensionalShipManagerImpl` (enum singleton)
```java
TreeMap<UUID, DimensionalShip> dimensionalShips
NbtFileIO nbt  // Master ships index (not per-ship)
```

**`init(ServerWorld world, UUID uuid, boolean existingShip)`**
Called during server startup (mixin) and on ship creation. If `existingShip=true`, reads NBT from disk. Adds the ship to `dimensionalShips`.

**`createDimensionalShip(ServerWorld world, UUID uuid, String name)`**
Creates a `DimensionalShipImpl`, assigns name/uuid/world, initializes its `NbtFileIO`, writes initial NBT to disk, tracks the file.

**`loadShipNBT(DimensionalShip)`**
Serializes the ship's current state (position, name, uuid) to its `NbtFileIO` tag, then writes to disk.

### `DimensionalShipImpl`
```java
DimensionalStarPosition position   // x, y, z, pitch, roll, yaw
UUID shipID
String name
NbtFileIO nbtFileIO                // Path: dimensions/stardriven/interstellar-ship_{UUID}/ship/{UUID}.nbt
ServerWorld world
```

**`setDimensionShipPosition(x, y, z, server)`**
Updates `position`, calls `DimensionalShipManagerImpl.loadShipNBT(this)` to persist, then iterates every player in the ship's world and sends an `INTERSTELLAR_SKYBOX_POSITION_UPDATE` packet containing UUID + new coordinates.

**`setDimensionShipName(name, server)`**
Updates `name`, calls `reloadNBT()` to re-read from disk and re-write.

**`compareTo(DimensionalShip)`**
Compares by UUID string — enables TreeSet/TreeMap ordering.

### `DimensionalStarPosition`
Plain data holder: `double x, y, z, pitch, roll, yaw`.
`setCameraPositions(x, y, z)` updates the three spatial fields.
`getVec3d()` returns a `Vec3d`.

---

## Dimension System

### `StardrivenDimensions`
```java
SPACE_DIMENSION_KEY   // Identifier "stardriven:space"
SPACE_WORLD_KEY       // RegistryKey<World> for the static space world
```
Registers `SpaceChunkGenerator`'s codec into the dimension type registry.

### `SpaceChunkGenerator` (extends `ChunkGenerator`)
A fully empty/void chunk generator:
- `populateNoise()` → returns chunk unchanged
- `buildSurface()`, `carve()`, `generateFeatures()` → no-ops
- `getHeight()` → always 0
- `getColumnSample()`, `addDebugScreenInfo()` → stubs
- Uses `FixedBiomeSource` with `StardrivenBiomes.SPACE`

### `ShipCreationUtils`
`createOrLoadShipWorld(RegistryEntryLookup<Biome>, DynamicDimensionRegistry, UUID)`:
1. Creates `SpaceChunkGenerator`
2. Builds a `DimensionType` with: no time cycle, has skylight, no cave effect, height 0–256, no mob spawning, dimension effect `stardriven:interstellar_sky`
3. Calls `dynamicDimensionRegistry.loadDynamicDimension("stardriven:interstellar-ship_{UUID}", dimensionType, generator)`
4. Returns the `ServerWorld`

The world key pattern `stardriven:interstellar-ship_{UUID}` is used throughout the codebase to identify ship dimensions.

---

## Command System

Commands are registered via `CommandRegistrationCallback.EVENT` under three aliases: `stardriven`, `s`, `sd`. Each alias registers the same `BaseShipCommand` subtree.

### `ICommand` (interface)
```java
String commandName()
LiteralArgumentBuilder<ServerCommandSource> registerNode()
```

### `BaseShipCommand`
Root literal `"ship"` with subcommands added as children:
```
ship
  create [name:String] [x:Double] [y:Double] [z:Double]
  move   <uuid:String> <x:Double> <y:Double> <z:Double>
  list
  teleport  (stub)
```
Requires permission level 2.

### `CreateShipCommand`
`execute(context)`:
1. Reads `name`, `x`, `y`, `z` from arguments
2. Calls `ShipCreationUtils.createOrLoadShipWorld(...)` with a new `UUID`
3. Calls `DimensionalShipManagerImpl.createDimensionalShip(world, uuid, name)`
4. Calls `ship.setDimensionShipPosition(x, y, z, server)`
5. Teleports player into the new dimension
6. Places glowstone at the player's feet

### `MoveShipCommand`
`execute(context)`:
1. Parses UUID from string argument (with UUID autocomplete suggestions)
2. Looks up ship via `DimensionalShipManager.getDimensionalShip(uuid)`
3. Calls `ship.setDimensionShipPosition(x, y, z, server)` — this persists and broadcasts

### `ShipListCommand`
Iterates `getAllDimensionalShips().values()`, sends a chat message per ship showing name and position.

---

## Block System

### `StardrivenBlocks`
```java
enum DisplayBlocks implements BlockRegistry {
  DISPLAY,         // DisplayBlock (no block entity)
  DISPLAY_ENTITY   // DisplayWithEntity (has block entity)
}
```
`register()` creates a `ModRegistry`, registers both blocks, and creates an item group.

### `DisplayBlock` (extends `Block`)
A display panel block with three block state properties:
- `FACING` (all 6 directions) — which face the display is mounted on
- `DISPLAY_PIECE` (CORNER, EDGE, SINGLE, THREE_EDGE, EMPTY, TWO_SIDE) — shape within the display grid
- `DISPLAY_ROTATION` (R0, R90, R180, R270) — visual rotation

The outline shape is a thin 2-pixel-thick slice matching the facing direction. Shapes are pre-computed per facing.

**`getPlacementState()`** — determines initial facing from player look.
**`onPlaced()`** — calls `DisplayUtils.getConnectedDisplays()` to find neighbors, then `DisplayUtils.getUpdatedState()` to set the correct piece type.
**`onBreak()`** — if shift-clicking, removes the entire connected display group.
**`onUse()`** — shift-click lists all connected display positions.

### `DisplayWithEntity` (extends `DisplayBlock`, implements `BlockEntityProvider`)
Overrides `createBlockEntity()` to return a `DisplayBlockEntity`. When a connected group is detected, converts block states to the entity variant via `stateWithEntity()`.

### `DisplayBlockEntity` (extends `BlockEntity`)
```java
ArrayList<BlockPos> connectedDisplays  // All positions in the display grid
DisplayBounds bounds                   // min/max corner of the grid
Direction facing
```
````
**`tryConnectDisplay()`** — attempts to expand the display by checking neighbors.
**`updateDisplays()`** — flood-fills all connected display blocks, updates `connectedDisplays` and `bounds`.
**`handleRemoval()`** — when a block is broken, splits the remaining display into segments, creates new screen entities for each segment.
**`createNewScreen(DisplayBounds)`** — creates a new `DisplayBlockEntity` for a sub-region.
**`updateBlockstates()`** — iterates all connected positions, calls `DisplayUtils.getUpdatedState()` for each, sets the correct `DISPLAY_PIECE` + `DISPLAY_ROTATION` state.
**`getSize()`** — returns `(max - min)` per axis.

**Persistence**: `writeNbt`/`readNbt` serialize `connectedDisplays` and `bounds`. `toUpdatePacket` returns `BlockUpdateS2CPacket`; `toInitialChunkDataNbt` returns NBT for chunk loading.

### `DisplayUtils`
Static helpers for display grid logic:
- `getUpdatedState(world, pos, facing)` — inspects orthogonal neighbors to pick the `DISPLAY_PIECE` enum value
- `rotationFromFacingAndDirection(facing, dir)` — large switch: returns `DISPLAY_ROTATION` given the block facing and a direction
- `getPossibleDirections(facing)` — returns the two axes perpendicular to `facing`
- `getConnectedDisplays(world, origin, facing)` — recursive flood-fill to find all connected display blocks on the same plane
- `getAdjacentDisplays(world, pos, facing)` — single-step adjacency check
- `getConnectedBlockEntities(world, positions)` — returns block entities for a set of positions
- `getBlocksBetweenMinMax(min, max, facing)` — enumerates all positions in a rectangular region

---

## Rendering System

### `SDDimensionEffects`
Called in `StardrivenClient`. Registers three things under `SPACE_WORLD_KEY`:
1. `DimensionRenderingRegistry.registerDimensionEffects(SPACE, SpaceDimensionEffect.INSTANCE)`
2. `DimensionRenderingRegistry.registerCloudRenderer(SPACE_WORLD_KEY, EmptyCloudRenderer.INSTANCE)`
3. `DimensionRenderingRegistry.registerSkyRenderer(SPACE_WORLD_KEY, SpaceSkyRenderer.INSTANCE)`

### `SpaceDimensionEffect` (extends `DimensionEffects`)
Configuration: `SkyType.NONE`, no thick fog, fog color `(0, 0, 0, 0)`.
- `adjustFogColor()` → `Vec3d.ZERO`
- `useThickFog()` → `false`
- `getFogColorOverride()` → `{0, 0, 0, 0}` (pure black)

### `EmptyCloudRenderer` (enum singleton)
Implements `DimensionRenderingRegistry.CloudRenderer`. `render()` is a no-op.

### `SpaceSkyRenderer`
```java
static SpaceSkyRenderer INSTANCE
GalaxyStarRendererManager starRendererManager      // the star field
static HashMap<String, DimensionalStarPosition> shipPositions  // per-world camera positions
```

**`render(context, tickDelta, matrices, camera, frustumIntersection, vanilla)`** (Fabric sky renderer):
1. Clears the background black
2. Looks up the current world's ship position from `shipPositions`
3. Calls `starRendererManager.setRelativeCameraRenderPosition(pos)`
4. Calls `starRendererManager.setupBufferPositions()` — rebuilds the vertex buffer
5. Calls `starRendererManager.render(matrices)` — draws the buffer

**`updateShipPosition(worldId, uuid, x, y, z)`** — called by S2C packet receiver; updates `shipPositions.put(worldId, ...)`.

### `GalaxyStarRendererManager`
Contains a `HashSet<GalaxyStar>` of 20,000 stars.

**Star generation (`setStarPositions()`):**
- Seed: `27893L`
- Each star: random position in `[-850, +850]³`, random size `[0.3, 1.3]`, random rotation `[0, 360]`

**`setupBufferPositions()`** — runs every frame:
1. Gets `Tessellator` and begins `QUADS` with `POSITION` vertex format
2. For each `GalaxyStar`:
   - Computes position relative to camera
   - Calls `getModifiedStarSize(distance)` for LOD
   - Skips stars where size returns 0 (culled beyond 600 units)
   - Builds a billboard quad (4 vertices) rotated by the star's rotation angle
3. Uploads result to `starBuffer` via `VertexBuffer.upload()`

**`getModifiedStarSize(distance)`** — distance-based LOD:
- `< 60` → fades toward 0 (too close)
- `60–320` → returns normal star size
- `320–600` → linearly enlarges
- `> 600` → returns 0 (culled)

**`render(matrices)`** — binds `starBuffer`, sets shader to `GameRenderer.getPositionProgram()`, draws with current matrix stack.

---

## Networking System

### `StardrivenNetworking`
Defines two static `Identifier`s using BadPackets' `PlayPackets.registerS2C()`:
- `INTERSTELLAR_SKYBOX_INITIALIZER` — tells client to register sky renderer for this world
- `INTERSTELLAR_SKYBOX_POSITION_UPDATE` — updates camera position for sky rendering

### `S2CNetworking` (client-side receiver registration)
`registerS2CReceiver()` uses BadPackets' `S2CPlayPacket.register()`:

**`INTERSTELLAR_SKYBOX_INITIALIZER`** handler:
- Registers `SpaceSkyRenderer.INSTANCE` for the current world via `DimensionRenderingRegistry.registerSkyRenderer()`

**`INTERSTELLAR_SKYBOX_POSITION_UPDATE`** handler:
- Reads `UUID`, `x`, `y`, `z` from `PacketByteBuf`
- Calls `SpaceSkyRenderer.updateShipPosition(worldId, uuid, x, y, z)`

---

## Mixin System

### `MinecraftServerMixin` (@Mixin `MinecraftServer`)
**`@Inject runServer()` after `setupServer()`** — server startup hook:
1. Scans the `dimensions/stardriven/` folder for directories matching `interstellar-ship_*`
2. For each found folder, extracts the UUID from the folder name
3. Calls `loadDimension(server, uuid)`:
   - Gets `DynamicDimensionRegistry` from the server
   - Calls `ShipCreationUtils.createOrLoadShipWorld(biomeRegistry, dynamicRegistry, uuid)` — recreates the dynamic dimension
   - Calls `DimensionalShipManagerImpl.init(world, uuid, true)` — loads NBT from disk

### `ServerWorldMixin` (@Mixin `ServerWorld`)
**`@Inject` on dimension changed / player connected** — detects when a player enters a ship dimension:
- Checks if world key starts with `"stardriven:interstellar-ship_"`
- Sends `INTERSTELLAR_SKYBOX_INITIALIZER` via `PacketSender.s2c().send()` to that player

### `PlayerMixin` (@Mixin `ServerPlayerEntity`)
**`@Inject tick()` @HEAD** — runs every server tick for each player:
1. Checks if the player is in a dimension whose key starts with `"stardriven:interstellar-ship_"`
2. Extracts the ship UUID from the dimension key
3. If the player is **sneaking**:
   - Gets the player's look vector
   - Adds `lookVec * 0.25` to the ship's current position
   - Calls `ship.setDimensionShipPosition(...)` — persists and broadcasts

### Other Mixins (rendering optimizations, client-side)
- `AmbientOcclusionFaceMixin`, `BlockModelRendererMixin`, `BlockRenderManagerMixin`, `ChunkBuilderRendererMixin` — low-level rendering patches
- `BakedQuadAccessor` — accessor mixin for reading baked quad data
- `SDWorldRendererMixin` — currently a stub/commented out

---

## Persistence (TerraUtil `NbtFileIO`)

### `NbtFileIO` (interface)
Key methods:
- `setHeaderPath(Path)` — base directory (e.g., the world save folder)
- `setWorkingPath(String)` — subdirectory relative to header; creates directories
- `setFileIdentifier(String)` — filename prefix
- `writeNbtToFile(NbtCompound)` — calls `NbtIo.write(tag, workingPath/id.nbt)`
- `readNbtFromFile()` — calls `NbtIo.read(workingPath/id.nbt)`
- `setFileTag(NbtCompound)` / `getFileTag()` — in-memory tag access
- `trackFile(NbtFileIO)` / `untrackFile(NbtFileIO)` — static registry of files to autosave

### `NbtFileIOImpl`
Concrete implementation. `setWorkingPath(String)` resolves the path relative to `headerPath` and calls `Files.createDirectories()`.

### Autosave via `TerraUtilMixin`
`@Inject` into `MinecraftServer.saveAll()` before `saveAllPlayerData()`:
- Iterates all tracked `NbtFileIO` instances
- For each: verifies directory exists, writes the file, reads it back (verification), logs debug info

### Ship NBT Layout
```
dimensions/stardriven/interstellar-ship_{UUID}/ship/{UUID}.nbt
  shipId        (UUID as string)
  shipName      (String)
  shipPosition
    x           (Double)
    y           (Double)
    z           (Double)
    pitch       (Double)
    roll        (Double)
    yaw         (Double)
```

---

## Data Flow Diagrams

### Ship Creation
```
/s ship create "Voyager" 100 200 300
  └─ CreateShipCommand.execute()
       ├─ ShipCreationUtils.createOrLoadShipWorld(biomeReg, dynDimReg, UUID)
       │    └─ DynamicDimensionRegistry.loadDynamicDimension("stardriven:interstellar-ship_{UUID}", ...)
       │         └─ ServerWorld (void space, FixedBiomeSource→SPACE biome)
       ├─ DimensionalShipManagerImpl.createDimensionalShip(world, uuid, "Voyager")
       │    └─ DimensionalShipImpl initialized + NbtFileIO tracked + NBT written to disk
       ├─ ship.setDimensionShipPosition(100, 200, 300, server)
       │    └─ NbtFileIO updated + S2C packets sent (no players yet)
       └─ Player teleported to new dimension + glowstone placed
```

### Player Movement (Sneaking)
```
[Server tick]
  └─ PlayerMixin.tick()
       ├─ Player in "stardriven:interstellar-ship_{UUID}" dimension?  YES
       ├─ Player sneaking?  YES
       └─ DimensionalShipImpl.setDimensionShipPosition(pos + lookVec * 0.25, server)
            ├─ DimensionalShipManagerImpl.loadShipNBT(ship)
            │    └─ NbtFileIO.writeNbtToFile(...)
            └─ For each player in world:
                 └─ PacketSender.s2c().send(INTERSTELLAR_SKYBOX_POSITION_UPDATE, buf{UUID, x, y, z})
```

### Client Sky Rendering
```
[Client render frame in ship dimension]
  └─ SpaceSkyRenderer.render()
       ├─ Clears background black
       ├─ Reads shipPositions[worldId] → DimensionalStarPosition
       ├─ starRendererManager.setRelativeCameraRenderPosition(pos)
       ├─ starRendererManager.setupBufferPositions()
       │    └─ For 20,000 stars: compute billboard quads → upload to VertexBuffer
       └─ starRendererManager.render(matrices)
            └─ VertexBuffer.draw(getPositionProgram)
```

### Server Restart / Ship Loading
```
[MinecraftServer.runServer()]
  └─ MinecraftServerMixin (after setupServer)
       └─ Scan dimensions/stardriven/ for interstellar-ship_* folders
            └─ For each folder:
                 ├─ ShipCreationUtils.createOrLoadShipWorld(...)  [recreate dynamic dim]
                 └─ DimensionalShipManagerImpl.init(world, uuid, existingShip=true)
                      └─ NbtFileIO.readNbtFromFile()  [load position/name from disk]
```

---

## TerraUtil Library

### `ModRegistry`
Thin wrapper over Fabric registry APIs. Used by `StardrivenBlocks` and `StardrivenBlockEntities`.
```java
ModRegistry reg = new ModRegistry("stardriven");
reg.block(block, settings, "display")            // registers block + block item
reg.blockEntity(DisplayBlockEntity::new, "display_entity", block)
```

### `BlockRegistry` / `ItemRegistry`
Marker interfaces implemented by the `DisplayBlocks` enum. Provides `asBlock()`, `asItem()`, `asStack()`, `getStack(n)`.

### `NbtUtils`
```java
NbtUtils.putBlockPos(compound, "key", pos)  // writes x/y/z as IntTag
NbtUtils.getBlockPos(compound, "key")        // reads back as BlockPos
```

### `MCWrapperUtils`
```java
MCWrapperUtils.id("stardriven", "space")   // → Identifier.of("stardriven", "space")
MCWrapperUtils.id("space")                  // → Identifier.of(Stardriven.MOD_ID, "space")
```

---

## Access Wideners

`stardriven.accesswidener` opens:
- `ChunkGenerator.trySetStructureStart` — used in space chunk generator
- `PlaceCommand.throwOnUnloadedPos` — used in ship creation/placement
- `BlockStateVariantMap.register` — used for display block model variants
- `ChunkBuilder.BuiltChunk.RebuildTask` (class) — rendering optimization
- `ClientWorld.worldRenderer` (field) — client rendering access

---

## Known Limitations / TODOs in Code

| Location | Issue |
|---|---|
| `DimensionalShipManagerImpl` | `deleteDimensionalShip()` not implemented |
| `DimensionalShipImpl` | Position packets sent to ALL players in world, not just those in ship |
| `DisplayBlock` | Rotations broken since 1.21 port |
| `ShipTeleportCommand` | Stub — not implemented |
| `ThrustThrottleControlEvent` | Only prints "Is pressed"; no actual ship control |
| `TeamManager` / `TeamImpl` | Stubs — team system not built out |
| `TerraUtilMixin` | TODO: also inject into `createLevels` for full save coverage |
