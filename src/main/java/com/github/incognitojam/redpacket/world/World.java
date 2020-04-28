package com.github.incognitojam.redpacket.world;

import com.github.incognitojam.redengine.graphics.Camera;
import com.github.incognitojam.redengine.graphics.ShaderProgram;
import com.github.incognitojam.redengine.graphics.TextureMap;
import org.joml.Matrix4f;
import org.joml.Vector3i;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class World {
    private final TextureMap textureMap;
    private final HashMap<Vector3i, Chunk> chunkMap;
    private final Matrix4f modelViewMatrix;
    private final ShaderProgram shader;

    public World() throws Exception {
        textureMap = new TextureMap("textures/grass.png", 2);
        chunkMap = new HashMap<>();
        modelViewMatrix = new Matrix4f();

        shader = new ShaderProgram();
        shader.addVertexShader(Files.readString(Paths.get("shaders/basic.vert")));
        shader.addFragmentShader(Files.readString(Paths.get("shaders/basic.frag")));
        shader.link();

        shader.createUniform("projectionMatrix");
        shader.createUniform("modelViewMatrix");
        shader.createUniform("textureSampler");
    }

    public void init() {
        generateChunks();
    }

    public void update(double interval) {
        for (final Chunk chunk : chunkMap.values()) {
            chunk.update(interval);
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

        textureMap.unbind();
        shader.unbind();
    }

    public void destroy() {
        for (final Chunk chunk : chunkMap.values()) {
            chunk.destroy();
        }

        shader.destroy();
        textureMap.destroy();
    }

    private void generateChunks() {
        for (int x = -4; x < 4; x++) {
            for (int z = -4; z < 4; z++) {
                final Vector3i position = new Vector3i(x, 0, z);
                final Chunk chunk = new Chunk(position);
                chunk.init(textureMap);
                chunkMap.put(position, chunk);
            }
        }
    }
}
