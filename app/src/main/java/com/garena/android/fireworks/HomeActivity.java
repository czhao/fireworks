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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mNightScene = (NightScene)findViewById(R.id.night_scene);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //generate the sparks
        mNightScene.postDelayed(new Runnable() {
            @Override
            public void run() {
                mNightScene.init();
            }
        }, 1000);
    }
}
