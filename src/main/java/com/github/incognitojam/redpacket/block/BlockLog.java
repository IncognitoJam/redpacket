package com.github.incognitojam.redpacket.block;

import org.jetbrains.annotations.NotNull;

public class BlockLog extends Block {
    protected BlockLog() {
        super("log");
    }

    @Override
    public int getTextureId(@NotNull BlockFace blockFace) {
        switch (blockFace) {
            case TOP:
            case BOTTOM:
                return 8;
            default:
                return 6;
        }
    }
}
