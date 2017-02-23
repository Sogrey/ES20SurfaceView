package org.sogrey.opengles20arcball;

/**
 * Created by prometheus on 10/12/2016.
 */

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

    private float[] modelMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    private final FloatBuffer vertexDataBuffer;
    private int mvpHandle;
    private int vertexPositionHandle;
    private int vertexColorHandle;

    private Context context;

    private ShaderProgram basicShader;
    private ArcballCamera arcballCamera;

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
        arcballCamera = new ArcballCamera();
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {

        GLES20.glClearColor(0.0f, 0.3f, 0.5f, 1.0f);
        basicShader = new ShaderProgram("Basic Shader", loadShaderSourceFromResources(R.raw.basic_vert), loadShaderSourceFromResources(R.raw.basic_frag));

        Matrix.setLookAtM(viewMatrix, 0,
                0.0f, 0.0f, 3.0f,
                0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f);

        mvpHandle = GLES20.glGetUniformLocation(basicShader.getHandle(), "uMVP");
        vertexPositionHandle = GLES20.glGetAttribLocation(basicShader.getHandle(), "aPosition");
        vertexColorHandle = GLES20.glGetAttribLocation(basicShader.getHandle(), "aColor");

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
        Matrix.setIdentityM(modelMatrix, 0);
        GLES20.glUseProgram(basicShader.getHandle());

        //POSITION
        vertexDataBuffer.position(0);
        GLES20.glVertexAttribPointer(vertexPositionHandle, 3, GLES20.GL_FLOAT, false, 7 * 4, vertexDataBuffer);
        GLES20.glEnableVertexAttribArray(vertexPositionHandle);

        //COLOR
        vertexDataBuffer.position(3);
        GLES20.glVertexAttribPointer(vertexColorHandle, 4, GLES20.GL_FLOAT, false, 7 * 4, vertexDataBuffer);
        GLES20.glEnableVertexAttribArray(vertexColorHandle);


        arcballCamera.getAxis().normalize();

        //Set a new rotation matrix using the computed angle and axis vector
        Matrix.setRotateM(arcballCamera.currentRotation, 0, radToDeg(arcballCamera.getRotationAngle()), arcballCamera.getAxis().v[0], arcballCamera.getAxis().v[1], arcballCamera.getAxis().v[2]);
        //Multiply the new rotation matrix with the old one so the rotation doesn't start from scratch
        Matrix.multiplyMM(arcballCamera.currentRotation, 0, arcballCamera.currentRotation, 0, arcballCamera.lastRotation, 0);

        //Apply the rotation to the models using the modelMatrix
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, arcballCamera.currentRotation, 0);
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

    public ArcballCamera getArcballCamera() {
        return arcballCamera;
    }

    public float[] getModelMatrix() {
        return modelMatrix;
    }

    private float radToDeg(float radians) {
        return (float) (radians * (180.0f / Math.PI));
    }

    private float degToRad(float degrees) {
        return (float) (degrees * Math.PI / 180.0f);
    }

}