package org.sogrey.opengles20arcball;

import android.opengl.GLES20;
import android.util.Log;

/**
 * Created by prometheus on 10/12/2016.
 */

public class ShaderProgram {

    private static final String SHADER_ERROR_TAG = "ShaderError";

    private String name;
    private int shaderProgramHandle;

    public ShaderProgram(String name, String vertexSource, String fragmentSource) {
        this.name = name;
        initShader(vertexSource, fragmentSource);
    }

    private void initShader(String vertexSource, String fragmentSource) {
        int vertexShader = addShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        int fragmentShader = addShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);

        shaderProgramHandle = GLES20.glCreateProgram();
        GLES20.glAttachShader(shaderProgramHandle, vertexShader);
        GLES20.glAttachShader(shaderProgramHandle, fragmentShader);
        GLES20.glLinkProgram(shaderProgramHandle);

        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(shaderProgramHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            Log.e(SHADER_ERROR_TAG, "Shader program link failed!\nShader Program: " + name + "\n" + GLES20.glGetProgramInfoLog(shaderProgramHandle));
            GLES20.glDeleteProgram(shaderProgramHandle);
            shaderProgramHandle = 0;
        }
    }

    private int addShader(int type, String source) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);

        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            String shaderType = "";
            switch (type) {
                case GLES20.GL_VERTEX_SHADER:
                    shaderType = "Vertex Shader";
                    break;

                case GLES20.GL_FRAGMENT_SHADER:
                    shaderType = "Fragment Shader";
                    break;
            }

            Log.e(SHADER_ERROR_TAG, "Shader Compilation Error!\nShaderProgram: " + name + "\nShader Type: " + shaderType + "\n" + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
        }

        return shader;
    }

    public int getHandle() {
        return shaderProgramHandle;
    }
}
