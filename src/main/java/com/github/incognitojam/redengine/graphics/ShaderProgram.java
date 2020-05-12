package com.github.incognitojam.redengine.graphics;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {
    private final int programId;
    private final List<Integer> shaderIds;
    private final Map<String, Integer> uniforms;

    public ShaderProgram() throws Exception {
        programId = glCreateProgram();
        if (programId == GL_FALSE) {
            throw new Exception("Could not create shader program");
        }
        shaderIds = new ArrayList<>();
        uniforms = new HashMap<>();
    }

    public void addVertexShader(@NotNull String shaderCode) throws Exception {
        shaderIds.add(createShader(GL_VERTEX_SHADER, shaderCode));
    }

    public void addFragmentShader(@NotNull String shaderCode) throws Exception {
        shaderIds.add(createShader(GL_FRAGMENT_SHADER, shaderCode));
    }

    protected int createShader(int shaderType, @NotNull String shaderCode) throws Exception {
        final int shaderId = glCreateShader(shaderType);
        if (shaderId == GL_FALSE) {
            throw new Exception("Could not create shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            final String infoLog = glGetShaderInfoLog(shaderId, 1024);
            throw new Exception("Error compiling shader code: " + infoLog);
        }

        glAttachShader(programId, shaderId);
        return shaderId;
    }

    public void link() throws Exception {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            final String infoLog = glGetProgramInfoLog(programId, 1024);
            throw new Exception("Error linking shader code: " + infoLog);
        }

        for (final int shaderId : shaderIds) {
            glDetachShader(programId, shaderId);
        }

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            final String infoLog = glGetProgramInfoLog(programId, 1024);
            System.err.println("Warning validating shader code: " + infoLog);
        }
    }

    public void createUniform(@NotNull String uniformName) throws Exception {
        final int uniformLocation = glGetUniformLocation(programId, uniformName);
        if (uniformLocation < 0) {
            throw new Exception("Could not find uniform: " + uniformName);
        }
        uniforms.put(uniformName, uniformLocation);
    }

    public void setUniform(@NotNull String uniformName, int value) {
        glUniform1i(uniforms.get(uniformName), value);
    }

    public void setUniform(@NotNull String uniformName, float value) {
        glUniform1f(uniforms.get(uniformName), value);
    }

    public void setUniform(@NotNull String uniformName, @NotNull Matrix4f value) {
        // Dump the matrix into a float buffer
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            value.get(buffer);
            glUniformMatrix4fv(uniforms.get(uniformName), false, buffer);
        }
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void destroy() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }
}
