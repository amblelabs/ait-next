package dev.amble.ait.common;

import dev.amble.ait.api.AitAPI;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class I18n {

    public static final Component EMPTY_WIDGET = Component.translatable("widget." + AitAPI.MOD_ID + ".empty")
            .append(Component.literal("\n"))
            .append(Component.translatable("widget." + AitAPI.MOD_ID + ".empty.desc")
                    .withStyle(ChatFormatting.GRAY));

    public static final Component FUNC_ON_FIRE = Component.translatable("sonic." + AitAPI.MOD_ID + ".set_on_fire");
}
