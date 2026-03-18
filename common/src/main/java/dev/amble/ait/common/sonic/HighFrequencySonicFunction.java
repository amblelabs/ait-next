package dev.amble.ait.common.sonic;

import dev.amble.ait.api.mod.sonic.SonicCrystal;
import dev.amble.ait.common.I18n;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class HighFrequencySonicFunction implements SonicCrystal.SonicFunction {

    private static final double RANGE = 8.0;
    private static final int STUN_DURATION = 100;

    @Override
    public ItemStack preview() {
        return new ItemStack(Items.NOTE_BLOCK);
    }

    @Override
    public Component name() {
        return I18n.FUNC_ON_HIGH_FREQ;
    }

    @Override
    public int maxTime() {
        return 2 * 20;
    }

    @Override
    public int tick(ItemStack stack, Level level, LivingEntity user, int ticks, int ticksLeft) {
        if (!canActivate(ticks)) return 1;
        if (!(user instanceof Player player)) return SonicCrystal.SonicFunction.HALT;

        AABB area = user.getBoundingBox().inflate(RANGE);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area, e -> e != user && e.isAlive());

        if (targets.isEmpty()) return SonicCrystal.SonicFunction.HALT;

        for (LivingEntity target : targets) {
            target.forceAddEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, STUN_DURATION, 5), player);
            target.forceAddEffect(new MobEffectInstance(MobEffects.BLINDNESS, STUN_DURATION, 0), player);
            target.forceAddEffect(new MobEffectInstance(MobEffects.WEAKNESS, STUN_DURATION, 2), player);
        }

        level.playSound(null, user.blockPosition(), SoundEvents.BELL_RESONATE, SoundSource.PLAYERS, 2.0F, 0.1F);
        level.playSound(null, user.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 0.6F, 2.0F);

        if (level instanceof ServerLevel serverLevel) {
            spawnShockwave(serverLevel, user);
        }

        return SonicCrystal.SonicFunction.HALT;
    }

    private static void spawnShockwave(ServerLevel level, LivingEntity user) {
        double cx = user.getX();
        double cy = user.getY() + user.getBbHeight() / 2;
        double cz = user.getZ();

        for (int i = 0; i < 60; i++) {
            double angle = Math.toRadians(i * 6.0);
            double radius = RANGE * 0.8;
            double px = cx + Math.cos(angle) * radius;
            double pz = cz + Math.sin(angle) * radius;

            level.sendParticles(ParticleTypes.SONIC_BOOM, px, cy, pz, 1, 0, 0, 0, 0);
        }

        level.sendParticles(ParticleTypes.FLASH, cx, cy, cz, 1, 0, 0, 0, 0);
    }

    private static boolean canActivate(int ticks) {
        return ticks >= 10;
    }
}

