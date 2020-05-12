package com.github.incognitojam.redpacket.block;

import org.jetbrains.annotations.NotNull;

public abstract class Block {
    private final String id;

    protected Block(@NotNull String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public abstract int getTextureId(@NotNull BlockFace blockFace);
}
