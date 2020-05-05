package com.github.incognitojam.redpacket.block;

public class BlockLeaves extends Block {
    protected BlockLeaves() {
        super("leaves");
    }

    @Override
    public int getTextureId(BlockFace blockFace) {
        return 5;
    }
}
