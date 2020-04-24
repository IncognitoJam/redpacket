package com.github.incognitojam.redpacket;

import com.github.incognitojam.redpacket.engine.graphics.Mesh;
import com.github.incognitojam.redpacket.engine.graphics.ShaderProgram;
import com.github.incognitojam.redpacket.engine.graphics.Window;
import com.github.incognitojam.redpacket.engine.lifecycle.GameLogic;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.glDisable;

public class RedPacket implements GameLogic {
    private Window window;
    private ShaderProgram shader;
    private Mesh mesh;
    private float color = 0.0F;

    @Override
    public void init() throws Exception {
        window = new Window("Red Packet", 600, 300);

        shader = new ShaderProgram();
        shader.addVertexShader(Files.readString(Paths.get("basic.vert")));
        shader.addFragmentShader(Files.readString(Paths.get("basic.frag")));
        shader.link();

        final float[] positions = new float[] {
            -0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f, 0.5f, 0.0f,
        };
        final int[] indices = new int[] {
            0, 1, 3, 3, 1, 2,
        };
        mesh = new Mesh(positions, indices);

        glDisable(GL_CULL_FACE);
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
    }

    @Override
    public void render() {
        window.clear();

        shader.bind();

        mesh.bind();
        mesh.draw();
        mesh.unbind();

        shader.unbind();

        window.swapBuffers();
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
