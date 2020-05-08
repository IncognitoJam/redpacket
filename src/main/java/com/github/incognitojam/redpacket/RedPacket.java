package com.github.incognitojam.redpacket;

import com.github.incognitojam.redengine.graphics.Camera;
import com.github.incognitojam.redengine.lifecycle.GameLogic;
import com.github.incognitojam.redengine.ui.MouseInput;
import com.github.incognitojam.redengine.ui.Window;
import com.github.incognitojam.redpacket.world.World;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

public class RedPacket implements GameLogic {
    private Window window;
    private MouseInput mouseInput;

    private World world;
    private Camera camera;

    // FPS and UPS counter
    private int frames;
    private int updates;
    private long counterTime;

    @Override
    public void init() throws Exception {
        window = new Window("Red Packet", 1280, 720, false, true);
        mouseInput = new MouseInput();
        mouseInput.init(window);

        world = new World("hello".hashCode());
        world.init();

        camera = new Camera(window.getWidth(), window.getHeight());
        camera.setPosition(0, 10, 0);
    }

    @Override
    public void update(double interval) {
        window.update();
        mouseInput.update(window);

        world.update(interval);

        if (mouseInput.isRightButton()) {
            final Vector2f displacement = mouseInput.getDisplacement();
            camera.rotate(displacement.x * (float) interval, displacement.y * (float) interval, 0);
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
        camera.move(movement);

        updates++;

        final long now = System.currentTimeMillis();
        if (counterTime < now - 1000L) {
            counterTime = now;
            window.setTitle(String.format("Red Packet: %d FPS, %d UPS", frames, updates));
            frames = 0;
            updates = 0;
        }
    }

    @Override
    public void render() {
        window.clear();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            camera.updateViewport(window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        world.render(camera);

        window.swapBuffers();

        frames++;
    }

    @Override
    public void destroy() {
        world.destroy();
        window.destroy();
    }

    @Override
    public boolean shouldClose() {
        return window.shouldClose();
    }
}
