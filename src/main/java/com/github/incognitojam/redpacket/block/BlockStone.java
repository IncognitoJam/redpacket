package com.github.incognitojam.redpacket.block;

import org.jetbrains.annotations.NotNull;

public class BlockStone extends Block {
    protected BlockStone() {
        super("stone");
    }

    @Override
    public int getTextureId(@NotNull BlockFace blockFace) {
        return 1;
    }
}
