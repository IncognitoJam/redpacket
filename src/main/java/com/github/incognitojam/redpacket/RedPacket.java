package com.github.incognitojam.redpacket;

import com.github.incognitojam.redengine.graphics.Camera;
import com.github.incognitojam.redengine.graphics.ShaderProgram;
import com.github.incognitojam.redengine.graphics.TextureMap;
import com.github.incognitojam.redengine.lifecycle.GameLogic;
import com.github.incognitojam.redengine.ui.MouseInput;
import com.github.incognitojam.redengine.ui.Window;
import com.github.incognitojam.redpacket.world.Chunk;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

public class RedPacket implements GameLogic {
    private Window window;
    private ShaderProgram shader;
    private Camera camera;
    private MouseInput mouseInput;

    private TextureMap textureMap;
    private Chunk chunk;
    private Matrix4f modelViewMatrix;

    // FPS and UPS counter
    private int frames;
    private int updates;
    private long counterTime;

    @Override
    public void init() throws Exception {
        window = new Window("Red Packet", 600, 300, true);

        shader = new ShaderProgram();
        shader.addVertexShader(Files.readString(Paths.get("shaders/basic.vert")));
        shader.addFragmentShader(Files.readString(Paths.get("shaders/basic.frag")));
        shader.link();

        shader.createUniform("projectionMatrix");
        shader.createUniform("modelViewMatrix");
        shader.createUniform("textureSampler");

        camera = new Camera(window.getWidth(), window.getHeight());
        mouseInput = new MouseInput();
        mouseInput.init(window);

        textureMap = new TextureMap("textures/grass.png", 2);
        chunk = new Chunk(new Vector3i());
        chunk.init(textureMap);
        modelViewMatrix = new Matrix4f();
    }

    @Override
    public void update(double interval) {
        window.update();
        mouseInput.update(window);

        chunk.update();

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

        shader.bind();
        shader.setUniform("projectionMatrix", camera.getProjectionMatrix());
        shader.setUniform("textureSampler", 0);

        textureMap.bind();

        camera.getViewMatrix().get(modelViewMatrix);
        modelViewMatrix.mul(chunk.getWorldMatrix());
        shader.setUniform("modelViewMatrix", modelViewMatrix);
        chunk.render();

        textureMap.unbind();

        shader.unbind();

        window.swapBuffers();

        frames++;
    }

    @Override
    public void destroy() {
        textureMap.destroy();
        chunk.destroy();

        if (shader != null) {
            shader.destroy();
        }

        window.destroy();
    }

    @Override
    public boolean shouldClose() {
        return window.shouldClose();
    }
}
