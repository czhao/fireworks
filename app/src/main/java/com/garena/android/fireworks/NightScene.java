package com.garena.android.fireworks;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import javax.xml.transform.stream.StreamSource;

/**
 * Stage for animation
 *
 * @author zhaocong
 */
public class NightScene extends SurfaceView{

    private float sceneWidthHalf, sceneHeightHalf;

    private final static String TAG = "scene";

    public NightScene(Context context) {
        super(context);
    }

    public NightScene(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NightScene(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public NightScene(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private List<SparkBase> sparks = new ArrayList<>();

    private ArrayList<SparkBase> recycleList = new ArrayList<>();

    float pixelToMeterRatio; //pixels per meter
    float sceneWidth, sceneDepth = 120f, sceneHeight = 200f; //expect to support scene with 200 m
    private boolean isShowOngoing = true;
    private Random mRandom;

    protected void init(){
        //add the sparks
        pixelToMeterRatio =  getHeight() / sceneHeight;
        sceneWidth = getWidth() / pixelToMeterRatio; //dynamically calculate the width in meters
        Log.d(TAG, "screen width:" + sceneWidth);
        sceneWidthHalf = sceneWidth  / 2;
        sceneHeightHalf = sceneHeight / 2;

        Point3f initStart = new Point3f(0, -30f, -30f);
        Vector3f initVelocity = new Vector3f(0, 20f, 0);

        Vector3f initVelocity2 = new Vector3f(0, 15f, 0);
        sparks.add(new Spark(initStart, initVelocity));

        Point3f initStart2 = new Point3f(10f, -20f, -20f);
        sparks.add(new Spark(initStart2, initVelocity));

        Point3f initStart3 = new Point3f(50f, -10f, 20f);
        sparks.add(new Spark(initStart3, initVelocity2));

        lastFireTime = System.currentTimeMillis();
        mRandom = new Random();
    }

    protected void addSpark(SparkBase base){
        sparks.add(base);
    }

    private void randomFire(){
        if (System.currentTimeMillis() - lastFireTime > 5000){
            float x =  -mRandom.nextFloat() * 0.8f * sceneWidth + sceneWidthHalf;
            float y =  -mRandom.nextFloat() * 20;
            float z = -mRandom.nextFloat() * sceneDepth;
            Point3f pos = new Point3f(x, y, z);
            Vector3f v = new Vector3f(0, 20f, 0);
            sparks.add(new Spark(pos, v));
            lastFireTime = System.currentTimeMillis();
        }
    }

    long time;
    long lastFireTime = 0;

    protected void stop(){
        isShowOngoing = false;
    }

    protected void play(){
        time = System.currentTimeMillis();
        isShowOngoing = true;

        new Thread(){
            @Override
            public void run() {
                while (isShowOngoing) {
                    int screenHeight = getHeight();
                    long newTime = System.currentTimeMillis();
                    long timeDelta = newTime - time;
                    Canvas canvas = getHolder().lockCanvas();
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    for (SparkBase s : sparks) {
                        if (s.isDead()) {
                            recycleList.add(s);
                        } else {
                            PhysicsEngine.move(s, timeDelta);
                            //convert 3D to 2D
                            float scale = sceneDepth / (sceneDepth + s.mPosition.z);
                            float x2d = s.mPosition.x * scale + sceneWidthHalf;
                            float y2d = s.mPosition.y * scale + sceneHeightHalf;
                            s.draw(canvas, (int)(x2d * pixelToMeterRatio), screenHeight - (int)(y2d * pixelToMeterRatio),scale, true);
                        }
                    }
                    sparks.removeAll(recycleList);
                    for (SparkBase s : recycleList) {
                        s.onDying(NightScene.this);
                    }
                    recycleList.clear();
                    if (sparks.size() > 0) {
                        try {
                            //60fps if possible
                            Thread.sleep(16);
                        } catch (Exception e) {
                            //DO NOTHING
                        }
                    } else {
                        randomFire();
                    }
                    time = newTime;
                    getHolder().unlockCanvasAndPost(canvas);
                }
            }
        }.start();
    }
}
