package com.github.incognitojam.redengine.graphics;

import org.jetbrains.annotations.NotNull;

public class TextureMap {
    @NotNull
    private final Texture texture;
    private final int size;
    private final float unit;

    public TextureMap(@NotNull String filename, int size) throws Exception {
        this.texture = Texture.loadTexture(filename);
        this.size = size;

        if (texture.getWidth() != texture.getHeight()) {
            throw new Exception("Texture is not square");
        } else if (!texture.isPowerOfTwo()) {
            throw new Exception("Texture is not power of two (16x16, 32x32, 64x64...)");
        }

        unit = 1.0F / (float) size;
    }

    @NotNull
    public Texture getTexture() {
        return texture;
    }

    public int getSize() {
        return size;
    }

    public void bind() {
        texture.bind();
    }

    public void unbind() {
        texture.unbind();
    }

    public void destroy() {
        texture.destroy();
    }

    public float[] getTextureCoordinates(int id) {
        final int x = id % size;
        final int y = id / size;

        /*
         * Coordinates of each corner of the texture, counter-clockwise from
         * the bottom left corner.
         */
        return new float[] {
            x * unit, (y + 1) * unit,
            (x + 1) * unit, (y + 1) * unit,
            (x + 1) * unit, y * unit,
            x * unit, y * unit
        };
    }
}
