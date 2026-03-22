package dev.amble.ait.common.impl.tardis.behavior;

import dev.amble.ait.api.tardis.Tardis;
import dev.amble.ait.api.tardis.event.block.DoorInteractionEvents;
import dev.amble.ait.api.tardis.util.TeleportUtil;
import dev.amble.ait.common.impl.tardis.state.DoorState;
import dev.amble.ait.common.impl.tardis.state.ExteriorState;
import dev.amble.ait.common.lib.AitSounds;
import dev.drtheo.ecs.behavior.TBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CapsuleDoorBehavior implements TBehavior, DoorInteractionEvents {

    @Override
    public void door$useWithoutItem(Tardis tardis, BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        DoorState door = tardis.state(DoorState.state);

        if (door.closed()) { // closed
            door.rightOpen = true;

            if (player.isShiftKeyDown()) {
                door.leftOpen = true;
            }

            level.playSound(null, pos, AitSounds.DOOR_OPEN, SoundSource.BLOCKS, 0.5f, 1.0f);
        } else if (door.rightOpen && !door.leftOpen) { // only right open
            if (player.isShiftKeyDown()) {
                door.rightOpen = false; // if sneaking, close the only open door (right)
            } else {
                door.leftOpen = true; // otherwise, open the other door
            }

            level.playSound(null, pos, AitSounds.DOOR_OPEN, SoundSource.BLOCKS, 0.5f, 1.0f);
        } else { // both open
            door.rightOpen = false;
            door.leftOpen = false;

            level.playSound(null, pos, AitSounds.DOOR_CLOSE, SoundSource.BLOCKS, 0.6f, 1.0f);
        }

        tardis.markDirty();
    }

    @Override
    public void door$useWithItem(Tardis tardis, ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {

        ExteriorState exteriorState = tardis.state(ExteriorState.state);

        if (!(level instanceof ServerLevel serverLevel)) return;

        GlobalPos globalPos = exteriorState.exteriorPos;

        byte direction = exteriorState.exteriorRot;

        BlockPos finalPos = globalPos.pos();

        ServerLevel exteriorLevel = serverLevel.getServer().getLevel(globalPos.dimension());
        if (exteriorLevel == null) return;

        TeleportUtil.teleportWithOffset(player, exteriorLevel, finalPos, direction);
    }
}
