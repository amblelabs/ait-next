package dev.amble.ait.client.renderer;

import net.minecraft.client.renderer.ShaderInstance;
import org.jetbrains.annotations.Nullable;

public class AITShaders {

    @Nullable
    private static ShaderInstance accumulationShader;

    public static void setAccumulationShader(@Nullable ShaderInstance shader) {
        accumulationShader = shader;
    }

    @Nullable
    public static ShaderInstance getAccumulationShader() {
        return accumulationShader;
    }
}

