package com.github.incognitojam.redpacket.engine.graphics;

import org.joml.Matrix4f;

public class Camera {
    private static final float FIELD_OF_VIEW = (float) Math.toRadians(60.0F);
    private static final float Z_NEAR = 0.01F;
    private static final float Z_FAR = 1000.0F;

    private Matrix4f projectionMatrix;

    public Camera(float aspectRatio) {
        projectionMatrix = new Matrix4f().perspective(FIELD_OF_VIEW, aspectRatio, Z_NEAR, Z_FAR);
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }
}
