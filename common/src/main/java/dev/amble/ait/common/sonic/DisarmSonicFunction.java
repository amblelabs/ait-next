package dev.amble.ait.common.sonic;

import dev.amble.ait.common.I18n;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleTypes;
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

    private static final Vector3f GOLD = new Vector3f(1.0f, 0.85f, 0.2f);
    private static final Vector3f WHITE = new Vector3f(1f, 1f, 1f);

    @Override
    public ItemStack preview() {
        return new ItemStack(Items.IRON_SWORD);
    }

    @Override
    public Component name() {
        return I18n.FUNC_ON_DISARM;
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
                spawnDisarmEffect(serverLevel, livingEntity);
            }

            return SonicCrystal.SonicFunction.HALT;
        }

        return 1;
    }

    private static void spawnDisarmEffect(ServerLevel level, LivingEntity target) {
        double x = target.getX();
        double y = target.getY() + target.getBbHeight() / 2;
        double z = target.getZ();

        DustColorTransitionOptions dust = new DustColorTransitionOptions(GOLD, WHITE, 1.5f);

        level.sendParticles(dust, x, y, z, 15, 0.4, 0.5, 0.4, 0.05);
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y, z, 8, 0.3, 0.4, 0.3, 0.1);
    }

    private static boolean canActivate(int ticks) {
        return ticks >= 10;
    }
}
