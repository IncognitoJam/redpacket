package com.github.incognitojam.redpacket.world;

import com.github.incognitojam.redengine.graphics.Mesh;
import com.github.incognitojam.redengine.graphics.TextureMap;
import com.github.incognitojam.redpacket.block.Block;
import com.github.incognitojam.redpacket.block.BlockFace;
import com.github.incognitojam.redpacket.block.Blocks;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;

public class Chunk {
    public static final int CHUNK_SIZE = 8;

    private final World world;
    private final Vector3i position;
    private final Vector3i worldPosition;
    private final Matrix4f worldMatrix;
    private String[] blocks;

    private TextureMap textureMap;
    private boolean outdatedMesh;
    private Mesh mesh;

    public Chunk(World world, Vector3i position) {
        this.world = world;
        this.position = position;

        worldPosition = new Vector3i(position).mul(CHUNK_SIZE);
        worldMatrix = new Matrix4f().translate(new Vector3f(position).mul(CHUNK_SIZE));
        blocks = new String[CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE];
    }

    public Vector3i getPosition() {
        return position;
    }

    public Matrix4f getWorldMatrix() {
        return worldMatrix;
    }

    public String getBlockId(int localX, int localY, int localZ) {
        if (localX < 0 || localX >= CHUNK_SIZE ||
            localY < 0 || localY >= CHUNK_SIZE ||
            localZ < 0 || localZ >= CHUNK_SIZE) {

            return world.getBlockId(
                worldPosition.x + localX,
                worldPosition.y + localY,
                worldPosition.z + localZ
            );
        }

        return blocks[localX * CHUNK_SIZE * CHUNK_SIZE + localZ * CHUNK_SIZE + localY];
    }

    public void init(TextureMap textureMap) {
        this.textureMap = textureMap;

        // Use world generator to get default blocks for this chunk.
        blocks = world.getGenerator().getBlocks(position);
        outdatedMesh = true;
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

    private void buildMesh() {
        mesh = new ChunkMeshBuilder()
            .setOptimiseMesh(true)
            .build();
    }

    private class ChunkMeshBuilder {
        private final ArrayList<Vector3i> positions;
        private final ArrayList<Float> textureCoords;
        private final ArrayList<Integer> indices;

        private boolean optimiseMesh;

        private int vertexCount;

        public ChunkMeshBuilder() {
            positions = new ArrayList<>();
            textureCoords = new ArrayList<>();
            indices = new ArrayList<>();
            vertexCount = 0;
        }

        public ChunkMeshBuilder setOptimiseMesh(boolean optimiseMesh) {
            this.optimiseMesh = optimiseMesh;
            return this;
        }

        public Mesh build() {
            // Iterate over blocks in chunk.
            int index = 0;
            for (int x = 0; x < CHUNK_SIZE; x++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {
                    for (int y = 0; y < CHUNK_SIZE; y++, index++) {
                        final String blockId = blocks[index];

                        // Do not add block if it is air.
                        if (blockId.equals("air"))
                            continue;

                        final Block block = Blocks.getBlock(blockId);
                        final Vector3i position = new Vector3i(x, y, z);

                        /*
                         * If optimize mesh is enabled, we should only add the
                         * block faces where there is no neighbouring block
                         * which would obscure the face from view.
                         */
                        if (optimiseMesh) {
                            if ("air".equals(getBlockId(x - 1, y, z))) {
                                addWestFace(position, block); // -ve x
                            }
                            if ("air".equals(getBlockId(x + 1, y, z))) {
                                addEastFace(position, block); // +ve x
                            }
                            if ("air".equals(getBlockId(x, y - 1, z))) {
                                addBottomFace(position, block); // -ve y
                            }
                            if ("air".equals(getBlockId(x, y + 1, z))) {
                                addTopFace(position, block); // +ve y
                            }
                            if ("air".equals(getBlockId(x, y, z - 1))) {
                                addNorthFace(position, block); // -ve z
                            }
                            if ("air".equals(getBlockId(x, y, z + 1))) {
                                addSouthFace(position, block); // +ve z
                            }
                        } else {
                            addWestFace(position, block); // -ve x
                            addEastFace(position, block); // +ve x
                            addBottomFace(position, block); // -ve y
                            addTopFace(position, block); // +ve y
                            addNorthFace(position, block); // -ve z
                            addSouthFace(position, block); // +ve z
                        }
                    }
                }
            }

            // Convert lists of vertex positions to array for float buffer.
            final float[] positionsArray = new float[positions.size() * 3];
            for (int i = 0; i < positions.size(); i++) {
                final Vector3i position = positions.get(i);
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

        private void addWestFace(Vector3i position, Block block) {
            positions.add(new Vector3i(position).add(0, 0, 0));
            positions.add(new Vector3i(position).add(0, 0, 1));
            positions.add(new Vector3i(position).add(0, 1, 1));
            positions.add(new Vector3i(position).add(0, 1, 0));
            addTextureCoords(block.getTextureId(BlockFace.WEST));
            addIndices();
        }

        private void addEastFace(Vector3i position, Block block) {
            positions.add(new Vector3i(position).add(1, 0, 1));
            positions.add(new Vector3i(position).add(1, 0, 0));
            positions.add(new Vector3i(position).add(1, 1, 0));
            positions.add(new Vector3i(position).add(1, 1, 1));
            addTextureCoords(block.getTextureId(BlockFace.EAST));
            addIndices();
        }

        private void addBottomFace(Vector3i position, Block block) {
            positions.add(new Vector3i(position).add(0, 0, 1));
            positions.add(new Vector3i(position).add(1, 0, 1));
            positions.add(new Vector3i(position).add(1, 0, 0));
            positions.add(new Vector3i(position).add(0, 0, 0));
            addTextureCoords(block.getTextureId(BlockFace.BOTTOM));
            addIndices();
        }

        private void addTopFace(Vector3i position, Block block) {
            positions.add(new Vector3i(position).add(1, 1, 0));
            positions.add(new Vector3i(position).add(0, 1, 0));
            positions.add(new Vector3i(position).add(0, 1, 1));
            positions.add(new Vector3i(position).add(1, 1, 1));
            addTextureCoords(block.getTextureId(BlockFace.TOP));
            addIndices();
        }

        private void addNorthFace(Vector3i position, Block block) {
            positions.add(new Vector3i(position).add(1, 0, 0));
            positions.add(new Vector3i(position).add(0, 0, 0));
            positions.add(new Vector3i(position).add(0, 1, 0));
            positions.add(new Vector3i(position).add(1, 1, 0));
            addTextureCoords(block.getTextureId(BlockFace.NORTH));
            addIndices();
        }

        private void addSouthFace(Vector3i position, Block block) {
            positions.add(new Vector3i(position).add(0, 0, 1));
            positions.add(new Vector3i(position).add(1, 0, 1));
            positions.add(new Vector3i(position).add(1, 1, 1));
            positions.add(new Vector3i(position).add(0, 1, 1));
            addTextureCoords(block.getTextureId(BlockFace.SOUTH));
            addIndices();
        }

        private void addTextureCoords(int textureId) {
            final float[] textureCoordinates = textureMap.getTextureCoordinates(textureId);
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
    }
}
