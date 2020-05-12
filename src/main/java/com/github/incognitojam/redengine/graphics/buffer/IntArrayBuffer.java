package com.github.incognitojam.redengine.graphics.buffer;

import org.jetbrains.annotations.NotNull;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.glBufferData;

public class IntArrayBuffer extends ArrayBuffer<IntBuffer> {
    public IntArrayBuffer(int target, int usage) {
        super(target, usage);
    }

    @Override
    public void upload(@NotNull IntBuffer data) {
        glBufferData(target, data, usage);
    }
}
