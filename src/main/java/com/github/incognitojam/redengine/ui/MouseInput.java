package com.github.incognitojam.redengine.ui;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {
    private final Vector2d previousPos;
    private final Vector2d currentPos;
    private final Vector2f displacement;

    private boolean inWindow;
    private boolean leftButton;
    private boolean rightButton;

    public MouseInput(@NotNull Window window) {
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0, 0);
        displacement = new Vector2f();

        // Register GLFW mouse callbacks
        final long handle = window.getHandle();
        glfwSetCursorPosCallback(handle, (h, x, y) -> currentPos.set(x, y));
        glfwSetCursorEnterCallback(handle, (h, entered) -> inWindow = entered);
        glfwSetMouseButtonCallback(handle, (h, button, action, mode) -> {
            leftButton = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            rightButton = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        });
    }

    public void update(@NotNull Window window) {
        displacement.zero();

        if (previousPos.x > 0 && previousPos.y > 0 && inWindow) {
            final double dx = currentPos.x - previousPos.x;
            final double dy = currentPos.y - previousPos.y;

            if (dx != 0.0) {
                displacement.y = (float) dx;
            }
            if (dy != 0.0) {
                displacement.x = (float) dy;
            }
        }

        previousPos.set(currentPos);
    }

    public Vector2f getDisplacement() {
        return displacement;
    }

    public boolean isLeftButton() {
        return leftButton;
    }

    public boolean isRightButton() {
        return rightButton;
    }
}
