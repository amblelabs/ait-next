package dev.amble.ait.common.sonic;

import dev.amble.ait.common.I18n;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.gui.screens.social.PlayerEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class SetOnFireSonicFunction implements SonicCrystal.SonicFunction {
    @Override
    public ItemStack preview() {
        return new ItemStack(Items.FIRE_CHARGE);
    }

    @Override
    public Component name() {
        return I18n.FUNC_ON_FIRE;
    }

    @Override
    public int maxTime() {
        return 2 * 20; // 2 seconds
    }

    @Override
    public int tick(ItemStack stack, Level level, LivingEntity user, int ticks, int ticksLeft) {
        if (canLight(ticks)) {
            HitResult hitResult = SonicCrystal.SonicFunction.getHitResultForOutline(user);

            if (hitResult instanceof BlockHitResult blockHit) {
                if (!(user instanceof Player player)) return SonicCrystal.SonicFunction.HALT;
                UseOnContext context = new UseOnContext(player, player.getUsedItemHand(), blockHit);
                BlockPos pos = context.getClickedPos();
                BlockState state = level.getBlockState(pos);
                Block block = state.getBlock();

                if (block instanceof TntBlock) {
                    TntBlock.explode(level, pos);
                    level.removeBlock(pos, false);
                    level.gameEvent(user, GameEvent.BLOCK_DESTROY, pos);

                    return SonicCrystal.SonicFunction.HALT;
                }

                if (state.getBlock() instanceof AbstractCandleBlock) {
                    level.setBlock(pos, state.setValue(AbstractCandleBlock.LIT, true), Block.UPDATE_ALL);
                    level.gameEvent(user, GameEvent.BLOCK_CHANGE, pos);

                    return SonicCrystal.SonicFunction.HALT;
                }

                BlockPos blockPos2 = pos.relative(context.getClickedFace());
                if (BaseFireBlock.canBePlacedAt(level, blockPos2, context.getHorizontalDirection())) {
                    level.playSound(user, blockPos2, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
                    BlockState blockState2 = BaseFireBlock.getState(level, blockPos2);
                    level.setBlock(blockPos2, blockState2, 11);
                    level.gameEvent(user, GameEvent.BLOCK_PLACE, pos);

                    return SonicCrystal.SonicFunction.HALT;
                }
            }
        }

        return 1;
    }

    private static boolean canLight(int ticks) {
        return ticks >= 10;
    }
}
