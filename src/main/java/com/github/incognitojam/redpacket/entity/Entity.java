package com.github.incognitojam.redpacket.entity;

import com.github.incognitojam.redengine.graphics.Mesh;
import com.github.incognitojam.redengine.maths.VectorUtils;
import com.github.incognitojam.redpacket.world.Chunk;
import com.github.incognitojam.redpacket.world.World;
import org.joml.*;

public class Entity {
    protected final World world;
    protected final Mesh mesh;
    protected final Vector3f position;
    protected final Vector3f rotation;
    protected final Vector3f scale;
    private final Matrix4f worldMatrix;

    public Entity(World world, Mesh mesh) {
        this.world = world;
        this.mesh = mesh;
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        scale = new Vector3f(1, 1, 1);
        worldMatrix = new Matrix4f();
    }

    public void update(double interval) {
    }

    public void render() {
        mesh.bind();
        mesh.render();
        mesh.unbind();
    }

    public void destroy() {
        mesh.destroy();
    }

    public Vector3fc getPosition() {
        return position;
    }

    public Vector3ic getBlockPosition() {
        return VectorUtils.round(getPosition());
    }

    public Vector3ic getChunkPosition() {
        return VectorUtils.floorDiv(getBlockPosition(), Chunk.CHUNK_SIZE);
    }

    public void setPosition(Vector3fc position) {
        this.position.set(position);
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public Vector3fc getRotation() {
        return rotation;
    }

    public void setRotation(Vector3fc rotation) {
        this.rotation.set(rotation);
    }

    public void setRotation(float x, float y, float z) {
        rotation.set(x, y, z);
    }

    public Vector3fc getScale() {
        return scale;
    }

    public void setScale(float x, float y, float z) {
        scale.set(x, y, z);
    }

    public Mesh getMesh() {
        return mesh;
    }

    public Matrix4fc getWorldMatrix() {
        return worldMatrix
            .identity()
            .translate(position)
            .rotateX(-rotation.x)
            .rotateY(-rotation.y)
            .rotateZ(-rotation.z)
            .scale(scale);
    }
}
