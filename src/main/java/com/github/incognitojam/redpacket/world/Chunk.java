package com.github.incognitojam.redpacket.world;

import com.github.incognitojam.redengine.graphics.Mesh;
import com.github.incognitojam.redengine.graphics.TextureMap;
import com.github.incognitojam.redengine.maths.VectorUtils;
import com.github.incognitojam.redpacket.block.Block;
import com.github.incognitojam.redpacket.block.BlockFace;
import com.github.incognitojam.redpacket.block.Blocks;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.ArrayList;

public class Chunk {
    public static final int CHUNK_SIZE = 8;

    private final World world;
    private final Vector3ic position;
    private final Vector3ic worldPosition;
    private final Matrix4f worldMatrix;
    private String[] blocks;

    private TextureMap textureMap;
    private boolean outdatedMesh;
    private Mesh mesh;

    public Chunk(World world, Vector3ic position) {
        this.world = world;
        this.position = position;

        worldPosition = new Vector3i(position).mul(CHUNK_SIZE);
        worldMatrix = new Matrix4f().translate(new Vector3f(position).mul(CHUNK_SIZE));
        blocks = new String[CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE];
    }

    public Vector3ic getPosition() {
        return position;
    }

    public Matrix4f getWorldMatrix() {
        return worldMatrix;
    }

    public String getBlockId(int x, int y, int z) {
        return getBlockId(new Vector3i(x, y, z));
    }

    public String getBlockId(Vector3ic position) {
        if (VectorUtils.isOutOfBounds(position, 0, CHUNK_SIZE)) {
            return world.getBlockId(new Vector3i(worldPosition).add(position));
        }
        return blocks[position.x() * CHUNK_SIZE * CHUNK_SIZE + position.z() * CHUNK_SIZE + position.y()];
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

    public void setOutdatedMesh(boolean outdatedMesh) {
        this.outdatedMesh = outdatedMesh;
    }

    private void buildMesh() {
        mesh = new ChunkMeshBuilder()
            .setOptimiseMesh(true)
            .build();
    }

    private class ChunkMeshBuilder {
        private final ArrayList<Vector3ic> positions;
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

                        /*
                         * If optimize mesh is enabled, we should only add the
                         * block faces where there is no neighbouring block
                         * which would obscure the face from view.
                         */
                        if (optimiseMesh) {
                            if ("air".equals(getBlockId(x - 1, y, z))) {
                                addWestFace(x, y, z, block); // -ve x
                            }
                            if ("air".equals(getBlockId(x + 1, y, z))) {
                                addEastFace(x, y, z, block); // +ve x
                            }
                            if ("air".equals(getBlockId(x, y - 1, z))) {
                                addBottomFace(x, y, z, block); // -ve y
                            }
                            if ("air".equals(getBlockId(x, y + 1, z))) {
                                addTopFace(x, y, z, block); // +ve y
                            }
                            if ("air".equals(getBlockId(x, y, z - 1))) {
                                addNorthFace(x, y, z, block); // -ve z
                            }
                            if ("air".equals(getBlockId(x, y, z + 1))) {
                                addSouthFace(x, y, z, block); // +ve z
                            }
                        } else {
                            addWestFace(x, y, z, block); // -ve x
                            addEastFace(x, y, z, block); // +ve x
                            addBottomFace(x, y, z, block); // -ve y
                            addTopFace(x, y, z, block); // +ve y
                            addNorthFace(x, y, z, block); // -ve z
                            addSouthFace(x, y, z, block); // +ve z
                        }
                    }
                }
            }

            // Convert lists of vertex positions to array for float buffer.
            final float[] positionsArray = new float[positions.size() * 3];
            for (int i = 0; i < positions.size(); i++) {
                final Vector3ic position = positions.get(i);
                positionsArray[i * 3 + 0] = position.x();
                positionsArray[i * 3 + 1] = position.y();
                positionsArray[i * 3 + 2] = position.z();
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

        private void addWestFace(int x, int y, int z, Block block) {
            positions.add(new Vector3i(x, y, z));
            positions.add(new Vector3i(x, y, z + 1));
            positions.add(new Vector3i(x, y + 1, z + 1));
            positions.add(new Vector3i(x, y + 1, z));
            addTextureCoords(block.getTextureId(BlockFace.WEST));
            addIndices();
        }

        private void addEastFace(int x, int y, int z, Block block) {
            positions.add(new Vector3i(x + 1, y, z + 1));
            positions.add(new Vector3i(x + 1, y, z));
            positions.add(new Vector3i(x + 1, y + 1, z));
            positions.add(new Vector3i(x + 1, y + 1, z + 1));
            addTextureCoords(block.getTextureId(BlockFace.EAST));
            addIndices();
        }

        private void addBottomFace(int x, int y, int z, Block block) {
            positions.add(new Vector3i(x, y, z + 1));
            positions.add(new Vector3i(x + 1, y, z + 1));
            positions.add(new Vector3i(x + 1, y, z));
            positions.add(new Vector3i(x, y, z));
            addTextureCoords(block.getTextureId(BlockFace.BOTTOM));
            addIndices();
        }

        private void addTopFace(int x, int y, int z, Block block) {
            positions.add(new Vector3i(x + 1, y + 1, z));
            positions.add(new Vector3i(x, y + 1, z));
            positions.add(new Vector3i(x, y + 1, z + 1));
            positions.add(new Vector3i(x + 1, y + 1, z + 1));
            addTextureCoords(block.getTextureId(BlockFace.TOP));
            addIndices();
        }

        private void addNorthFace(int x, int y, int z, Block block) {
            positions.add(new Vector3i(x + 1, y, z));
            positions.add(new Vector3i(x, y, z));
            positions.add(new Vector3i(x, y + 1, z));
            positions.add(new Vector3i(x + 1, y + 1, z));
            addTextureCoords(block.getTextureId(BlockFace.NORTH));
            addIndices();
        }

        private void addSouthFace(int x, int y, int z, Block block) {
            positions.add(new Vector3i(x, y, z + 1));
            positions.add(new Vector3i(x + 1, y, z + 1));
            positions.add(new Vector3i(x + 1, y + 1, z + 1));
            positions.add(new Vector3i(x, y + 1, z + 1));
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
