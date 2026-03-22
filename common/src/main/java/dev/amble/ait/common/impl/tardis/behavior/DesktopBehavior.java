package dev.amble.ait.common.impl.tardis.behavior;

import dev.amble.ait.api.tardis.ServerTardis;
import dev.amble.ait.api.tardis.event.desktop.DesktopPlacementEvents;
import dev.amble.ait.common.blocks.DoorBlock;
import dev.amble.ait.common.impl.tardis.state.DimensionState;
import dev.amble.ait.common.impl.tardis.state.DesktopState;
import dev.amble.ait.common.lib.AitBlocks;
import dev.drtheo.ecs.behavior.TBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class DesktopBehavior implements TBehavior, DesktopPlacementEvents {
    private static final int FLOOR_Y = 63;
    private static final int INTERIOR_RADIUS = 8;
    private static final int INTERIOR_HEIGHT = 6;
    private static final BlockPos DOOR_POS = new BlockPos(0, FLOOR_Y + 1, -6);

    @Override
    public void startDesktopPlacing(ServerTardis tardis, MinecraftServer server) {
        DimensionState dimensionState = tardis.state(DimensionState.state);
        DesktopState desktopState = tardis.state(DesktopState.state);
        ServerLevel level = dimensionState.level.get();

        if (level == null) {
            return;
        }

        this.placeCenteredInterior(level);
        this.placeInteriorDoor(level, desktopState);
    }

    private void placeCenteredInterior(ServerLevel level) {
        BlockState shell = Blocks.STONE_BRICKS.defaultBlockState();

        // Build a simple shell centered around x/z = 0, then carve the inside to air.
        for (int x = -INTERIOR_RADIUS; x <= INTERIOR_RADIUS; x++) {
            for (int z = -INTERIOR_RADIUS; z <= INTERIOR_RADIUS; z++) {
                for (int y = FLOOR_Y; y <= FLOOR_Y + INTERIOR_HEIGHT; y++) {
                    boolean boundary = x == -INTERIOR_RADIUS || x == INTERIOR_RADIUS
                            || z == -INTERIOR_RADIUS || z == INTERIOR_RADIUS
                            || y == FLOOR_Y || y == FLOOR_Y + INTERIOR_HEIGHT;

                    BlockPos target = new BlockPos(x, y, z);
                    level.setBlock(target, boundary ? shell : Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        }
    }

    private void placeInteriorDoor(ServerLevel level, DesktopState desktopState) {
        BlockState doorState = AitBlocks.TARDIS_DOOR.defaultBlockState()
                .setValue(DoorBlock.ROTATION, 4)
                .setValue(DoorBlock.BETWEEN, false)
                .setValue(DoorBlock.ON_SLAB, false);

        level.setBlock(DOOR_POS, doorState, Block.UPDATE_ALL);

        // Keep a clear exit pocket in front of the door.
        for (int y = 0; y <= 2; y++) {
            level.setBlock(DOOR_POS.offset(0, y, 1), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            level.setBlock(DOOR_POS.offset(0, y, 2), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }

        level.setBlock(DOOR_POS.offset(0, -1, 1), Blocks.STONE_BRICKS.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(DOOR_POS.offset(0, -1, 2), Blocks.STONE_BRICKS.defaultBlockState(), Block.UPDATE_ALL);

        // Store the interior door position and rotation in DesktopState
        desktopState.doorPos = DOOR_POS;
        desktopState.doorRot = 4;
    }

    @Override
    public void duringDesktopPlacement(ServerTardis tardis, MinecraftServer server) {
        // probably gonna go unused tbf. - Loqor
    }

    @Override
    public void endDesktopPlaced(ServerTardis tardis, MinecraftServer server) {
        // Also unused ig. - Loqor
    }
}
