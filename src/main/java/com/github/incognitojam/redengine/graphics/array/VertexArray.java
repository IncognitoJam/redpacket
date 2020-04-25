package com.github.incognitojam.redengine.graphics.array;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class VertexArray {
    private final int vaoId;
    private final List<Integer> attributes;

    public VertexArray() {
        this.vaoId = glGenVertexArrays();
        this.attributes = new ArrayList<>();
    }

    public void bind() {
        glBindVertexArray(vaoId);
    }

    public void enable() {
        for (final int attribute : attributes) {
            glEnableVertexAttribArray(attribute);
        }
    }

    public void attrib(int index, int size, int type, boolean normalized, int stride, long pointer) {
        attributes.add(index);
        glVertexAttribPointer(index, size, type, normalized, stride, pointer);
    }

    public void disable() {
        for (final int attribute : attributes) {
            glDisableVertexAttribArray(attribute);
        }
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    public void destroy() {
        unbind();
        glDeleteVertexArrays(vaoId);
    }
}
