package com.github.incognitojam.redpacket.engine.graphics.array;

import static org.lwjgl.opengl.GL30.*;

public class VertexArray {
    private final int vaoId;

    public VertexArray() {
        this.vaoId = glGenVertexArrays();
    }

    public void bind() {
        glBindVertexArray(vaoId);
    }

    public void attrib(int index, int size, int type, boolean normalized, int stride, long pointer) {
        glVertexAttribPointer(index, size, type, normalized, stride, pointer);
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    public void destroy() {
        unbind();
        glDeleteVertexArrays(vaoId);
    }
}
