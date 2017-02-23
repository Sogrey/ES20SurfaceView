package org.sogrey.basic.app;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by prometheus on 05/12/16.
 */

public class ES20SurfaceView extends GLSurfaceView {

    private ES20Renderer es20Renderer;

    public ES20SurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextClientVersion(2);

        es20Renderer = new ES20Renderer(context);
        setRenderer(es20Renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}