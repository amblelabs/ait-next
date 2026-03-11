package dev.amble.ait.common.sonic;

import dev.amble.ait.common.I18n;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class DisarmSonicFunction implements SonicCrystal.SonicFunction {

    private static final Vector3f GOLD = new Vector3f(0.5f, 0.5f, 0.2f);
    private static final Vector3f WHITE = new Vector3f(1f, 1f, 1f);

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
            if (!(entity instanceof LivingEntity livingEntity)) return SonicCrystal.SonicFunction.HALT;

            ItemStack droppedStack = livingEntity.getMainHandItem().copy();

            if (droppedStack.isEmpty()) return SonicCrystal.SonicFunction.HALT;

            livingEntity.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);

            // Fully configure the ItemEntity before adding to world
            ItemEntity itemEntity = new ItemEntity(
                    level,
                    livingEntity.getX(), livingEntity.getY() + livingEntity.getBbHeight() / 2, livingEntity.getZ(),
                    droppedStack
            );

            Vec3 lookDir = livingEntity.getLookAngle();
            itemEntity.setDeltaMovement(lookDir.x * 0.2, 0.3, lookDir.z * 0.2);
            itemEntity.setPickUpDelay(40);

            level.addFreshEntity(itemEntity);


            level.playSound(null, livingEntity.blockPosition(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F, 0.8F);

            if (level instanceof ServerLevel serverLevel) {
                spawnPushWave(serverLevel, livingEntity.position().add(0, livingEntity.getBbHeight() / 2, 0),
                        livingEntity.position().add(0, livingEntity.getBbHeight() / 2, 0));
            }

            return SonicCrystal.SonicFunction.HALT;
        }

        return 1;
    }

    /**
     * Spawns a wave of color-transitioning dust particles from origin to target.
     * Particles transition from bright blue to dark blue along the path.
     */
    private static void spawnPushWave(ServerLevel level, Vec3 origin, Vec3 target) {
        Vec3 direction = target.subtract(origin);
        double distance = direction.length();
        Vec3 step = direction.normalize();

        int particleCount = (int) (distance * 4);
        DustColorTransitionOptions dustOptions = new DustColorTransitionOptions(
                GOLD, WHITE, 1.5f
        );

        for (int i = 0; i < particleCount; i++) {
            double progress = (double) i / particleCount;
            Vec3 pos = origin.add(step.scale(distance * progress));

            // Spread particles in a cone that widens toward the target
            double spread = 0.15 + progress * 0.6;

            level.sendParticles(
                    dustOptions,
                    pos.x, pos.y, pos.z,
                    3,             // count
                    spread, spread, spread,  // offset
                    0.02           // speed
            );
        }
    }

    private static boolean canActivate(int ticks) {
        return ticks >= 10;
    }
}
