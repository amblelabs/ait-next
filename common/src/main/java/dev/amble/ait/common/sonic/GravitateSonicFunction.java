package dev.amble.ait.common.sonic;

import dev.amble.ait.common.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class GravitateSonicFunction implements SonicCrystal.SonicFunction {
    @Override
    public ItemStack preview() {
        return new ItemStack(Items.AMETHYST_SHARD);
    }

    @Override
    public Component name() {
        return I18n.FUNC_ON_GRAVITATE;
    }

    @Override
    public int maxTime() {
        return 2 * 20; // 2 seconds
    }

    @Override
    public int tick(ItemStack stack, Level level, LivingEntity user, int ticks, int ticksLeft) {
        if (canActivate(ticks)) {
            HitResult hitResult = SonicCrystal.SonicFunction.getHitResult(user);

            System.out.println(hitResult);


            if (hitResult instanceof EntityHitResult entityHitResult) {
                if (!(user instanceof Player player)) return SonicCrystal.SonicFunction.HALT;
                LivingEntity target = (LivingEntity) entityHitResult.getEntity();
                target.forceAddEffect(new MobEffectInstance(MobEffects.LEVITATION, 200), player);
                level.playSound(null, target.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 2.0F);
                return SonicCrystal.SonicFunction.HALT;
            }
        }

        return 1;
    }

    private static boolean canActivate(int ticks) {
        return ticks >= 10;
    }
}
