package com.github.incognitojam.redengine;

public class Timer {
    private double previousTime;

    public void init() {
        previousTime = getTime();
    }

    public double getTime() {
        return System.nanoTime() / 1_000_000_000.0;
    }

    public float getElapsedTime() {
        final double time = getTime();
        float elapsedTime = (float) (time - previousTime);
        previousTime = time;
        return elapsedTime;
    }

    public double getPreviousTime() {
        return previousTime;
    }
}
