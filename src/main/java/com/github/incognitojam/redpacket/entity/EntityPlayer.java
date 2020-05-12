package com.github.incognitojam.redpacket.entity;

import com.github.incognitojam.redengine.graphics.Mesh;
import com.github.incognitojam.redpacket.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class EntityPlayer extends Entity {
    private static final Vector3fc DIMENSIONS = new Vector3f(0.8f, 1.8f, 0.8f);

    @NotNull
    protected final String name;

    public EntityPlayer(@NotNull World world, @NotNull String name) {
        super(world, buildMesh(), DIMENSIONS);
        this.name = name;
    }

    /**
     * Get the player's name.
     *
     * @return the name of the player.
     */
    @NotNull
    public final String getName() {
        return name;
    }

    /**
     * Apply a movement vector to the player.
     * <p>
     * Moves the player appropriately considering their rotation.
     *
     * @param movement The magnitude of the movement in each axis.
     */
    public void move(@NotNull Vector3fc movement) {
        final Vector3f position = new Vector3f(getPosition());

        if (movement.x() != 0) {
            position.x += (float) Math.sin(rotation.y - (Math.PI / 2)) * -movement.x();
            position.z += (float) Math.cos(rotation.y - (Math.PI / 2)) * movement.x();
        }
        if (movement.z() != 0) {
            position.x += (float) Math.sin(rotation.y) * -movement.z();
            position.z += (float) Math.cos(rotation.y) * movement.z();
        }
        position.y += movement.y();

        setPosition(position);
    }

    // TODO: add real player model
    @NotNull
    private static Mesh buildMesh() {
        final float[] positions = new float[] {
            // 0 (front bottom left)
            -0.5F, -0.5F, 0.5F,
            // 1 (front bottom right)
            0.5F, -0.5F, 0.5F,
            // 2 (front top right)
            0.5F, 0.5F, 0.5F,
            // 3 (front top left)
            -0.5F, 0.5F, 0.5F,
            // 4 (back bottom right)
            0.5F, -0.5F, -0.5F,
            // 5 (back bottom left)
            -0.5F, -0.5F, -0.5F,
            // 6 (back top left)
            -0.5F, 0.5F, -0.5F,
            // 7 (back top right)
            0.5F, 0.5F, -0.5F
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
