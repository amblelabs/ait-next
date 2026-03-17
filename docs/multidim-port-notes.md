# MultiDim Fabric 1.21.1 port notes

This port lives in the Fabric module and is meant to get the library back into a usable state on 1.21.1 without dragging a lot of extra behavior in with it.

## What was moved over

The original `mc-multidim` code was brought into `fabric/src/main/java/dev/amble/lib/multidim` and updated to the current naming and registry APIs used by this project.

The big mechanical changes were:

- `ServerWorld` -> `ServerLevel`
- `Identifier` -> `ResourceLocation`
- `RegistryKey` -> `ResourceKey`
- `DimensionOptions` -> `LevelStem`
- `SimpleRegistry` -> `MappedRegistry`

On top of that, the server and registry mixins were rewritten around the 1.21.1 internals that AIT is already compiling against.

## What is wired right now

There is a small Fabric-only demo integration in `dev.amble.ait.fabric.multidim.AitMultiDimDemo`.

It registers one blueprint on server start:

- blueprint id: `ait:void_demo`
- world id: `ait:demo_void`

The demo world uses the overworld dimension type and a void chunk generator. A small stone platform is placed at the shared spawn when the world is created or loaded so the test world is actually usable.

## Commands

The demo is reachable through the existing `/ait` command root:

- `/ait multidim demo info`
- `/ait multidim demo create`
- `/ait multidim demo tp`
- `/ait multidim demo unload`
- `/ait multidim demo remove`

The idea here is to keep the first integration boring and obvious. If these commands work, the port is doing the important things correctly:

- blueprint registration
- dynamic level creation
- server level insertion/removal
- save hook
- persistent metadata write/read

One practical detail from runtime testing: after a restart, `ait:demo_void` is present as a regular `ServerLevel` in this environment. The demo command path was adjusted to handle both `MultiDimServerLevel` and plain `ServerLevel` safely so repeated boots do not crash command usage.

## Current scope

This is intentionally not a full gameplay feature yet. It is a working port with one demo blueprint and one command path so the library can be exercised inside AIT instead of just compiling on its own.

That gives us a clean base for the next step, which would be wiring real AIT content into a custom interior or utility dimension instead of a plain void test world.

## Next obvious follow-up

Once this settles, the next sensible move is to replace the demo blueprint with something AIT-specific:

- a staging/testing interior dimension
- a dedicated utility space for content previews
- or a proper TARDIS-related dimension flow if that ends up fitting the current architecture better


