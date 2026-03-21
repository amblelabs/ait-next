package dev.amble.ait.api.tardis.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.RelativeMovement;

public class TeleportUtil {

    public static void teleportWithOffset(LivingEntity livingEntity, ServerLevel serverLevel, BlockPos blockPos, byte yaw) {
        teleportWithOffset(livingEntity, serverLevel, blockPos, yaw * 45, 0);
    }

    public static void teleportWithOffset(LivingEntity livingEntity, ServerLevel serverLevel, BlockPos blockPos, float yaw, float pitch) {
        livingEntity.teleportTo(serverLevel, blockPos.getX(), blockPos.getY(), blockPos.getZ(), RelativeMovement.ROTATION, yaw, pitch);
    }

    private BlockPos positionOffset(BlockPos pos, byte yaw) {
        return switch(yaw) {
            case 0 -> pos.offset(0, 0, 1);
            case 1 -> pos.offset(-1, 0, 0);
            case 2 -> pos.offset(0, 0, -1);
            case 3 -> pos.offset(1, 0, 0);
            default -> pos;
        };
    }
}
