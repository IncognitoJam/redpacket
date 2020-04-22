package com.github.incognitojam.redpacket;

import com.github.incognitojam.redpacket.engine.GameLogic;
import com.github.incognitojam.redpacket.engine.Window;

public class RedPacket implements GameLogic {
    private Window window;

    @Override
    public void init() {
        // Setup window.
        window = new Window("Red Packet", 300, 300);
        window.setClearColor(1.0f, 0.0f, 0.0f, 0.0f);
    }

    @Override
    public void update(double interval) {
         window.update();
    }

    @Override
    public void render() {
        window.clear();

        // Draw stuff

        window.swapBuffers();
    }

    @Override
    public void destroy() {
        window.destroy();
    }

    @Override
    public boolean shouldClose() {
        return window.shouldClose();
    }
}
