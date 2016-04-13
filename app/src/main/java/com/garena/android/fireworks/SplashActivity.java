package com.garena.android.fireworks;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import bolts.Continuation;
import bolts.Task;

public class SplashActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 1221;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check the permission
        Task.delay(2000L).continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                checkPermission();
                return null;
            }
        });
    }

    private void checkPermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.RECORD_AUDIO);
        if (PackageManager.PERMISSION_GRANTED == permissionCheck) {
            onNavigation(0L);
        }else{
            //request for permission
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_RECORD_AUDIO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //start the recording task
                onNavigation(0L);
            }
        }
    }

    private void onNavigation(long delay){
        Task.delay(delay).continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }
}
