package dev.amble.ait.common.lib;

public final class AitVariants {

    public record ConsoleVariant(String modelName, String animationName, String[] textureNames) {
    }

    public static final ConsoleVariant[] CONSOLE_VARIANTS = {
            new ConsoleVariant("hartnell_console", "hartnell_console", new String[]{"hartnell"}),
            // Legacy tokamak console currently uses the renaissance asset filenames.
            new ConsoleVariant("renaissance_console", "renaissance_console", new String[]{"renaissance_default"})
    };

    public static final String[] EXTERIOR_MODEL_NAMES = {
            "police_box"
    };

    public static final String[] EXTERIOR_TEXTURE_NAMES = {
            "police_box_default",
            "police_box_coral",
            "police_box_renaissance",
            "police_box_crystalline",
            "police_box_future"
    };

    private AitVariants() {
    }

    public static int wrap(int index, int size) {
        if (size <= 0) {
            return 0;
        }

        int wrapped = index % size;
        return wrapped < 0 ? wrapped + size : wrapped;
    }
}

