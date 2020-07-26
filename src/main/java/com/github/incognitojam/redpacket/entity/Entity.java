package com.github.incognitojam.redpacket.entity;

import com.github.incognitojam.redengine.graphics.Mesh;
import com.github.incognitojam.redengine.maths.VectorUtils;
import com.github.incognitojam.redengine.physics.PhysicsBody;
import com.github.incognitojam.redpacket.world.Chunk;
import com.github.incognitojam.redpacket.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

public class Entity extends PhysicsBody {
    @NotNull
    protected final World world;
    @Nullable
    protected final Mesh mesh;

    @NotNull
    protected final Vector3f position;
    @NotNull
    protected final Vector3f rotation;
    @NotNull
    protected final Vector3f scale;

    @NotNull
    protected final Vector3fc dimensions;
    @NotNull
    protected final AABBf boundingBox;
    @NotNull
    protected final Mesh boundingMesh;

    @NotNull
    private final Matrix4f worldMatrix;

    /**
     * Create a new Entity.
     *
     * @param world      the world containing this entity.
     * @param mesh       the mesh which should be rendered at this entity's position.
     * @param dimensions the dimensions of this entity's bounding box.
     */
    public Entity(@NotNull World world, @Nullable Mesh mesh, @NotNull Vector3fc dimensions, float mass) {
        super(mass);
        this.world = world;
        this.mesh = mesh;
        this.dimensions = dimensions;

        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        scale = new Vector3f(1, 1, 1);

        boundingBox = new AABBf(position, dimensions);
        boundingMesh = createBoundingBoxMesh(dimensions);

        worldMatrix = new Matrix4f();
    }

    /**
     * Update this entity.
     * <p>
     * Invoked by the {@link World} when an update occurs.
     * <p>
     * This method should be overridden by subclasses to subscribe to update
     * events.
     *
     * @param interval the time interval in seconds since the last update.
     */
    public void update(double interval) {
        applyForce(new Vector3f(0, -1F, 0));
        super.update(interval);
    }

    /**
     * Render this entity.
     * <p>
     * Automatically binds, renders and unbinds the mesh. This method should be
     * overridden by subclasses adding additional features.
     */
    public void render() {
        if (mesh != null) {
            mesh.bind();
            mesh.render();
            mesh.unbind();
        }
    }

    /**
     * Render this entity's bounds.
     * <p>
     * Used for debugging.
     */
    public final void renderBounds() {
        boundingMesh.bind();
        boundingMesh.render();
        boundingMesh.unbind();
    }

    /**
     * Clean up resources used by this entity.
     */
    public void destroy() {
        if (mesh != null) {
            mesh.destroy();
        }
    }

    /**
     * Get the world this entity is located in.
     *
     * @return the world object containing this entity.
     */
    @NotNull
    public final World getWorld() {
        return world;
    }

    /**
     * Get the position of this entity.
     *
     * @return the float coordinates of the position of this entity.
     */
    @Override
    @NotNull
    public final Vector3fc getPosition() {
        return position;
    }

    /**
     * Get the block position of this entity.
     *
     * @return the integer coordinates of the block containing this entity.
     */
    @NotNull
    public final Vector3ic getBlockPosition() {
        return VectorUtils.round(getPosition());
    }

    /**
     * Get the position of the chunk containing this entity.
     *
     * @return the integer coordinates of the chunk containing this entity.
     */
    @NotNull
    public final Vector3ic getChunkPosition() {
        return VectorUtils.floorDiv(getBlockPosition(), Chunk.CHUNK_SIZE);
    }

    /**
     * Attempt to move the entity to a new position.
     * <p>
     * Tests whether the position is occupied by a block before updating the
     * position of the entity. If it is occupied, returns {@code false}
     * otherwise sets the new position and returns {@code true}.
     *
     * @param position the new position of this entity.
     * @return {@code true} if the update was successful, otherwise {@code false}.
     */
    @Override
    public boolean updatePosition(@NotNull Vector3fc position) {
        final Vector3ic blockPosition = VectorUtils.round(position);
        final String blockId = world.getBlockId(blockPosition);
        if (!"air".equals(blockId)) {
            return false;
        }

        setPosition(position);
        return true;
    }

    /**
     * Set this entity's position.
     *
     * @param position the new position of this entity.
     */
    public void setPosition(@NotNull Vector3fc position) {
        this.position.set(position);
        updateBoundingBox();
    }

    /**
     * Get the rotation vector for this entity.
     *
     * @return a float vector representing the rotation of this entity in each
     * axis.
     */
    @NotNull
    public final Vector3fc getRotation() {
        return rotation;
    }

    /**
     * Update this entity's rotation.
     *
     * @param rotation the new rotation of this entity.
     */
    public void setRotation(@NotNull Vector3fc rotation) {
        this.rotation.set(rotation);
    }

    /**
     * Get the scale vector for this entity.
     *
     * @return a float vector representing the scaling of this entity in each
     * axis.
     */
    @NotNull
    public final Vector3fc getScale() {
        return scale;
    }

    /**
     * Set the scaling of this entity.
     *
     * @param scale The magnitude of the scaling in each axis.
     */
    public void setScale(@NotNull Vector3fc scale) {
        this.scale.set(scale);
    }

    /**
     * Get the dimensions of this entity.
     *
     * @return a float vector representing dimensions of this entity in each axis.
     */
    @NotNull
    public final Vector3fc getDimensions() {
        return dimensions;
    }

    /**
     * Get the bounding box for this entity.
     * <p>
     * Used for calculating collisions.
     *
     * @return an {@link AABBf} object representing the bounds of this entity.
     */
    @NotNull
    public final AABBf getBoundingBox() {
        return boundingBox;
    }

    /**
     * Get the mesh occupying the bounding box for this entity.
     * <p>
     * Used for drawing collision boxes in debug mode.
     *
     * @return a {@link Mesh} positioned and resized to match this entity's
     * bounding box.
     */
    @NotNull
    public final Mesh getBoundingMesh() {
        return boundingMesh;
    }

    @NotNull
    public final Matrix4fc getWorldMatrix() {
        return worldMatrix
            .identity()
            .translate(position)
            .rotateX(-rotation.x)
            .rotateY(-rotation.y)
            .rotateZ(-rotation.z)
            .scale(scale);
    }

    protected final void updateBoundingBox() {
        boundingBox.setMin(position);
        boundingBox.setMax(position.add(dimensions, new Vector3f()));
    }

    @NotNull
    private static Mesh createBoundingBoxMesh(@NotNull Vector3fc dimensions) {
        final float halfWidth = dimensions.x() / 2.0F;
        final float halfHeight = dimensions.y() / 2.0F;
        final float halfDepth = dimensions.z() / 2.0F;

        // TODO: remove texture from bounding box mesh
        final float[] positions = new float[] {
            // 0 (front bottom left)
            -halfWidth, -halfHeight, halfDepth,
            // 1 (front bottom right)
            halfWidth, -halfHeight, halfDepth,
            // 2 (front top right)
            halfWidth, halfHeight, halfDepth,
            // 3 (front top left)
            -halfWidth, halfHeight, halfDepth,
            // 4 (back bottom right)
            halfWidth, -halfHeight, -halfDepth,
            // 5 (back bottom left)
            -halfWidth, -halfHeight, -halfDepth,
            // 6 (back top left)
            -halfWidth, halfHeight, -halfDepth,
            // 7 (back top right)
            halfWidth, halfHeight, -halfDepth
        };
        final float[] texCoords = new float[] {
            0.0F, 0.5F,
            0.5F, 0.5F,
            0.5F, 0.0F,
            0.0F, 0.0F,
            0.0F, 0.5F,
            0.5F, 0.5F,
            0.5F, 0.0F,
            0.0F, 0.0F
        };
        final int[] indices = new int[] {
            // Front
            0, 1, 2, 2, 3, 0,
            // Top
            3, 2, 7, 7, 6, 3,
            // Right
            1, 4, 7, 7, 2, 1,
            // Left
            5, 0, 3, 3, 6, 5,
            // Bottom
            5, 4, 1, 1, 0, 5,
            // Back
            4, 5, 6, 6, 7, 4
        };
        return new Mesh(positions, texCoords, indices);
    }
}
