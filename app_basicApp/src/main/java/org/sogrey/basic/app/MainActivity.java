package org.sogrey.basic.app;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isES20Compatible()) {
            glSurfaceView = (ES20SurfaceView) findViewById(R.id.es20SurfaceView);
        }
        else {
            Toast.makeText(getApplicationContext(), "Device DOES NOT support OpenGL ES 2.0", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isES20Compatible() {
        ActivityManager am = (ActivityManager)
                getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return info.reqGlEsVersion >= 0x20000;
    }
}
