package com.github.incognitojam.redpacket.world;

import com.github.incognitojam.redengine.graphics.Camera;
import com.github.incognitojam.redengine.graphics.ShaderProgram;
import com.github.incognitojam.redengine.graphics.TextureMap;
import com.github.incognitojam.redengine.maths.VectorUtils;
import com.github.incognitojam.redpacket.entity.Entity;
import com.github.incognitojam.redpacket.entity.EntityPlayer;
import com.github.incognitojam.redpacket.world.generator.WorldGenerator;
import org.joml.Matrix4f;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.LinkedTransferQueue;

import static com.github.incognitojam.redpacket.world.Chunk.CHUNK_SIZE;

public class World {
    private final WorldGenerator generator;
    private final HashMap<Vector3ic, Chunk> chunkMap;
    private final Queue<Vector3ic> chunkQueue;
    private final List<Entity> entities;

    private final TextureMap textureMap;
    private final ShaderProgram shader;
    private final Matrix4f modelViewMatrix;

    public World(long seed) throws Exception {
        generator = new WorldGenerator(seed);
        chunkMap = new HashMap<>();
        chunkQueue = new LinkedTransferQueue<>();
        entities = new ArrayList<>();

        textureMap = new TextureMap("textures/blocks.png", 16);
        shader = new ShaderProgram();
        shader.addVertexShader(Files.readString(Paths.get("shaders/basic.vert")));
        shader.addFragmentShader(Files.readString(Paths.get("shaders/basic.frag")));
        shader.link();
        shader.createUniform("projectionMatrix");
        shader.createUniform("modelViewMatrix");
        shader.createUniform("textureSampler");
        modelViewMatrix = new Matrix4f();
    }

    public WorldGenerator getGenerator() {
        return generator;
    }

    public Chunk getChunk(Vector3ic position) {
        return chunkMap.get(position);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public List<Entity> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    public String getBlockId(Vector3ic position) {
        if (position.y() < 0) {
            /*
             * FIXME: I return null to avoid generating faces for the bottom of
             *  the world since faces are only added to chunk meshes when they
             *  border air blocks. This needs a better solution.
             */
            return null;
        }

        // Get the chunk containing this block position.
        final Vector3ic chunkPosition = VectorUtils.floorDiv(position, CHUNK_SIZE);
        final Chunk chunk = getChunk(chunkPosition);
        if (chunk == null) {
            return "air";
        }

        // Get the block id from the chunk.
        final Vector3ic localPosition = VectorUtils.floorMod(position, CHUNK_SIZE);
        return chunk.getBlockId(localPosition);
    }

    public void init() {
        /*
         * Add chunks around spawn to the chunk queue so that they can start
         * being created immediately.
         */
        for (int x = -2; x < 2; x++) {
            for (int z = -2; z < 2; z++) {
                for (int y = 0; y < 4; y++) {
                    chunkQueue.add(new Vector3i(x, y, z));
                }
            }
        }
    }

    public void update(double interval) {
        // Generate new chunks for about 5 milliseconds.
        final long startTime = System.currentTimeMillis();
        do {
            // Check the queue for new chunks to generate.
            final Vector3ic position = chunkQueue.poll();
            if (position == null) {
                // No elements in the queue, abort.
                break;
            }

            if (chunkMap.containsKey(position)) {
                // We've already generated this chunk.
                continue;
            }

            // Create the new chunk object and add it to the chunk map.
            final Chunk chunk = new Chunk(this, position);
            chunk.init(textureMap);
            chunkMap.put(position, chunk);

            /*
             * Force neighbouring chunks to update their meshes, removing
             * redundant faces.
             */
            for (final Vector3ic neighbourPosition : VectorUtils.getNeighbours(position)) {
                final Chunk neighbourChunk = chunkMap.get(neighbourPosition);

                /*
                 * The neighbouring chunk might not have been generated
                 * yet, so check for null values.
                 */
                if (neighbourChunk != null) {
                    neighbourChunk.setOutdatedMesh(true);
                }
            }
        } while (System.currentTimeMillis() < startTime + 2L);

        /*
         * Update all of the chunks.
         *
         * TODO: only update chunks around the player
         */
        for (final Chunk chunk : chunkMap.values()) {
            chunk.update(interval);
        }

        // Update all of the entities.
        for (final Entity entity : entities) {
            entity.update(interval);

            if (entity instanceof EntityPlayer) {
                final Vector3ic currentChunk = entity.getChunkPosition();
                chunkQueue.add(currentChunk);

                final Vector3ic[] neighbouringChunks = VectorUtils.getNeighbours(currentChunk);
                chunkQueue.addAll(Arrays.asList(neighbouringChunks));
            }
        }
    }

    public void render(Camera camera) {
        shader.bind();
        shader.setUniform("projectionMatrix", camera.getProjectionMatrix());
        shader.setUniform("textureSampler", 0);
        textureMap.bind();

        /*
         * Render all of the chunks.
         *
         * TODO: only render chunks around the player
         */
        for (final Chunk chunk : chunkMap.values()) {
            camera.getViewMatrix().get(modelViewMatrix);
            modelViewMatrix.mul(chunk.getWorldMatrix());
            shader.setUniform("modelViewMatrix", modelViewMatrix);
            chunk.render();
        }

        for (final Entity entity : entities) {
            camera.getViewMatrix().get(modelViewMatrix);
            modelViewMatrix.mul(entity.getWorldMatrix());
            shader.setUniform("modelViewMatrix", modelViewMatrix);
            entity.render();
        }

        textureMap.unbind();
        shader.unbind();
    }

    public void destroy() {
        for (final Chunk chunk : chunkMap.values()) {
            chunk.destroy();
        }
        for (final Entity entity : entities) {
            entity.destroy();
        }

        shader.destroy();
        textureMap.destroy();
    }
}
