package com.github.incognitojam.redpacket.entity;

import com.github.incognitojam.redengine.graphics.Mesh;
import com.github.incognitojam.redpacket.world.World;

public class EntityPlayer extends Entity {
    public EntityPlayer(World world) {
        super(world, buildMesh());
    }

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
