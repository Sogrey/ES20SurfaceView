package org.sogrey.opengles20arcball;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.Arrays;

/**
 * Created by prometheus on 05/12/16.
 */

public class ES20SurfaceView extends GLSurfaceView {

    private static final String TAG = "ES20Renderer";
    private ES20Renderer es20Renderer;

    public ES20SurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextClientVersion(2);

        es20Renderer = new ES20Renderer(context);
        setRenderer(es20Renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent evt) {
        int x = (int) evt.getX();
        int y = (int) evt.getY();

        switch (evt.getAction()) {
            case MotionEvent.ACTION_DOWN:
                es20Renderer.getArcballCamera().compute_mouse_start_vector(getWidth(), getHeight(), x, y);
                es20Renderer.getArcballCamera().saveLastRoation();
                break;

            case MotionEvent.ACTION_UP:
                break;

            case MotionEvent.ACTION_MOVE:
                es20Renderer.getArcballCamera().computeAngleAndAxis(getWidth(), getHeight(), x, y);

                requestRender();
                break;
        }

        return true;
    }
}
