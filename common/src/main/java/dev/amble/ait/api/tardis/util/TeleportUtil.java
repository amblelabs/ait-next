package dev.amble.ait.api.tardis.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.RelativeMovement;

public class TeleportUtil {

    public static void teleportWithOffset(LivingEntity livingEntity, ServerLevel serverLevel, BlockPos blockPos, byte yaw) {
        int rotation = normalizeRotation(yaw);
        BlockPos offsetPos = positionOffset(blockPos, rotation);
        teleportWithOffset(livingEntity, serverLevel, offsetPos, rotation * 45.0f, 0.0f);
    }

    public static void teleportWithOffset(LivingEntity livingEntity, ServerLevel serverLevel, BlockPos blockPos, float yaw, float pitch) {
        livingEntity.teleportTo(serverLevel,
                blockPos.getX() + 0.5,
                blockPos.getY(),
                blockPos.getZ() + 0.5,
                RelativeMovement.ROTATION,
                yaw,
                pitch);
    }

    private static int normalizeRotation(byte rotation) {
        return rotation & 7;
    }

    private static BlockPos positionOffset(BlockPos pos, int rotation) {
        return switch (rotation) {
            case 0 -> pos.offset(0, 0, 1);
            case 1 -> pos.offset(-1, 0, 1);
            case 2 -> pos.offset(-1, 0, 0);
            case 3 -> pos.offset(-1, 0, -1);
            case 4 -> pos.offset(0, 0, -1);
            case 5 -> pos.offset(1, 0, -1);
            case 6 -> pos.offset(1, 0, 0);
            case 7 -> pos.offset(1, 0, 1);
            default -> pos;
        };
    }
}
