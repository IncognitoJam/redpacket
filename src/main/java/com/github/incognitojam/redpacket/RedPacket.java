package com.github.incognitojam.redpacket;

import com.github.incognitojam.redpacket.engine.lifecycle.GameLogic;
import com.github.incognitojam.redpacket.engine.graphics.Window;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;

public class RedPacket implements GameLogic {
    private Window window;
    private float color = 0.0F;

    @Override
    public void init() {
        // Setup window.
        window = new Window("Red Packet", 600, 300);
    }

    @Override
    public void update(double interval) {
        window.update();

        if (window.isKeyPressed(GLFW_KEY_UP)) {
            color += interval;
            if (color > 1.0F)
                color = 1.0F;
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            color -= interval;
            if (color < 0.0F)
                color = 0.0F;
        }

        window.setClearColor(color, color, color, 1.0F);
        window.setTitle(String.format("Red Packet: %f", color));
        window.setHeight(300 + (int) (300 * color));
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
