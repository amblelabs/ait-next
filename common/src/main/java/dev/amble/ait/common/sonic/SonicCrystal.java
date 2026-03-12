package dev.amble.ait.common.sonic;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public record SonicCrystal(SonicFunction... functions) {

    public static final SonicCrystal EMPTY = new SonicCrystal();
    public static final SonicCrystal BASIC = new SonicCrystal(new SetOnFireSonicFunction());
    public static final SonicCrystal OVERCHARGED = new SonicCrystal(new SetOnFireSonicFunction());
    public static final SonicCrystal RESONATING = new SonicCrystal(new SetOnFireSonicFunction());
    public static final SonicCrystal GRAVITY = new SonicCrystal(new GravitateSonicFunction(), new GravityPushSonicFunction());
    public static final SonicCrystal REFRACTION = new SonicCrystal(new LaserSonicFunction());
    public static final SonicCrystal AMETHYST = new SonicCrystal(new SetOnFireSonicFunction());
    public static final SonicCrystal QUARTZ = new SonicCrystal(new HighFrequencySonicFunction());
    public static final SonicCrystal ECHO_SHARD = new SonicCrystal(new DisarmSonicFunction());

    public interface SonicFunction {
        ItemStack preview();
        Component name();

        int maxTime();

        default boolean startUsing(ItemStack stack, Level level, Player user, InteractionHand hand) {
            return true;
        }

        // FIXME: fuel usage, halt, etc should be instead provided either via return type OR via a context arg
        //  instead of this silly magic number shit
        default int tick(ItemStack stack, Level level, LivingEntity user, int ticks, int ticksLeft) {
            return 0;
        }

        @SuppressWarnings("EmptyMethod")
        default void stopUsing(ItemStack stack, Level level, LivingEntity user, int ticks, int ticksLeft) { }

        default void finishUsing(ItemStack stack, Level level, LivingEntity user) {
            this.stopUsing(stack, level, user, this.maxTime(), 0);
        }

        int MAX_DISTANCE = 16;
        int HALT = -256;

        static HitResult getHitResultForOutline(LivingEntity user) {
            return getHitResultForOutline(user, MAX_DISTANCE);
        }

        static HitResult getHitResult(LivingEntity user) {
            return getHitResult(user, MAX_DISTANCE);
        }

        // FIXME: ported from AIT 1.x
        static HitResult getHitResultForOutline(LivingEntity user, double distance) {
            Vec3 eyePos = user.getEyePosition(1.0F);
            Vec3 rotation = user.getViewVector(1.0F);
            Vec3 end = eyePos.add(rotation.scale(distance));

            return user.level().clip(new ClipContext(eyePos, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, user));
        }

        static HitResult getHitResult(LivingEntity user, double distance) {
            Vec3 eyePos = user.getEyePosition(1.0F);
            Vec3 rotation = user.getViewVector(1.0F);
            Vec3 end = eyePos.add(rotation.scale(distance));

            BlockHitResult blockHit = user.level().clip(new ClipContext(eyePos, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, user));

            double blockDist = blockHit.getType() != HitResult.Type.MISS
                    ? blockHit.getLocation().distanceToSqr(eyePos) : distance * distance;

            EntityHitResult entityHit = null;
            double closestEntityDist = blockDist;

            for (var entity : user.level().getEntities(user, user.getBoundingBox().expandTowards(rotation.scale(distance)).inflate(1.0))) {
                var aabb = entity.getBoundingBox().inflate(entity.getPickRadius());
                var optional = aabb.clip(eyePos, end);

                if (optional.isPresent()) {
                    double entityDist = eyePos.distanceToSqr(optional.get());
                    if (entityDist < closestEntityDist) {
                        closestEntityDist = entityDist;
                        entityHit = new EntityHitResult(entity, optional.get());
                    }
                }
            }

            return entityHit != null ? entityHit : blockHit;
        }
    }
}
