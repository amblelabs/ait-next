package dev.amble.ait.common.items;

import dev.amble.ait.api.mod.AitTags;
import dev.amble.ait.client.renderer.CoralSonicItemRenderer;
import dev.amble.ait.common.items.components.SonicCrystals;
import dev.amble.ait.common.items.components.SonicData;
import dev.amble.ait.common.items.tooltips.SonicTooltip;
import dev.amble.ait.common.lib.AitComponents;
import dev.amble.ait.common.sonic.SonicCrystal;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ItemSonic extends Item implements GeoItem {

    private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);
    public static final int TOOLTIP_MAX_WEIGHT = 4;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation OPEN = RawAnimation.begin().thenPlay("open");
    private static final RawAnimation CLOSE = RawAnimation.begin().thenPlay("close");

    public ItemSonic(Properties properties) {
        super(properties);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private @Nullable CoralSonicItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new CoralSonicItemRenderer();

                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "sonic_state", 2, state -> {
            ItemStack stack = state.getData(software.bernie.geckolib.constant.DataTickets.ITEMSTACK);
            if (stack != null && isOpened(stack)) {
                return state.setAndContinue(OPEN);
            }
            return state.setAndContinue(CLOSE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack screwdriver, ItemStack other, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess) {
        if (clickAction != ClickAction.SECONDARY || !slot.allowModification(player))
            return false;

        if (other.isEmpty())
            return false;

        if (!other.is(AitTags.Items.ZEITON_SHARDS) || !(other.getItem() instanceof ItemCrystal))
            return false;

        SonicCrystals contents = screwdriver.get(AitComponents.SONIC_CRYSTALS);
        if (contents == null) return false;

        SonicCrystals.Mutable mutable = new SonicCrystals.Mutable(contents);

        int i = mutable.tryInsert(other);

        if (i > 0) {
            this.playInsertSound(player);
        }

        screwdriver.set(AitComponents.SONIC_CRYSTALS, mutable.toImmutable());
        return true;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack itemStack, Slot slot, ClickAction clickAction, Player player) {
        if (clickAction != ClickAction.SECONDARY) return false;

        SonicCrystals bundleContents = itemStack.get(AitComponents.SONIC_CRYSTALS);
        if (bundleContents == null) return false;

        ItemStack itemStack2 = slot.getItem();
        if (!itemStack2.isEmpty()) return false;

        SonicCrystals.Mutable mutable = new SonicCrystals.Mutable(bundleContents);
        this.playRemoveOneSound(player);

        ItemStack itemStack3 = mutable.removeOne();

        if (itemStack3 != null) {
            ItemStack itemStack4 = slot.safeInsert(itemStack3);
            mutable.tryInsert(itemStack4);
        }

        itemStack.set(AitComponents.SONIC_CRYSTALS, mutable.toImmutable());
        return true;
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {
        SonicCrystals bundleContents = itemStack.getOrDefault(AitComponents.SONIC_CRYSTALS, SonicCrystals.EMPTY);
        return bundleContents.weight().compareTo(Fraction.ZERO) > 0;
    }

    @Override
    public int getBarWidth(ItemStack itemStack) {
        SonicCrystals bundleContents = itemStack.getOrDefault(AitComponents.SONIC_CRYSTALS, SonicCrystals.EMPTY);
        return Math.min(1 + Mth.mulAndTruncate(bundleContents.weight(), 12), 13);
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        return BAR_COLOR;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack itemStack) {
        return !itemStack.has(DataComponents.HIDE_TOOLTIP) && !itemStack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP)
                ? Optional.ofNullable(itemStack.get(AitComponents.SONIC_CRYSTALS)).map(SonicTooltip::new) : Optional.empty();
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
        SonicCrystals contents = stack.get(AitComponents.SONIC_CRYSTALS);

        if (contents != null) {
            int i = Mth.mulAndTruncate(contents.weight(), TOOLTIP_MAX_WEIGHT);
            lines.add(Component.translatable("item.minecraft.bundle.fullness", i, TOOLTIP_MAX_WEIGHT).withStyle(ChatFormatting.GRAY));
        }

        SonicCrystal.SonicFunction function = function(stack);

        lines.add(function != null ? function.name() : Component.empty());
        lines.add(Component.translatable(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    @Override
    public void onDestroyed(ItemEntity entity) {
        SonicCrystals contents = entity.getItem().get(AitComponents.SONIC_CRYSTALS);

        if (contents != null) {
            entity.getItem().set(AitComponents.SONIC_CRYSTALS, SonicCrystals.EMPTY);
            ItemUtils.onContainerDestroyed(entity, contents.itemsCopy());
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slotId, boolean isSelected) {
        if (!world.isClientSide && world instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            GeoItem.getOrAssignId(stack, serverLevel);
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        SonicCrystal.SonicFunction mode = function(stack);

        if (mode == null)
            return InteractionResultHolder.fail(stack);

        if (!checkCharge(stack))
            return InteractionResultHolder.fail(stack);

        if (mode.startUsing(stack, world, user, hand)) {
            user.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }

        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        SonicCrystal.SonicFunction mode = function(stack);
        if (mode == null) return;

        int ticks = mode.maxTime() - remainingUseTicks;

        int fuelUsed = mode.tick(stack, world, user, ticks, remainingUseTicks);

        if (fuelUsed == SonicCrystal.SonicFunction.HALT) {
            user.stopUsingItem();
            return;
        }

        removeCharge(stack, fuelUsed);

        if (!checkCharge(stack))
            user.stopUsingItem();
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        SonicCrystal.SonicFunction mode = function(stack);
        if (mode == null) return;

        mode.stopUsing(stack, world, user, mode.maxTime() - remainingUseTicks, remainingUseTicks);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        SonicCrystal.SonicFunction mode = function(stack);
        if (mode == null) return stack;

        mode.finishUsing(stack, level, user);
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        SonicCrystal.SonicFunction function = function(stack);
        return function != null ? function.maxTime() : 0;
    }

    @SuppressWarnings({"EmptyMethod", "unused"})
    private static void removeCharge(ItemStack stack, int amount) {
        // TODO: implement this
    }

    @SuppressWarnings({"EmptyMethod", "unused"})
    private static void addCharge(ItemStack stack, int amount) {
        // TODO: implement this
    }

    @SuppressWarnings({"BooleanMethodIsAlwaysInverted", "unused", "SameReturnValue"})
    private static boolean checkCharge(ItemStack stack) {
        // TODO: implement this
        return true;
    }

    private static @Nullable SonicCrystal.SonicFunction function(ItemStack stack) {
        int funcIdx = getFunction(stack);
        if (funcIdx == -1) return null;

        int relFuncIdx = funcIdx % 8;
        int crystalIdx = (funcIdx - relFuncIdx) / 8;

        SonicCrystal crystal = getCrystal(stack, crystalIdx);
        if (crystal == null || relFuncIdx >= crystal.functions().length) return null;

        return crystal.functions()[relFuncIdx];
    }

    public static @Nullable SonicCrystal getCrystal(ItemStack stack, int crystalIdx) {
        SonicCrystals crystals = stack.get(AitComponents.SONIC_CRYSTALS);
        if (crystals == null) return null;

        ItemStack crystalStack = crystals.getItem(crystalIdx);
        if (crystalStack == null) return null;

        if (!(crystalStack.getItem() instanceof ItemCrystal crystalItem)) return null;
        return crystalItem.getCrystal();
    }

    public static int getFunction(ItemStack itemStack) {
        return itemStack.getOrDefault(AitComponents.SONIC_DATA, SonicData.DEFAULT).function();
    }

    public static void setFunction(ItemStack stack, int funcIdx) {
        SonicData current = stack.getOrDefault(AitComponents.SONIC_DATA, SonicData.DEFAULT);
        stack.set(AitComponents.SONIC_DATA, current.withFunction(funcIdx));
    }

    public static boolean isOpened(ItemStack stack) {
        return stack.getOrDefault(AitComponents.SONIC_DATA, SonicData.DEFAULT).opened();
    }

    public static void setOpened(ItemStack stack, boolean opened) {
        SonicData current = stack.getOrDefault(AitComponents.SONIC_DATA, SonicData.DEFAULT);
        stack.set(AitComponents.SONIC_DATA, current.withOpened(opened));
    }

    public static boolean toggleOpened(ItemStack stack) {
        boolean newState = !isOpened(stack);
        setOpened(stack, newState);
        return newState;
    }

    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }
}
