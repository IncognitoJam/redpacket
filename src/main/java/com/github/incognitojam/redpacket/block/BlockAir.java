package com.github.incognitojam.redpacket.block;

public class BlockAir extends Block {
    protected BlockAir() {
        super("air");
    }

    @Override
    public int getTextureId(BlockFace blockFace) {
        return 3;
    }
}
