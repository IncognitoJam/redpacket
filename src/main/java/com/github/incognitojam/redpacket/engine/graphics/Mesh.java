package com.github.incognitojam.redpacket.engine.graphics;

import com.github.incognitojam.redpacket.engine.graphics.array.VertexArray;
import com.github.incognitojam.redpacket.engine.graphics.buffer.FloatArrayBuffer;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class Mesh {
    private final VertexArray vao;
    private final FloatArrayBuffer vbo;
    private final int vertexCount;

    public Mesh(float[] positions) {
        FloatBuffer verticesBuffer = null;
        try {
            verticesBuffer = MemoryUtil.memAllocFloat(positions.length);
            vertexCount = positions.length / 3;
            verticesBuffer.put(positions).flip();

            vao = new VertexArray();
            vao.bind();

            vbo = new FloatArrayBuffer(GL_STATIC_DRAW);
            vbo.bind();
            vbo.upload(verticesBuffer);
            vao.attrib(0, 3, GL_FLOAT, false, 0, 0);
            vbo.unbind();

            vao.unbind();
        } finally {
            if (verticesBuffer != null) {
                MemoryUtil.memFree(verticesBuffer);
            }
        }
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void destroy() {
        vbo.destroy();
        vao.destroy();
    }
}
