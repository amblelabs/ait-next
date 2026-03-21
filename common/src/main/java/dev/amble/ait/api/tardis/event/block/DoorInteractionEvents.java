package dev.amble.ait.api.tardis.event.block;

import dev.amble.ait.api.tardis.Tardis;
import dev.amble.ait.api.tardis.event.NotifyEvent;
import dev.drtheo.ecs.event.TEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface DoorInteractionEvents extends TEvents {

    static void useWithoutItem(Tardis tardis, BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        TEvents.handle(new NotifyEvent<>(event, handler -> handler.door$useWithoutItem(tardis, state, level, pos, player, hit)));
    }

    static void useWithItem(Tardis tardis, ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        TEvents.handle(new NotifyEvent<>(event, handler -> handler.door$useWithItem(tardis, stack, state, level, pos, player, hand, hit)));
    }

    Type<DoorInteractionEvents> event = new Type<>(DoorInteractionEvents.class);

    void door$useWithoutItem(Tardis tardis, BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit);
    void door$useWithItem(Tardis tardis, ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit);
}
