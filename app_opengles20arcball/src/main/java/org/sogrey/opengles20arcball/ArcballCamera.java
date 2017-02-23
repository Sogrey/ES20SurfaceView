package org.sogrey.opengles20arcball;

import android.opengl.Matrix;

import java.util.Arrays;

/**
 * Created by prometheus on 10/12/2016.
 */

public class ArcballCamera {

    private Vector3 mouseStart;
    private Vector3 mouseDrag;
    private Vector3 axis;
    private float rotationAngle;
    public float[] currentRotation = new float[16];
    public float[] lastRotation = new float[16];

    public ArcballCamera() {
        mouseStart = new Vector3();
        mouseDrag = new Vector3();
        axis = new Vector3(0.0f, 1.0f, 0.0f);
        rotationAngle = 0.0f;
        Matrix.setIdentityM(currentRotation, 0);
        Matrix.setIdentityM(lastRotation, 0);
    }

    public void computeAngleAndAxis(int width, int height, int x, int y) {
        mouseDrag = compute_vector_in_screen(width, height, x, y);
        axis = mouseStart.cross(mouseDrag);
        rotationAngle = (float) Math.acos(mouseStart.dot(mouseDrag));
    }

    public void compute_mouse_start_vector(int width, int height, int x, int y) {
        mouseStart = compute_vector_in_screen(width, height, x, y);
    }

    public void saveLastRoation() {
        lastRotation = Arrays.copyOf(currentRotation, currentRotation.length);
    }


    private Vector3 compute_vector_in_screen(int width, int height, int x, int y) {
        float screen_to_camera_x = 1.0f * x / width * 2 - 1.0f;
        float screen_to_camera_y = 1.0f * y / height * 2 - 1.0f;

        Vector3 result = new Vector3(screen_to_camera_x, screen_to_camera_y, 0.0f);
        result.v[1] = -result.v[1];

        float squaredLength = result.squaredLength();
        if (squaredLength <= 1.0f) {
            result.v[2] = (float) Math.sqrt(1 - squaredLength);
        }
        else {
            result = result.normalize();
        }

        return result;
    }

    public Vector3 getMouseStart() {
        return mouseStart;
    }

    public Vector3 getMouseDrag() {
        return mouseDrag;
    }

    public Vector3 getAxis() {
        return axis;
    }

    public float getRotationAngle() {
        return rotationAngle;
    }

}
