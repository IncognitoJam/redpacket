package com.github.incognitojam.redpacket.entity;

import com.github.incognitojam.redengine.graphics.Camera;
import com.github.incognitojam.redengine.ui.MouseInput;
import com.github.incognitojam.redengine.ui.Window;
import com.github.incognitojam.redpacket.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

public class LocalPlayer extends EntityPlayer {
    protected final Window window;
    protected final MouseInput mouseInput;
    protected final Camera camera;

    public LocalPlayer(@NotNull World world, @NotNull String name, @NotNull Window window) {
        super(world, name);
        this.window = window;
        mouseInput = new MouseInput(window);
        camera = new Camera(window.getWidth(), window.getHeight());
    }

    @Override
    public void update(double interval) {
        mouseInput.update(window);

        if (mouseInput.isRightButton()) {
            final Vector2f displacement = mouseInput.getDisplacement();
            final Vector3f rotation = getRotation().add(displacement.x * (float) interval, displacement.y * (float) interval, 0, new Vector3f());
            setRotation(rotation);
        }

        final Vector3f movement = new Vector3f();
        if (window.isKeyPressed(GLFW_KEY_W)) {
            movement.z -= 1;
        }
        if (window.isKeyPressed(GLFW_KEY_S)) {
            movement.z += 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            movement.x -= 1;
        }
        if (window.isKeyPressed(GLFW_KEY_D)) {
            movement.x += 1;
        }
        if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            movement.y += 1;
        }
        if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            movement.y -= 1;
        }
        movement.mul((float) interval);
        movement.mul(1.5f);
        move(movement);

        super.update(interval);
    }

    @Override
    public void render() {
        super.render();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            camera.updateViewport(window.getWidth(), window.getHeight());
            window.setResized(false);
        }
    }

    @NotNull
    public Camera getCamera() {
        return camera;
    }

    @Override
    public void setPosition(@NotNull Vector3fc position) {
        super.setPosition(position);
        camera.setPosition(getPosition());
    }

    @Override
    public void setRotation(@NotNull Vector3fc rotation) {
        super.setRotation(rotation);
        camera.setRotation(getRotation());
    }
}
