package com.github.incognitojam.redpacket;

import com.github.incognitojam.redengine.graphics.Camera;
import com.github.incognitojam.redengine.graphics.Mesh;
import com.github.incognitojam.redengine.graphics.ShaderProgram;
import com.github.incognitojam.redengine.graphics.Window;
import com.github.incognitojam.redengine.lifecycle.GameLogic;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.glViewport;

public class RedPacket implements GameLogic {
    private Window window;
    private ShaderProgram shader;
    private Camera camera;
    private Mesh mesh;

    private int frames;
    private long frameTime;

    @Override
    public void init() throws Exception {
        window = new Window("Red Packet", 600, 300, false);

        shader = new ShaderProgram();
        shader.addVertexShader(Files.readString(Paths.get("basic.vert")));
        shader.addFragmentShader(Files.readString(Paths.get("basic.frag")));
        shader.link();

        shader.createUniform("projectionMatrix");

        final float aspectRatio = (float) window.getWidth() / (float) window.getHeight();
        camera = new Camera(aspectRatio);

        final float[] positions = new float[] {
            -0.5f, 0.5f, -1.05f,
            -0.5f, -0.5f, -1.05f,
            0.5f, -0.5f, -1.05f,
            0.5f, 0.5f, -1.05f,
        };
        final int[] indices = new int[] {
            0, 1, 3, 3, 1, 2,
        };
        float[] colours = new float[] {
            0.5f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f,
            0.0f, 0.0f, 0.5f,
            0.0f, 0.5f, 0.5f,
        };
        mesh = new Mesh(positions, colours, indices);
    }

    @Override
    public void update(double interval) {
        window.update();

        final long now = System.currentTimeMillis();
        if (frameTime < now - 1000L) {
            frameTime = now;
            window.setTitle(String.format("Red Packet: %d FPS", frames));
            frames = 0;
        }
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

        mesh.bind();
        mesh.draw();
        mesh.unbind();

        shader.unbind();

        window.swapBuffers();

        frames++;
    }

    @Override
    public void destroy() {
        mesh.destroy();

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
