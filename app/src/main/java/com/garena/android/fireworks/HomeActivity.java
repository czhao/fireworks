package com.garena.android.fireworks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class HomeActivity extends AppCompatActivity{

    NightScene mNightScene;
    SurfaceHolder mSurfaceHolder;
    boolean isSurfaceCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mNightScene = (NightScene)findViewById(R.id.night_scene);
        mSurfaceHolder = mNightScene.getHolder();
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mNightScene.init();
                mNightScene.play();
                isSurfaceCreated = true;
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                isSurfaceCreated = false;
                mNightScene.stop();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //generate the sparks
        if (isSurfaceCreated) {
            mNightScene.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mNightScene.play();
                }
            }, 2000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNightScene.stop();
    }
}
