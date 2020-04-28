package com.github.incognitojam.redpacket.world;

import com.github.incognitojam.redengine.graphics.Mesh;
import com.github.incognitojam.redengine.graphics.TextureMap;
import com.github.incognitojam.redpacket.block.Block;
import com.github.incognitojam.redpacket.block.Blocks;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;

public class Chunk {
    public static final int CHUNK_SIZE = 8;

    private final Vector3i position;
    private final Matrix4f worldMatrix;
    private final String[] blocks;
    private TextureMap textureMap;

    private boolean outdatedMesh;
    private Mesh mesh;

    public Chunk(Vector3i position) {
        this.position = position;
        worldMatrix = new Matrix4f().translate(new Vector3f(position).mul(CHUNK_SIZE));
        blocks = new String[CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE];
    }

    public Vector3i getPosition() {
        return position;
    }

    public Matrix4f getWorldMatrix() {
        return worldMatrix;
    }

    public void init(TextureMap textureMap) {
        this.textureMap = textureMap;
        generateBlocks();
        buildMesh();
    }

    public void update(double interval) {
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
        if (mesh != null) {
            mesh.destroy();
        }
    }

    private void generateBlocks() {
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                for (int y = 0; y < CHUNK_SIZE; y++) {
                    String id;
                    if (y == CHUNK_SIZE - 1) {
                        id = "grass";
                    } else {
                        id = "dirt";
                    }
                    blocks[x + y * CHUNK_SIZE + z * CHUNK_SIZE * CHUNK_SIZE] = id;
                }
            }
        }
    }

    private void buildMesh() {
        final ChunkMeshBuilder builder = new ChunkMeshBuilder(textureMap);

        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                for (int y = 0; y < CHUNK_SIZE; y++) {
                    final Block block = Blocks.getBlock(blocks[x + y * CHUNK_SIZE + z * CHUNK_SIZE * CHUNK_SIZE]);
                    builder.addBlock(x, y, z, block);
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

        public void addBlock(int x, int y, int z, Block block) {
            final Vector3f position = new Vector3f(x, y, z);
            addLeftFace(position, block); // -ve x
            addRightFace(position, block); // +ve x
            addBottomFace(position, block); // -ve y
            addTopFace(position, block); // +ve y
            addBackFace(position, block); // -ve z
            addFrontFace(position, block); // +ve z
        }

        private void addLeftFace(Vector3f position, Block block) {
            positions.add(new Vector3f(position).add(0, 0, 0));
            positions.add(new Vector3f(position).add(0, 0, 1));
            positions.add(new Vector3f(position).add(0, 1, 1));
            positions.add(new Vector3f(position).add(0, 1, 0));
            addTextureCoords(block);
            addIndices();
        }

        private void addRightFace(Vector3f position, Block block) {
            positions.add(new Vector3f(position).add(1, 0, 0));
            positions.add(new Vector3f(position).add(1, 0, 1));
            positions.add(new Vector3f(position).add(1, 1, 1));
            positions.add(new Vector3f(position).add(1, 1, 0));
            addTextureCoords(block);
            addIndices();
        }

        private void addBottomFace(Vector3f position, Block block) {
            positions.add(new Vector3f(position).add(0, 0, 1));
            positions.add(new Vector3f(position).add(1, 0, 1));
            positions.add(new Vector3f(position).add(1, 0, 0));
            positions.add(new Vector3f(position).add(0, 0, 0));
            addTextureCoords(block);
            addIndices();
        }

        private void addTopFace(Vector3f position, Block block) {
            positions.add(new Vector3f(position).add(1, 1, 0));
            positions.add(new Vector3f(position).add(0, 1, 0));
            positions.add(new Vector3f(position).add(0, 1, 1));
            positions.add(new Vector3f(position).add(1, 1, 1));
            addTextureCoords(block);
            addIndices();
        }

        private void addBackFace(Vector3f position, Block block) {
            positions.add(new Vector3f(position).add(1, 0, 0));
            positions.add(new Vector3f(position).add(0, 0, 0));
            positions.add(new Vector3f(position).add(0, 1, 0));
            positions.add(new Vector3f(position).add(1, 1, 0));
            addTextureCoords(block);
            addIndices();
        }

        private void addFrontFace(Vector3f position, Block block) {
            positions.add(new Vector3f(position).add(0, 0, 1));
            positions.add(new Vector3f(position).add(1, 0, 1));
            positions.add(new Vector3f(position).add(1, 1, 1));
            positions.add(new Vector3f(position).add(0, 1, 1));
            addTextureCoords(block);
            addIndices();
        }

        private void addTextureCoords(Block block) {
            final float[] textureCoordinates = textureMap.getTextureCoordinates(block.getTextureId());
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