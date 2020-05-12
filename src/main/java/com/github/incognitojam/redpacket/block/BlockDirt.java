package com.github.incognitojam.redpacket.block;

import org.jetbrains.annotations.NotNull;

public class BlockDirt extends Block {
    protected BlockDirt() {
        super("dirt");
    }

    @Override
    public int getTextureId(@NotNull BlockFace blockFace) {
        return 3;
    }
}
