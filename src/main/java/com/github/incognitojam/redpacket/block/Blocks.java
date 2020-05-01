package com.github.incognitojam.redpacket.block;

import java.util.HashMap;
import java.util.Map;

public class Blocks {
    public static final BlockGrass GRASS;
    public static final BlockDirt DIRT;
    public static final BlockStone STONE;

    private static final Map<String, Block> blockMap;

    static {
        blockMap = new HashMap<>();
        GRASS = registerBlock(new BlockGrass());
        DIRT = registerBlock(new BlockDirt());
        STONE = registerBlock(new BlockStone());
    }

    private static <T extends Block> T registerBlock(T block) {
        blockMap.put(block.getId(), block);
        return block;
    }

    public static Block getBlock(String id) {
        return blockMap.get(id);
    }
}
