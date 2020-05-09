package com.github.incognitojam.redpacket.world;

import com.github.incognitojam.redengine.graphics.Camera;
import com.github.incognitojam.redengine.graphics.ShaderProgram;
import com.github.incognitojam.redengine.graphics.TextureMap;
import com.github.incognitojam.redengine.maths.VectorUtils;
import com.github.incognitojam.redpacket.entity.Entity;
import com.github.incognitojam.redpacket.entity.Player;
import com.github.incognitojam.redpacket.world.generator.WorldGenerator;
import org.joml.Matrix4f;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.github.incognitojam.redpacket.world.Chunk.CHUNK_SIZE;

public class World {
    private final WorldGenerator generator;

    private final TextureMap textureMap;
    private final HashMap<Vector3ic, Chunk> chunkMap;
    private final List<Entity> entities;
    private final Matrix4f modelViewMatrix;
    private final ShaderProgram shader;

    public World(long seed) throws Exception {
        generator = new WorldGenerator(seed);

        textureMap = new TextureMap("textures/blocks.png", 16);
        chunkMap = new HashMap<>();
        modelViewMatrix = new Matrix4f();

        entities = new ArrayList<>();
        final Player player = new Player();
        player.setPosition(0, 10, 0);
        entities.add(player);

        shader = new ShaderProgram();
        shader.addVertexShader(Files.readString(Paths.get("shaders/basic.vert")));
        shader.addFragmentShader(Files.readString(Paths.get("shaders/basic.frag")));
        shader.link();

        shader.createUniform("projectionMatrix");
        shader.createUniform("modelViewMatrix");
        shader.createUniform("textureSampler");
    }

    public WorldGenerator getGenerator() {
        return generator;
    }

    public Chunk getChunk(Vector3ic position) {
        return chunkMap.get(position);
    }

    public String getBlockId(Vector3ic position) {
        final Vector3ic chunkPosition = VectorUtils.floorDiv(position, CHUNK_SIZE);
        final Chunk chunk = getChunk(chunkPosition);
        if (chunk == null) {
            return null;
        }

        final Vector3ic localPosition = VectorUtils.floorMod(position, CHUNK_SIZE);
        return chunk.getBlockId(localPosition);
    }

    public void init() {
        generateChunks();
    }

    public void update(double interval) {
        for (final Chunk chunk : chunkMap.values()) {
            chunk.update(interval);
        }

        for (final Entity entity : entities) {
            entity.update(interval);
        }
    }

    private void generateChunks() {
        for (int x = -5; x < 5; x++) {
            for (int z = -5; z < 5; z++) {
                for (int y = 0; y < 4; y++) {
                    final Vector3i position = new Vector3i(x, y, z);
                    final Chunk chunk = new Chunk(this, position);
                    chunk.init(textureMap);
                    chunkMap.put(position, chunk);
                }
            }
        }
    }

    public void render(Camera camera) {
        shader.bind();
        shader.setUniform("projectionMatrix", camera.getProjectionMatrix());
        shader.setUniform("textureSampler", 0);
        textureMap.bind();

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
