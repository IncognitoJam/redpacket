package com.github.incognitojam.redpacket;

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
    private float color = 0.0F;
    private int vboId, vaoId;

    @Override
    public void init() throws Exception {
        window = new Window("Red Packet", 600, 300);

        shader = new ShaderProgram();
        shader.addVertexShader(Files.readString(Paths.get("basic.vert")));
        shader.addFragmentShader(Files.readString(Paths.get("basic.frag")));
        shader.link();

        float[] vertices = new float[]{
            0.0F, 0.5F, 0.0F,
            -0.5F, -0.5F, 0.0F,
            0.5F, -0.5F, 0.0F
        };

        FloatBuffer verticesBuffer = null;
        try {
            verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
            verticesBuffer.put(vertices).flip();

            // Create the VAO and bind to it
            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            // Create the VBO and bind to it
            vboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

            // Enable location 0
            glEnableVertexAttribArray(0);
            // Define the structure of the data
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            // Unbind the VBO
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            // Unbind the VAO
            glBindVertexArray(0);
        } finally {
            if (verticesBuffer != null) {
                MemoryUtil.memFree(verticesBuffer);
            }
        }
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

        // Bind to the VAO
        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);

        // Draw the vertices
        glDrawArrays(GL_TRIANGLES, 0, 3);

        // Restore sstate
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

        shader.unbind();

        window.swapBuffers();
    }

    @Override
    public void destroy() {
        // Delete the VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(vboId);

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);

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
