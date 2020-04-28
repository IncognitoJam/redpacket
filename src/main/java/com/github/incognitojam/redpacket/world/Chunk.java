package com.github.incognitojam.redpacket.world;

import com.github.incognitojam.redengine.graphics.Mesh;
import com.github.incognitojam.redengine.graphics.TextureMap;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;

public class Chunk {
    public static final int CHUNK_SIZE = 8;

    private final Vector3i position;
    private final Matrix4f worldMatrix;
    private TextureMap textureMap;

    private boolean outdatedMesh;
    private Mesh mesh;

    public Chunk(Vector3i position) {
        this.position = position;
        worldMatrix = new Matrix4f().translate(new Vector3f(position).mul(CHUNK_SIZE));
    }

    public Vector3i getPosition() {
        return position;
    }

    public Matrix4f getWorldMatrix() {
        return worldMatrix;
    }

    public void init(TextureMap textureMap) {
        this.textureMap = textureMap;
        buildMesh();
    }

    public void update() {
        if (outdatedMesh) {
            buildMesh();
            outdatedMesh = false;
        }
    }

    public void render() {
        mesh.bind();
        mesh.render();
        mesh.unbind();
    }

    public void destroy() {
        mesh.destroy();
    }

    private void buildMesh() {
        final ChunkMeshBuilder builder = new ChunkMeshBuilder(textureMap);

        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                for (int y = 0; y < CHUNK_SIZE; y++) {
                    builder.addBlock(x, y, z);
                }
            }
        }

        mesh = builder.build();
    }

    private static class ChunkMeshBuilder {
        private final TextureMap textureMap;

        private final ArrayList<Vector3f> positions;
        private final ArrayList<Float> textureCoords;
        private final ArrayList<Integer> indices;
        private int vertexCount;

        public ChunkMeshBuilder(TextureMap textureMap) {
            this.textureMap = textureMap;

            positions = new ArrayList<>();
            textureCoords = new ArrayList<>();
            indices = new ArrayList<>();
            vertexCount = 0;
        }

        public void addBlock(int x, int y, int z) {
            final Vector3f position = new Vector3f(x, y, z);
            addLeftFace(position); // -ve x
            addRightFace(position); // +ve x
            addBottomFace(position); // -ve y
            addTopFace(position); // +ve y
            addBackFace(position); // -ve z
            addFrontFace(position); // +ve z
        }

        private void addLeftFace(Vector3f position) {
            positions.add(new Vector3f(position).add(0, 0, 0));
            positions.add(new Vector3f(position).add(0, 0, 1));
            positions.add(new Vector3f(position).add(0, 1, 1));
            positions.add(new Vector3f(position).add(0, 1, 0));
            addTextureCoords();
            addIndices();
        }

        private void addRightFace(Vector3f position) {
            positions.add(new Vector3f(position).add(1, 0, 0));
            positions.add(new Vector3f(position).add(1, 0, 1));
            positions.add(new Vector3f(position).add(1, 1, 1));
            positions.add(new Vector3f(position).add(1, 1, 0));
            addTextureCoords();
            addIndices();
        }

        private void addBottomFace(Vector3f position) {
            positions.add(new Vector3f(position).add(0, 0, 1));
            positions.add(new Vector3f(position).add(1, 0, 1));
            positions.add(new Vector3f(position).add(1, 0, 0));
            positions.add(new Vector3f(position).add(0, 0, 0));
            addTextureCoords();
            addIndices();
        }

        private void addTopFace(Vector3f position) {
            positions.add(new Vector3f(position).add(1, 1, 0));
            positions.add(new Vector3f(position).add(0, 1, 0));
            positions.add(new Vector3f(position).add(0, 1, 1));
            positions.add(new Vector3f(position).add(1, 1, 1));
            addTextureCoords();
            addIndices();
        }

        private void addBackFace(Vector3f position) {
            positions.add(new Vector3f(position).add(1, 0, 0));
            positions.add(new Vector3f(position).add(0, 0, 0));
            positions.add(new Vector3f(position).add(0, 1, 0));
            positions.add(new Vector3f(position).add(1, 1, 0));
            addTextureCoords();
            addIndices();
        }

        private void addFrontFace(Vector3f position) {
            positions.add(new Vector3f(position).add(0, 0, 1));
            positions.add(new Vector3f(position).add(1, 0, 1));
            positions.add(new Vector3f(position).add(1, 1, 1));
            positions.add(new Vector3f(position).add(0, 1, 1));
            addTextureCoords();
            addIndices();
        }

        private void addTextureCoords() {
            final float[] textureCoordinates = textureMap.getTextureCoordinates(0);
            for (final float ordinate : textureCoordinates) {
                textureCoords.add(ordinate);
            }
        }

        private void addIndices() {
            indices.add(vertexCount + 0);
            indices.add(vertexCount + 1);
            indices.add(vertexCount + 2);
            indices.add(vertexCount + 2);
            indices.add(vertexCount + 3);
            indices.add(vertexCount + 0);
            vertexCount += 4;
        }

        public Mesh build() {
            final float[] positionsArray = new float[positions.size() * 3];
            for (int i = 0; i < positions.size(); i++) {
                final Vector3f position = positions.get(i);
                positionsArray[i * 3 + 0] = position.x;
                positionsArray[i * 3 + 1] = position.y;
                positionsArray[i * 3 + 2] = position.z;
            }

            final float[] textureCoordsArray = new float[textureCoords.size()];
            for (int i = 0; i < textureCoords.size(); i++) {
                textureCoordsArray[i] = textureCoords.get(i);
            }

            final int[] indicesArray = new int[indices.size()];
            for (int i = 0; i < indices.size(); i++) {
                indicesArray[i] = indices.get(i);
            }

            return new Mesh(
                positionsArray,
                textureCoordsArray,
                indicesArray
            );
        }
    }
}
