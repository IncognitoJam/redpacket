package com.github.incognitojam.redpacket.block;

public class BlockGrass extends Block {
    protected BlockGrass() {
        super("grass");
    }

    @Override
    public int getTextureId(BlockFace blockFace) {
        switch (blockFace) {
            case TOP:
                return 2;
            case BOTTOM:
                return 1;
            default:
                return 0;
        }
    }
}
