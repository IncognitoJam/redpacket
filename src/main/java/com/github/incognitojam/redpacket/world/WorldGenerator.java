package com.github.incognitojam.redpacket.world;

import org.joml.Vector3i;

public abstract class WorldGenerator {
    protected final int seed;

    public WorldGenerator(int seed) {
        this.seed = seed;
    }

    public abstract String[] getBlocks(Vector3i chunkPosition);
}
