package com.github.incognitojam.redpacket.block;

public class BlockPlanks extends Block {
    protected BlockPlanks() {
        super("planks");
    }

    @Override
    public int getTextureId(BlockFace blockFace) {
        return 4;
    }
}
