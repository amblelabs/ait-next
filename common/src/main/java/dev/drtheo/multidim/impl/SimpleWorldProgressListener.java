package dev.drtheo.multidim.impl;

public class SimpleWorldProgressListener extends AbstractWorldProgressListener {

    private final Runnable onDone;

    public SimpleWorldProgressListener(Runnable onDone) {
        this.onDone = onDone;
    }

    @Override
    public void stop() {
        this.onDone.run();
    }
}

