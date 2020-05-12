package com.github.incognitojam.redpacket.block;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @NotNull
    private static <T extends Block> T registerBlock(@NotNull T block) {
        blockMap.put(block.getId(), block);
        return block;
    }

    @Nullable
    public static Block getBlock(@NotNull String id) {
        return blockMap.get(id);
    }
}
