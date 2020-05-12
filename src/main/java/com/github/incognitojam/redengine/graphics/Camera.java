package com.github.incognitojam.redengine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Camera {
    private static final float FIELD_OF_VIEW = (float) Math.toRadians(60.0F);
    private static final float Z_NEAR = 0.01F;
    private static final float Z_FAR = 1000.0F;

    private final Vector3f position;
    private final Vector3f rotation;
    private final Matrix4f projectionMatrix;
    private final Matrix4f viewMatrix;

    public Camera(float width, float height) {
        position = new Vector3f();
        rotation = new Vector3f();
        projectionMatrix = new Matrix4f().perspective(FIELD_OF_VIEW, width / height, Z_NEAR, Z_FAR);
        viewMatrix = new Matrix4f();
    }

    public void updateViewport(float width, float height) {
        projectionMatrix.identity().perspective(FIELD_OF_VIEW, width / height, Z_NEAR, Z_FAR);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3fc position) {
        this.position.set(position);
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3fc rotation) {
        this.rotation.set(rotation);
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
