package com.github.incognitojam.redpacket.block;

import org.jetbrains.annotations.NotNull;

public class BlockLeaves extends Block {
    protected BlockLeaves() {
        super("leaves");
    }

    @Override
    public int getTextureId(@NotNull BlockFace blockFace) {
        return 5;
    }
}
