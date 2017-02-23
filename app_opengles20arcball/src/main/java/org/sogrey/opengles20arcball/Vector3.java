package org.sogrey.opengles20arcball;

/**
 * Created by prometheus on 10/12/2016.
 */

public class Vector3 {

    public float[] v = new float[3];

    public Vector3() {
        v[0] = 0.0f;
        v[1] = 0.0f;
        v[2] = 0.0f;
    }

    public Vector3(float[] v) {
        this.v = v;
    }

    public Vector3(float x, float y, float z) {
        v[0] = x;
        v[1] = y;
        v[2] = z;
    }

    public Vector3(Vector3 vec) {
        v = vec.v;
    }

    public float squaredLength() {
        return v[0] * v[0] + v[1] * v[1] + v[2] * v[2];
    }

    public float length() {
        return (float) Math.sqrt(squaredLength());
    }

    public Vector3 normalize() {
        float len = length();
        if (len == 0.0f) {
            return new Vector3(0.0f, 0.0f, 0.0f);
        }

        return new Vector3(v[0] / len, v[1] / len, v[2] / len);
    }

    public float dot(Vector3 vec) {
        return v[0] * vec.v[0] + v[1] * vec.v[1] + v[2] * vec.v[2];
    }

    public Vector3 cross(Vector3 vec) {
        float x = v[1] * vec.v[2] - v[2] * vec.v[1];
        float y = v[2] * vec.v[0] - v[0] * vec.v[2];
        float z = v[0] * vec.v[1] - v[1] * vec.v[0];

        return new Vector3(x, y, z);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("x: ").append(v[0]).append(" y: ").append(v[1]).append(" z: ").append(v[2]);
        return result.toString();
    }
}