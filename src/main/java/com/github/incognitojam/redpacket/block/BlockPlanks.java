package com.github.incognitojam.redpacket.block;

import org.jetbrains.annotations.NotNull;

public class BlockPlanks extends Block {
    protected BlockPlanks() {
        super("planks");
    }

    @Override
    public int getTextureId(@NotNull BlockFace blockFace) {
        return 4;
    }
}
