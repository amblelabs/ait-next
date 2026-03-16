package dev.amble.ait.common.sonic;

import dev.amble.ait.api.mod.sonic.SonicCrystal;
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

public class GravityPushSonicFunction implements SonicCrystal.SonicFunction {

    private static final float PUSH_STRENGTH = 2.5f;
    private static final float PUSH_UPWARD = 0.4f;

    private static final Vector3f BRIGHT_BLUE = new Vector3f(0.2f, 0.6f, 1.0f);
    private static final Vector3f DARK_BLUE = new Vector3f(0.02f, 0.02f, 0.15f);

    @Override
    public ItemStack preview() {
        return new ItemStack(Items.BLACK_WOOL);
    }

    @Override
    public Component name() {
        return I18n.FUNC_ON_PUSH;
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
            if (!(user instanceof Player)) return SonicCrystal.SonicFunction.HALT;

            Entity entity = entityHitResult.getEntity();

            Vec3 direction = entity.position().subtract(user.position()).normalize();
            Vec3 push = direction.multiply(PUSH_STRENGTH, PUSH_STRENGTH, PUSH_STRENGTH)
                    .add(0, PUSH_UPWARD, 0);

            entity.setDeltaMovement(entity.getDeltaMovement().add(push));
            entity.hurtMarked = true;

            level.playSound(null, user.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.5F, 0.5F);
            level.playSound(null, entity.blockPosition(), SoundEvents.WIND_CHARGE_BURST.value(), SoundSource.PLAYERS, 1.0F, 0.8F);

            if (level instanceof ServerLevel serverLevel) {
                spawnPushWave(serverLevel, user.getPosition(0), entity.position().add(0, entity.getBbHeight() / 2, 0));
            }

            return SonicCrystal.SonicFunction.HALT;
        }

        return 1;
    }

    private static void spawnPushWave(ServerLevel level, Vec3 origin, Vec3 target) {
        Vec3 direction = target.subtract(origin);
        double distance = direction.length();
        Vec3 step = direction.normalize();

        int particleCount = (int) (distance * 4);
        DustColorTransitionOptions dustOptions = new DustColorTransitionOptions(
                BRIGHT_BLUE, DARK_BLUE, 1.5f
        );

        for (int i = 0; i < particleCount; i++) {
            double progress = (double) i / particleCount;
            Vec3 pos = origin.add(step.scale(distance * progress));

            double spread = 0.15 + progress * 0.6;

            level.sendParticles(
                    dustOptions,
                    pos.x, pos.y, pos.z,
                    3,
                    spread, spread, spread,
                    0.02
            );
        }
    }

    private static boolean canActivate(int ticks) {
        return ticks >= 10;
    }
}
