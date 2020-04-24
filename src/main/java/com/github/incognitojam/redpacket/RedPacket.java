package com.github.incognitojam.redpacket;

import com.github.incognitojam.redpacket.engine.graphics.Mesh;
import com.github.incognitojam.redpacket.engine.graphics.ShaderProgram;
import com.github.incognitojam.redpacket.engine.lifecycle.GameLogic;
import com.github.incognitojam.redpacket.engine.graphics.Window;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

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

        float[] positions = new float[]{
            -0.5f,  0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f,  0.5f, 0.0f,
            0.5f,  0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
        };
        mesh = new Mesh(positions);
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
