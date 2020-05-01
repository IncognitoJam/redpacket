package com.github.incognitojam.redpacket.world;

import com.github.incognitojam.redengine.graphics.Camera;
import com.github.incognitojam.redengine.graphics.ShaderProgram;
import com.github.incognitojam.redengine.graphics.TextureMap;
import com.github.incognitojam.redpacket.entity.Entity;
import com.github.incognitojam.redpacket.entity.Player;
import org.joml.Matrix4f;
import org.joml.Vector3i;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class World {
    private final WorldGenerator generator;

    private final TextureMap textureMap;
    private final HashMap<Vector3i, Chunk> chunkMap;
    private final List<Entity> entities;
    private final Matrix4f modelViewMatrix;
    private final ShaderProgram shader;

    public World(WorldGenerator generator) throws Exception {
        this.generator = generator;

        textureMap = new TextureMap("textures/grass.png", 2);
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

    private void generateChunks() {
        for (int x = -4; x < 4; x++) {
            for (int z = -4; z < 4; z++) {
                final Vector3i position = new Vector3i(x, 0, z);
                final Chunk chunk = new Chunk(this, position);
                chunk.init(textureMap);
                chunkMap.put(position, chunk);
            }
        }
    }
}
