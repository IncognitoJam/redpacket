package com.github.incognitojam.redpacket.world.generator;

import org.joml.Vector3i;

import java.util.Random;

import static com.github.incognitojam.redpacket.world.Chunk.CHUNK_SIZE;

public class WorldGenerator {
    private static final int MIN_HEIGHT = 8;
    private static final int RANGE = 16;

    private final long seed;
    private final OpenSimplexNoise simplexNoise;

    public WorldGenerator(long seed) {
        this.seed = seed;
        simplexNoise = new OpenSimplexNoise(seed);
    }

    public String[] getBlocks(Vector3i chunkPosition) {
        final Vector3i chunkOrigin = chunkPosition.mul(CHUNK_SIZE);
        final String[] blocks = new String[CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE];

        int index = 0;
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                final double noise = (simplexNoise.eval((chunkOrigin.x + x) / (double) CHUNK_SIZE, (chunkOrigin.z + z) / (double) CHUNK_SIZE) + 1.0) / 2.0;
                final int height = (int) (MIN_HEIGHT + noise * RANGE);

                for (int y = 0; y < CHUNK_SIZE; y++, index++) {
                    final int worldY = chunkOrigin.y + y;
                    String id;
                    if (worldY < height - 2) {
                        id = "stone";
                    } else if (worldY < height) {
                        id = "dirt";
                    } else if (worldY == height) {
                        id = "grass";
                    } else {
                        id = "air";
                    }
                    blocks[index] = id;
                }
            }
        }

        return blocks;
    }

    public long getSeed() {
        return seed;
    }
}
