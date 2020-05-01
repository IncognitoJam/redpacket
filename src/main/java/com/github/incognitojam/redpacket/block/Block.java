package com.github.incognitojam.redpacket.block;

public abstract class Block {
    private final String id;

    protected Block(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public abstract int getTextureId(BlockFace blockFace);
}
