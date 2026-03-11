package dev.amble.ait.common.sonic;

import dev.amble.ait.common.I18n;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class LaserSonicFunction implements SonicCrystal.SonicFunction {

    private static final float DAMAGE = 8.0f;
    private static final int FUEL_COST = 4;

    private static final Vector3f RED = new Vector3f(1.0f, 0.1f, 0.1f);
    private static final Vector3f WHITE = new Vector3f(1.0f, 0.8f, 0.8f);

    @Override
    public ItemStack preview() {
        return new ItemStack(Items.BLAZE_ROD);
    }

    @Override
    public Component name() {
        return I18n.FUNC_ON_LASER;
    }

    @Override
    public int maxTime() {
        return 2 * 20;
    }

    @Override
    public int tick(ItemStack stack, Level level, LivingEntity user, int ticks, int ticksLeft) {
        if (!canActivate(ticks)) return 1;

        HitResult hitResult = SonicCrystal.SonicFunction.getHitResult(user);

        if (hitResult instanceof EntityHitResult entityHitResult) {
            if (!(user instanceof Player player)) return SonicCrystal.SonicFunction.HALT;

            Entity entity = entityHitResult.getEntity();
            entity.hurt(level.damageSources().playerAttack(player), DAMAGE);
            entity.setRemainingFireTicks(40);

            level.playSound(null, user.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0F, 1.8F);

            if (level instanceof ServerLevel serverLevel) {
                spawnBeam(serverLevel, user.getEyePosition(), entityHitResult.getLocation());
            }

            return FUEL_COST;
        }

        return 1;
    }

    private static void spawnBeam(ServerLevel level, Vec3 origin, Vec3 target) {
        Vec3 direction = target.subtract(origin);
        double distance = direction.length();
        Vec3 step = direction.normalize();

        int particleCount = (int) (distance * 6);
        DustColorTransitionOptions dust = new DustColorTransitionOptions(WHITE, RED, 0.8f);

        for (int i = 0; i < particleCount; i++) {
            double progress = (double) i / particleCount;
            Vec3 pos = origin.add(step.scale(distance * progress));

            level.sendParticles(dust, pos.x, pos.y, pos.z, 1, 0.02, 0.02, 0.02, 0.0);
        }
    }

    private static boolean canActivate(int ticks) {
        return ticks >= 10;
    }
}

