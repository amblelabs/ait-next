package dev.amble.ait.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class AitKeybinds {

    public static final KeyMapping SONIC_WHEEL = new KeyMapping(
            "key.ait.sonic_wheel",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.categories.ait"
    );

    public static final KeyMapping SONIC_TOGGLE = new KeyMapping(
            "key.ait.sonic_toggle",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.categories.ait"
    );
}

