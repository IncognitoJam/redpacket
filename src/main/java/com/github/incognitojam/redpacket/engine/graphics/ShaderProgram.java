package com.github.incognitojam.redpacket.engine.graphics;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {
    private final int programId;
    private final List<Integer> shaderIds;

    public ShaderProgram() throws Exception {
        programId = glCreateProgram();
        if (programId == GL_FALSE) {
            throw new Exception("Could not create shader program");
        }
        shaderIds = new ArrayList<>();
    }

    public void addVertexShader(String shaderCode) throws Exception {
        shaderIds.add(createShader(GL_VERTEX_SHADER, shaderCode));
    }

    public void addFragmentShader(String shaderCode) throws Exception {
        shaderIds.add(createShader(GL_FRAGMENT_SHADER, shaderCode));
    }

    protected int createShader(int shaderType, String shaderCode) throws Exception {
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
