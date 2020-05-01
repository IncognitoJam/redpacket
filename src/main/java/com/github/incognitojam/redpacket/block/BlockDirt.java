package com.github.incognitojam.redpacket.block;

public class BlockDirt extends Block {
    protected BlockDirt() {
        super("dirt");
    }

    @Override
    public int getTextureId(BlockFace blockFace) {
        return 1;
    }
}
