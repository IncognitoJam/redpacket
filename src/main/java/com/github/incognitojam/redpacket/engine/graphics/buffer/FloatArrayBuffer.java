package com.github.incognitojam.redpacket.engine.graphics.buffer;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;

public class FloatArrayBuffer extends ArrayBuffer<FloatBuffer> {
    public FloatArrayBuffer(int usage) {
        super(usage);
    }

    @Override
    public void upload(FloatBuffer data) {
        glBufferData(vboId, data, usage);
    }
}
