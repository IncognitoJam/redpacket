package com.github.incognitojam.redpacket.engine.graphics.buffer;

import static org.lwjgl.opengl.GL15.*;

public abstract class ArrayBuffer<T> extends Buffer<T> {
    public ArrayBuffer(int usage) {
        super(GL_ARRAY_BUFFER, usage);
    }
}
