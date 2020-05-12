package com.github.incognitojam.redpacket.block;

import org.jetbrains.annotations.NotNull;

public class BlockGrass extends Block {
    protected BlockGrass() {
        super("grass");
    }

    @Override
    public int getTextureId(@NotNull BlockFace blockFace) {
        switch (blockFace) {
            case TOP:
                return 2;
            case BOTTOM:
                return 3;
            default:
                return 7;
        }
    }
}
