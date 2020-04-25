package com.github.incognitojam.redengine.graphics.buffer;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.glBufferData;

public class FloatArrayBuffer extends ArrayBuffer<FloatBuffer> {
    public FloatArrayBuffer(int target, int usage) {
        super(target, usage);
    }

    @Override
    public void upload(FloatBuffer data) {
        glBufferData(target, data, usage);
    }
}
