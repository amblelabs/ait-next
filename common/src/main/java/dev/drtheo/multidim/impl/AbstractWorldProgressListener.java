package dev.drtheo.multidim.impl;

import net.minecraft.network.chat.Component;
import net.minecraft.util.ProgressListener;

public abstract class AbstractWorldProgressListener implements ProgressListener {

    @Override
    public void progressStartNoAbort(Component title) { }

    @Override
    public void progressStart(Component title) { }

    @Override
    public void progressStage(Component stage) { }

    @Override
    public void progressStagePercentage(int percentage) { }

    @Override
    public void stop() { }
}

