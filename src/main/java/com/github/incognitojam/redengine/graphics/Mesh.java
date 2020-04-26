package com.github.incognitojam.redengine.graphics;

import com.github.incognitojam.redengine.graphics.array.VertexArray;
import com.github.incognitojam.redengine.graphics.buffer.FloatArrayBuffer;
import com.github.incognitojam.redengine.graphics.buffer.IntArrayBuffer;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class Mesh {
    private final VertexArray vao;
    private final FloatArrayBuffer positionsVbo;
    private final FloatArrayBuffer texCoordsVbo;
    private final IntArrayBuffer indicesVbo;
    private final Texture texture;
    private final int vertexCount;

    public Mesh(float[] positions, float[] texCoords, int[] indices, Texture texture) {
        this.texture = texture;
        vertexCount = indices.length;

        FloatBuffer positionsBuffer = null;
        FloatBuffer texCoordsBuffer = null;
        IntBuffer indicesBuffer = null;
        try {
            vao = new VertexArray();
            vao.bind();

            positionsVbo = new FloatArrayBuffer(GL_ARRAY_BUFFER, GL_STATIC_DRAW);
            positionsBuffer = MemoryUtil.memAllocFloat(positions.length);
            positionsBuffer.put(positions).flip();
            positionsVbo.bind();
            positionsVbo.upload(positionsBuffer);
            vao.attrib(0, 3, GL_FLOAT, false, 0, 0);
            positionsVbo.unbind();

            texCoordsVbo = new FloatArrayBuffer(GL_ARRAY_BUFFER, GL_STATIC_DRAW);
            texCoordsBuffer = MemoryUtil.memAllocFloat(texCoords.length);
            texCoordsBuffer.put(texCoords).flip();
            texCoordsVbo.bind();
            texCoordsVbo.upload(texCoordsBuffer);
            vao.attrib(1, 2, GL_FLOAT, false, 0, 0);
            texCoordsVbo.unbind();

            indicesVbo = new IntArrayBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_STATIC_DRAW);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            indicesVbo.bind();
            indicesVbo.upload(indicesBuffer);
            indicesVbo.unbind();

            vao.unbind();
        } finally {
            if (positionsBuffer != null) {
                MemoryUtil.memFree(positionsBuffer);
            }
            if (texCoordsBuffer != null) {
                MemoryUtil.memFree(texCoordsBuffer);
            }
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
        }
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void bind() {
        texture.bind();
        vao.bind();
        positionsVbo.bind();
        texCoordsVbo.bind();
        indicesVbo.bind();
        vao.enable();
    }

    public void draw() {
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
    }

    public void unbind() {
        vao.disable();
        positionsVbo.unbind();
        texCoordsVbo.unbind();
        indicesVbo.unbind();
        vao.unbind();
        texture.unbind();
    }

    public void destroy() {
        vao.disable();
        positionsVbo.destroy();
        texCoordsVbo.destroy();
        indicesVbo.destroy();
        vao.destroy();
    }
}
