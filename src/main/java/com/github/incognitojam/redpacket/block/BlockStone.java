package com.github.incognitojam.redpacket.block;

public class BlockStone extends Block {
    protected BlockStone() {
        super("stone");
    }

    @Override
    public int getTextureId(BlockFace blockFace) {
        return 1;
    }
}
