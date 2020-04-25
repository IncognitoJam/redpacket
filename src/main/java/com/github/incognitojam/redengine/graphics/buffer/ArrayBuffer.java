package com.github.incognitojam.redengine.graphics.buffer;

public abstract class ArrayBuffer<T> extends Buffer<T> {
    public ArrayBuffer(int target, int usage) {
        super(target, usage);
    }
}
