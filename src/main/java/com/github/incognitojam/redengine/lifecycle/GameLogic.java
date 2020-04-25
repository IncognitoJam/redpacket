package com.github.incognitojam.redengine.lifecycle;

public interface GameLogic {
    void init() throws Exception;

    void update(final double interval);

    void render();

    void destroy();

    boolean shouldClose();
}
