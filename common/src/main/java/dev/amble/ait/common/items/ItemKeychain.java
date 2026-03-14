package dev.amble.ait.common.items;

import dev.amble.ait.api.mod.AitTags;
import dev.amble.ait.common.items.tooltips.KeychainTooltip;
import dev.amble.ait.common.lib.AitComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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
import net.minecraft.world.level.Level;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemKeychain extends Item {

    private static final int BAR_COLOR = 0x6666FF;
    public static final int MAX_SIZE = 5;

    public ItemKeychain(Properties properties) {
        super(properties);
    }

    public record KeychainContents(List<ItemStack> items, int selectedIndex) {
        public static final KeychainContents EMPTY = new KeychainContents(List.of(), -1);

        public static final Codec<KeychainContents> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        ItemStack.CODEC.listOf().fieldOf("items").forGetter(KeychainContents::items),
                        Codec.INT.optionalFieldOf("selected_index", -1).forGetter(KeychainContents::selectedIndex)
                ).apply(instance, KeychainContents::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, KeychainContents> STREAM_CODEC =
                StreamCodec.composite(
                        ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
                        KeychainContents::items,
                        ByteBufCodecs.INT,
                        KeychainContents::selectedIndex,
                        KeychainContents::new
                );

        public int size() { return items.size(); }
        public boolean isEmpty() { return items.isEmpty(); }

        public ItemStack getItem(int index) {
            return index >= 0 && index < items.size() ? items.get(index) : ItemStack.EMPTY;
        }

        public ItemStack getSelectedItem() {
            return selectedIndex >= 0 && selectedIndex < items.size() ? items.get(selectedIndex) : ItemStack.EMPTY;
        }

        public List<ItemStack> itemsCopy() { return new ArrayList<>(items); }

        public ItemStack removeOne() {
            if (items.isEmpty()) return ItemStack.EMPTY;
            return items.get(items.size() - 1).copy();
        }

        public KeychainContents withRemovedLast() {
            if (items.isEmpty()) return this;

            List<ItemStack> newItems = new ArrayList<>(items.subList(0, items.size() - 1));
            int newSelected = selectedIndex;

            if (selectedIndex >= items.size() - 1) {
                newSelected = newItems.isEmpty() ? -1 : Math.min(selectedIndex, newItems.size() - 1);
            }

            return new KeychainContents(newItems, newSelected);
        }

        public KeychainContents withSelected(int index) {
            if (index < -1 || index >= items.size()) return this;
            return new KeychainContents(items, index);
        }

        public KeychainContents withAdded(ItemStack stack) {
            List<ItemStack> newItems = new ArrayList<>(items);
            newItems.add(stack);
            return new KeychainContents(newItems, selectedIndex);
        }

        public Mutable mutable() { return new Mutable(this); }

        public static class Mutable {
            private final List<ItemStack> items;
            private int selectedIndex;

            public Mutable(KeychainContents contents) {
                this.items = new ArrayList<>(contents.items);
                this.selectedIndex = contents.selectedIndex;
            }

            public int tryInsert(ItemStack stack) {
                if (stack.isEmpty() || items.size() >= MAX_SIZE) return 0;

                int canInsert = Math.min(stack.getCount(), MAX_SIZE - items.size());
                if (canInsert <= 0) return 0;

                ItemStack toInsert = stack.copy();
                toInsert.setCount(canInsert);
                items.add(toInsert);
                stack.shrink(canInsert);

                if (items.size() == 1) {
                    selectedIndex = 0;
                }

                return canInsert;
            }

            public void setSelected(int index) {
                if (index >= -1 && index < items.size()) {
                    this.selectedIndex = index;
                }
            }

            public KeychainContents toImmutable() {
                return new KeychainContents(List.copyOf(items), selectedIndex);
            }
        }
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack keychain, Slot slot, ClickAction action, Player player) {
        if (action != ClickAction.SECONDARY) return false;

        ItemStack other = slot.getItem();

        if (other.isEmpty()) {
            handleRemoveFromKeychain(keychain, slot, player);
        } else if (isKey(other)) {
            handleInsertIntoKeychain(keychain, slot, player);
        }

        return true;
    }

    private void handleRemoveFromKeychain(ItemStack keychain, Slot slot, Player player) {
        KeychainContents contents = keychain.get(AitComponents.KEYCHAIN_CONTENTS);

        if (contents == null || contents.isEmpty()) return;

        ItemStack removed = contents.removeOne();
        if (removed.isEmpty()) return;

        playRemoveOneSound(player);
        ItemStack remaining = slot.safeInsert(removed);

        if (!remaining.isEmpty()) {
            KeychainContents.Mutable mutable = contents.mutable();
            mutable.tryInsert(remaining);
            keychain.set(AitComponents.KEYCHAIN_CONTENTS, mutable.toImmutable());
            playInsertSound(player);
        } else {
            keychain.set(AitComponents.KEYCHAIN_CONTENTS, contents.withRemovedLast());
        }
    }

    private void handleInsertIntoKeychain(ItemStack keychain, Slot slot, Player player) {
        KeychainContents contents = keychain.getOrDefault(AitComponents.KEYCHAIN_CONTENTS, KeychainContents.EMPTY);

        if (contents.size() >= MAX_SIZE) return;

        KeychainContents.Mutable mutable = contents.mutable();
        int spaceLeft = MAX_SIZE - contents.size();

        ItemStack slotStack = slot.getItem();
        int takeCount = Math.min(slotStack.getCount(), spaceLeft);

        if (takeCount <= 0) return;

        ItemStack taken = slotStack.copy();
        taken.setCount(takeCount);
        slotStack.shrink(takeCount);

        int inserted = mutable.tryInsert(taken);
        if (inserted > 0) {
            playInsertSound(player);
            keychain.set(AitComponents.KEYCHAIN_CONTENTS, mutable.toImmutable());
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack keychain, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (action != ClickAction.SECONDARY || !slot.allowModification(player)) return false;

        if (other.isEmpty()) {
            handleRemoveToCursor(keychain, access, player);
        } else if (isKey(other)) {
            handleInsertFromCursor(keychain, other, player);
        }

        return true;
    }

    private void handleRemoveToCursor(ItemStack keychain, SlotAccess access, Player player) {
        KeychainContents contents = keychain.get(AitComponents.KEYCHAIN_CONTENTS);

        if (contents == null || contents.isEmpty()) return;

        ItemStack removed = contents.removeOne();
        if (removed.isEmpty()) return;

        playRemoveOneSound(player);
        access.set(removed);
        keychain.set(AitComponents.KEYCHAIN_CONTENTS, contents.withRemovedLast());
    }

    private void handleInsertFromCursor(ItemStack keychain, ItemStack other, Player player) {
        KeychainContents contents = keychain.getOrDefault(AitComponents.KEYCHAIN_CONTENTS, KeychainContents.EMPTY);

        if (contents.size() >= MAX_SIZE) return;

        KeychainContents.Mutable mutable = contents.mutable();
        int spaceLeft = MAX_SIZE - contents.size();
        int takeCount = Math.min(other.getCount(), spaceLeft);

        if (takeCount <= 0) return;

        ItemStack taken = other.copy();
        taken.setCount(takeCount);
        other.shrink(takeCount);

        int inserted = mutable.tryInsert(taken);
        if (inserted > 0) {
            playInsertSound(player);
            keychain.set(AitComponents.KEYCHAIN_CONTENTS, mutable.toImmutable());
        }
    }

    private boolean isKey(ItemStack stack) {
        return stack.is(AitTags.Items.KEYS) && stack.getItem() instanceof ItemKey;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (stack.has(DataComponents.HIDE_TOOLTIP) || stack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP)) {
            return Optional.empty();
        }

        KeychainContents contents = stack.get(AitComponents.KEYCHAIN_CONTENTS);
        return contents != null && !contents.isEmpty()
                ? Optional.of(new KeychainTooltip(contents))
                : Optional.empty();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        KeychainContents contents = stack.get(AitComponents.KEYCHAIN_CONTENTS);
        if (contents != null && !contents.isEmpty()) {

            if (contents.selectedIndex() >= 0) {
                ItemStack selected = contents.getSelectedItem();
            }
        }
    }

    @Override
    public void onDestroyed(ItemEntity entity) {
        KeychainContents contents = entity.getItem().get(AitComponents.KEYCHAIN_CONTENTS);
        if (contents != null && !contents.isEmpty()) {
            entity.getItem().set(AitComponents.KEYCHAIN_CONTENTS, KeychainContents.EMPTY);
            ItemUtils.onContainerDestroyed(entity, contents.itemsCopy());
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        KeychainContents contents = stack.get(AitComponents.KEYCHAIN_CONTENTS);
        return contents != null && !contents.isEmpty();
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        KeychainContents contents = stack.get(AitComponents.KEYCHAIN_CONTENTS);
        if (contents == null) return 0;
        return Math.min(1 + (contents.size() * 12) / MAX_SIZE, 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BAR_COLOR;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            KeychainContents contents = stack.get(AitComponents.KEYCHAIN_CONTENTS);
            if (contents != null && !contents.isEmpty()) {
                KeychainContents.Mutable mutable = contents.mutable();
                int nextIndex = (contents.selectedIndex() + 1) % contents.size();
                mutable.setSelected(nextIndex);
                stack.set(AitComponents.KEYCHAIN_CONTENTS, mutable.toImmutable());

                player.playSound(SoundEvents.CHAIN_HIT, 1F, 1.2F);

                return InteractionResultHolder.success(stack);
            }
        }

        return InteractionResultHolder.pass(stack);
    }

    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.CHAIN_BREAK, 1F, 1.2F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.CHAIN_PLACE, 1F, 1.2F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    public static void selectSlot(ItemStack stack, int slotIndex) {
        KeychainContents contents = stack.get(AitComponents.KEYCHAIN_CONTENTS);
        if (contents != null) {
            KeychainContents.Mutable mutable = contents.mutable();
            mutable.setSelected(slotIndex);
            stack.set(AitComponents.KEYCHAIN_CONTENTS, mutable.toImmutable());
        }
    }

}