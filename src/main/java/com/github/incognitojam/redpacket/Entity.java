package com.github.incognitojam.redpacket;

import com.github.incognitojam.redengine.graphics.Mesh;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Entity {
    private final Mesh mesh;
    private final Vector3f position;
    private final Vector3f rotation;
    private final Vector3f scale;
    private final Matrix4f worldMatrix;

    public Entity(Mesh mesh) {
        this.mesh = mesh;
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        scale = new Vector3f(1, 1, 1);
        worldMatrix = new Matrix4f();
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        rotation.set(x, y, z);
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(float x, float y, float z) {
        scale.set(x, y, z);
    }

    public Mesh getMesh() {
        return mesh;
    }

    public Matrix4f getWorldMatrix() {
        return worldMatrix
            .identity()
            .translate(position)
            .rotateX(rotation.x)
            .rotateY(rotation.y)
            .rotateZ(rotation.z)
            .scale(scale);
    }
}
