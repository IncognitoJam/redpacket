package com.github.incognitojam.redengine.graphics.buffer;

import org.jetbrains.annotations.NotNull;

import static org.lwjgl.opengl.GL15.*;

public abstract class Buffer<T> {
    protected final int target;
    protected final int usage;
    protected final int vboId;

    public Buffer(int target, int usage) {
        this.target = target;
        this.usage = usage;
        this.vboId = glGenBuffers();
    }

    public void bind() {
        glBindBuffer(target, vboId);
    }

    public abstract void upload(@NotNull T data);

    public void unbind() {
        glBindBuffer(target, 0);
    }

    public void destroy() {
        unbind();
        glDeleteBuffers(vboId);
    }
}
