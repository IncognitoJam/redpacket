package com.github.incognitojam.redpacket;

import com.github.incognitojam.redengine.graphics.*;
import com.github.incognitojam.redengine.lifecycle.GameLogic;
import com.github.incognitojam.redengine.ui.MouseInput;
import com.github.incognitojam.redengine.ui.Window;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

public class RedPacket implements GameLogic {
    private Window window;
    private ShaderProgram shader;
    private Camera camera;
    private MouseInput mouseInput;

    private Entity entity;
    private Matrix4f modelViewMatrix;

    // FPS counter
    private int frames;
    private long frameTime;

    @Override
    public void init() throws Exception {
        window = new Window("Red Packet", 600, 300, false);

        shader = new ShaderProgram();
        shader.addVertexShader(Files.readString(Paths.get("shaders/basic.vert")));
        shader.addFragmentShader(Files.readString(Paths.get("shaders/basic.frag")));
        shader.link();

        shader.createUniform("projectionMatrix");
        shader.createUniform("modelViewMatrix");
        shader.createUniform("textureSampler");

        final float aspectRatio = (float) window.getWidth() / (float) window.getHeight();
        camera = new Camera(aspectRatio);
        mouseInput = new MouseInput();
        mouseInput.init(window);

        final float[] positions = new float[] {
            // 0 (front bottom left)
            -0.5F, -0.5F, 0.5F,
            // 1 (front bottom right)
            0.5F, -0.5F, 0.5F,
            // 2 (front top right)
            0.5F, 0.5F, 0.5F,
            // 3 (front top left)
            -0.5F, 0.5F, 0.5F,
            // 4 (back bottom right)
            0.5F, -0.5F, -0.5F,
            // 5 (back bottom left)
            -0.5F, -0.5F, -0.5F,
            // 6 (back top left)
            -0.5F, 0.5F, -0.5F,
            // 7 (back top right)
            0.5F, 0.5F, -0.5F
        };
        final float[] texCoords = new float[] {
            0.0F, 0.5F,
            0.5F, 0.5F,
            0.5F, 0.0F,
            0.0F, 0.0F,
            0.0F, 0.5F,
            0.5F, 0.5F,
            0.5F, 0.0F,
            0.0F, 0.0F
        };
        final int[] indices = new int[] {
            // Front
            0, 1, 2, 2, 3, 0,
            // Top
            3, 2, 7, 7, 6, 3,
            // Right
            1, 4, 7, 7, 2, 1,
            // Left
            5, 0, 3, 3, 6, 5,
            // Bottom
            5, 4, 1, 1, 0, 5,
            // Back
            4, 5, 6, 6, 7, 4
        };
        Texture texture = new Texture("textures/grass.png");
        Mesh mesh = new Mesh(positions, texCoords, indices, texture);
        entity = new Entity(mesh);
        entity.setPosition(0, 0, -2F);
        modelViewMatrix = new Matrix4f();
    }

    @Override
    public void update(double interval) {
        window.update();
        mouseInput.update(window);

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

        final long now = System.currentTimeMillis();
        if (frameTime < now - 1000L) {
            frameTime = now;
            window.setTitle(String.format("Red Packet: %d FPS", frames));
            frames = 0;
        }

        final float rotation = (float) (entity.getRotation().x + Math.PI * interval);
        entity.setRotation(rotation, rotation, rotation);
    }

    @Override
    public void render() {
        window.clear();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shader.bind();
        shader.setUniform("projectionMatrix", camera.getProjectionMatrix());
        shader.setUniform("textureSampler", 0);

        camera.getViewMatrix().get(modelViewMatrix);
        modelViewMatrix.mul(entity.getWorldMatrix());
        shader.setUniform("modelViewMatrix", modelViewMatrix);
        entity.getMesh().bind();
        entity.getMesh().draw();
        entity.getMesh().unbind();

        shader.unbind();

        window.swapBuffers();

        frames++;
    }

    @Override
    public void destroy() {
        entity.getMesh().destroy();

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
