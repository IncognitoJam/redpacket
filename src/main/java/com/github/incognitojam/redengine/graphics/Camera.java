package com.github.incognitojam.redengine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private static final float FIELD_OF_VIEW = (float) Math.toRadians(60.0F);
    private static final float Z_NEAR = 0.01F;
    private static final float Z_FAR = 1000.0F;

    private final Vector3f position;
    private final Vector3f rotation;
    private final Matrix4f projectionMatrix;
    private final Matrix4f viewMatrix;

    public Camera(float aspectRatio) {
        position = new Vector3f();
        rotation = new Vector3f();
        projectionMatrix = new Matrix4f().perspective(FIELD_OF_VIEW, aspectRatio, Z_NEAR, Z_FAR);
        viewMatrix = new Matrix4f();
    }

    public void move(float offsetX, float offsetY, float offsetZ) {
        if (offsetZ != 0) {
            position.x += (float) Math.sin(rotation.y - 90) * -offsetX;
            position.z += (float) Math.cos(rotation.y - 90) * offsetX;
        }
        if (offsetX != 0) {
            position.x += (float) Math.sin(rotation.y) * -offsetZ;
            position.z += (float) Math.cos(rotation.y) * offsetZ;
        }
        position.y += offsetY;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.set(x, y, z);
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix
            .identity()
            .rotate(rotation.x, new Vector3f(1, 0, 0))
            .rotate(rotation.y, new Vector3f(0, 1, 0))
            .translate(-position.x, -position.y, -position.z);
    }
}
