package dev.amble.ait.common.lib;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class AitCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var mainCmd = Commands.literal("ait");

        // CommandImpl.add(mainCmd)

        dispatcher.register(mainCmd);
    }
}