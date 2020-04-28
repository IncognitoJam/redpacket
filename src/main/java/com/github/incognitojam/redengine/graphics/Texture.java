package com.github.incognitojam.redengine.graphics;

import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
    private final int id;
    private final int width;
    private final int height;

    private Texture(int id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isPowerOfTwo() {
        return isPowerOfTwo(width) && isPowerOfTwo(height);
    }

    private static boolean isPowerOfTwo(int number) {
        return number > 0 && ((number & (number - 1)) == 0);
    }

    public void bind() {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void destroy() {
        glDeleteTextures(id);
    }

    public static Texture loadTexture(String filename) throws Exception {
        int width, height;
        ByteBuffer buffer;

        // Load texture file
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            buffer = stbi_load(filename, w, h, channels, 4);
            if (buffer == null) {
                throw new Exception("Image file " + filename + " not loaded: " + stbi_failure_reason());
            }

            // Get width and height of image
            width = w.get();
            height = h.get();
        }

        // Create a new OpenGL texture
        final int id = glGenTextures();
        // Bind the texture
        glBindTexture(GL_TEXTURE_2D, id);

        // Tell OpenGL how to unpack the RGBA bytes. Each component is 1 byte in size.
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Upload the texture data
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        // Generate the mipmap
        glGenerateMipmap(GL_TEXTURE_2D);

        stbi_image_free(buffer);

        // Construct the Texture object
        return new Texture(id, width, height);
    }
}
