package dev.amble.ait.common.lib;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class AitCommands {
    public static LiteralArgumentBuilder<CommandSourceStack> root() {
        // CommandImpl.add(mainCmd)
        return Commands.literal("ait");
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(root());
    }
}