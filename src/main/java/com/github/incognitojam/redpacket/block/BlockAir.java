package com.github.incognitojam.redpacket.block;

import org.jetbrains.annotations.NotNull;

public class BlockAir extends Block {
    protected BlockAir() {
        super("air");
    }

    @Override
    public int getTextureId(@NotNull BlockFace blockFace) {
        return 0;
    }
}
