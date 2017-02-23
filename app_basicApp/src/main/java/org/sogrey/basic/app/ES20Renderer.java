package org.sogrey.basic.app;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by prometheus on 05/12/16.
 */

public class ES20Renderer implements GLSurfaceView.Renderer  {

    private static final String TAG = "ES20Renderer";
    private static final String SHADER_ERROR = "ShaderError";

    private float[] modelMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    private final FloatBuffer vertexDataBuffer;
    private int mvpHandle;
    private int vertexPositionHandle;
    private int vertexColorHandle;
    private int shaderProgram;

    private Context context;

    public ES20Renderer(Context context) {
        this.context = context;

        final float[] vertexData = {
                // X, Y, Z
                0.0f, 0.5f, 0.0f,
                // R, G, B, A
                1.0f, 0.0f, 0.0f, 1.0f,

                // X, Y, Z
                -0.5f, -0.5f, 0.0f,
                // R, G, B, A
                0.0f, 0.0f, 1.0f, 1.0f,

                // X, Y, Z
                0.5f, -0.5f, 0.0f,
                // R, G, B, A
                0.0f, 1.0f, 0.0f, 1.0f
        };

        //Number of position values * 4 bytes per float
        ByteBuffer bb = ByteBuffer.allocateDirect(vertexData.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexDataBuffer = bb.asFloatBuffer();
        vertexDataBuffer.put(vertexData);
        vertexDataBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {

        GLES20.glClearColor(0.0f, 0.3f, 0.5f, 1.0f);

        Matrix.setLookAtM(viewMatrix, 0,
                0.0f, 0.0f, 3.0f,
                0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f);

        //VERTEX SHADER
        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        if (vertexShader != 0) {
            GLES20.glShaderSource(vertexShader, loadShaderSourceFromResources(R.raw.basic_vert));
            GLES20.glCompileShader(vertexShader);

            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(vertexShader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
            if (compileStatus[0] == 0) {
                Log.e(SHADER_ERROR, "Shader Compilation Error!\nShader Type: Vertex Shader\n" + GLES20.glGetShaderInfoLog(vertexShader));
                GLES20.glDeleteShader(vertexShader);
                vertexShader = 0;
            }
        }

        //FRAGMENT SHADER
        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        if (fragmentShader != 0) {
            GLES20.glShaderSource(fragmentShader, loadShaderSourceFromResources(R.raw.basic_frag));
            GLES20.glCompileShader(fragmentShader);

            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(fragmentShader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
            if (compileStatus[0] == 0) {
                Log.e(SHADER_ERROR, "Shader Compilation Error!\nShader Type: Fragment Shader\n" + GLES20.glGetShaderInfoLog(fragmentShader));
                GLES20.glDeleteShader(fragmentShader);
                fragmentShader = 0;
            }
        }

        if (vertexShader != 0 && fragmentShader != 0) {
            shaderProgram = GLES20.glCreateProgram();
            if (shaderProgram != 0) {
                GLES20.glAttachShader(shaderProgram, vertexShader);
                GLES20.glAttachShader(shaderProgram, fragmentShader);
                GLES20.glLinkProgram(shaderProgram);

                final int[] linkStatus = new int[1];
                GLES20.glGetProgramiv(shaderProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);
                if (linkStatus[0] == 0) {
                    Log.e(SHADER_ERROR, "Shader Program Link Error!\n" + GLES20.glGetProgramInfoLog(shaderProgram));
                    GLES20.glDeleteProgram(shaderProgram);
                    shaderProgram = 0;
                }
            }
        }

        GLES20.glBindAttribLocation(shaderProgram, 0, "a_Position");
        GLES20.glBindAttribLocation(shaderProgram, 1, "a_Color");

        mvpHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVP");
        vertexPositionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
        vertexColorHandle = GLES20.glGetAttribLocation(shaderProgram, "aColor");
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        final float ratio = (float) width / height;
        Matrix.perspectiveM(projectionMatrix, 0, 45.0f, ratio, 0.1f, 1000.0f);
    }

    @Override
    public void onDrawFrame(GL10 glUnused)  {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUseProgram(shaderProgram);
        Matrix.setIdentityM(modelMatrix, 0);

        //POSITION
        vertexDataBuffer.position(0);
        GLES20.glVertexAttribPointer(vertexPositionHandle, 3, GLES20.GL_FLOAT, false, 7 * 4, vertexDataBuffer);
        GLES20.glEnableVertexAttribArray(vertexPositionHandle);

        //COLOR
        vertexDataBuffer.position(3);
        GLES20.glVertexAttribPointer(vertexColorHandle, 4, GLES20.GL_FLOAT, false, 7 * 4, vertexDataBuffer);
        GLES20.glEnableVertexAttribArray(vertexColorHandle);

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);

        GLES20.glUniformMatrix4fv(mvpHandle, 1, false, mvpMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }

    private String loadShaderSourceFromResources(int resource) {
        InputStream is = context.getResources().openRawResource(resource);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder result = new StringBuilder();

        try {

            while ((line = br.readLine()) != null) {
                result.append(line);
                result.append("\n");
            }
        }
        catch (IOException ex) {
            Log.e(TAG, "Failed loading shader source: " + ex.toString());
            return null;
        }

        return result.toString();
    }
}
