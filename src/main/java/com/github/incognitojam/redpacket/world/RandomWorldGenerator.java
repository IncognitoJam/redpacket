package com.github.incognitojam.redpacket.world;

import org.joml.Vector3i;

import java.util.Random;

import static com.github.incognitojam.redpacket.world.Chunk.CHUNK_SIZE;

public class RandomWorldGenerator extends WorldGenerator {
    private final int minHeight;
    private final int range;
    private final Random random;

    public RandomWorldGenerator(int seed, int minHeight, int range) {
        super(seed);
        this.minHeight = minHeight;
        this.range = range;
        random = new Random();
    }

    @Override
    public String[] getBlocks(Vector3i chunkPosition) {
        final String[] blocks = new String[CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE];

        int index = 0;
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                final int localSeed = seed * 31 + chunkPosition.hashCode() * 31 + x * 31 + z;
                random.setSeed(localSeed);
                final int height = minHeight + random.nextInt(range);

                for (int y = 0; y < CHUNK_SIZE; y++, index++) {
                    String id;
                    if (CHUNK_SIZE * chunkPosition.y + y <= height) {
                        id = "dirt";
                    } else {
                        id = "air";
                    }
                    blocks[index] = id;
                }
            }
        }

        return blocks;
    }
}
