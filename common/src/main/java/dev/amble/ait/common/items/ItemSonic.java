package dev.amble.ait.common.items;

import dev.amble.ait.api.mod.AitTags;
import dev.amble.ait.client.screen.SonicWheelScreen;
import dev.amble.ait.common.items.components.SonicCrystals;
import dev.amble.ait.common.items.components.SonicData;
import dev.amble.ait.common.items.tooltips.SonicTooltip;
import dev.amble.ait.common.lib.AitComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ItemSonic extends Item {

    private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);
    public static final int TOOLTIP_MAX_WEIGHT = 4;

    public ItemSonic(Properties properties) {
        super(properties);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack screwdriver, ItemStack other, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess) {
        if (clickAction != ClickAction.SECONDARY || !slot.allowModification(player))
            return false;

        if (other.isEmpty()) {
            SonicCrystals contents = screwdriver.get(AitComponents.SONIC_CRYSTALS);
            if (contents == null) return false;

            // FIXME: this will explode on the server, i think.
            Minecraft.getInstance().setScreen(SonicWheelScreen.tryCreate(contents));
            return true;
        }

        if (!other.is(AitTags.Items.ZEITON_SHARDS))
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
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack itemStack) {
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

        lines.add(Component.empty());
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

    public static int getCrystal(ItemStack itemStack) {
        return itemStack.getOrDefault(AitComponents.SONIC_DATA, SonicData.DEFAULT).currentCrystal();
    }

    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }
}
