package com.garena.android.fireworks;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * Stage for animation
 *
 * @author zhaocong
 */
public class NightScene extends SurfaceView{

    private float sceneWidthHalf, sceneHeightHalf;
    private float densityDpi;

    private final static String TAG = "scene";

    public NightScene(Context context) {
        super(context);
        initDpi(context);
    }

    public NightScene(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDpi(context);
    }

    public NightScene(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDpi(context);
    }

    @TargetApi(21)
    public NightScene(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initDpi(context);
    }

    private List<SparkBase> sparks = new ArrayList<>();

    private ArrayList<SparkBase> recycleList = new ArrayList<>();

    float dpToMeterRatio; //dp per meter
    float pixelMeterRatio; //pixels per meter
    float sceneWidth, sceneDepth = 80f, sceneHeight = 200f; //expect to support scene with 200 m
    private boolean isShowOngoing = true;
    private Random mRandom;

    private void initDpi(Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        densityDpi = metrics.densityDpi;
    }

    protected void init(){
        //add the sparks
        dpToMeterRatio =  pixelToDp(getHeight()) / sceneHeight;
        pixelMeterRatio = getHeight() / sceneHeight;
        sceneWidth = pixelToDp(getWidth()) / dpToMeterRatio; //dynamically calculate the width in meters
        sceneWidthHalf = sceneWidth  / 2;
        sceneHeightHalf = sceneHeight / 2;
        mRandom = new Random();
    }

    protected void addSpark(SparkBase base){
        sparks.add(base);
    }

    private void randomFire(){
        long time = System.currentTimeMillis() - lastFireTime;
        if (time > 2000){

            float x =  (-mRandom.nextFloat() * sceneWidth + sceneWidthHalf) * 0.2f;
            float y =  -mRandom.nextFloat() * 30;
            float z = -mRandom.nextFloat() * sceneDepth /4 - sceneDepth /2;
            Point3f pos = new Point3f(x, y, z);
            Vector3f v = new Vector3f(0, 10f, 0);

            long random  = time % 4;

            if (random == 1l) {
                sparks.add(new Spark(pos, v));
            }else if (random == 2l){
                sparks.add(new WaterfallSpark(pos, v));
            }else if (random == 3){
                sparks.add(new BallSpark(pos, v));
            }else{
                sparks.add(new BallSpark(pos, v));
            }

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
                            float scale = 1.5f * sceneDepth / (sceneDepth + s.mPosition.z);
                            float x2d = s.mPosition.x * scale + sceneWidthHalf;
                            float y2d = s.mPosition.y * scale + sceneHeightHalf;
                            s.draw(canvas, (int)(x2d * pixelMeterRatio), screenHeight - (int)(y2d * pixelMeterRatio),scale, true);
                        }
                    }
                    sparks.removeAll(recycleList);
                    for (SparkBase s : recycleList) {
                        s.onExplosion(NightScene.this);
                    }
                    recycleList.clear();
                    /*if (sparks.size() > 0) {
                        try {
                            //60fps if possible
                            Thread.sleep(5);
                        } catch (Exception e) {
                            //DO NOTHING
                        }
                    } else {
                        randomFire();
                    }*/
                    randomFire();
                    time = newTime;
                    getHolder().unlockCanvasAndPost(canvas);
                }
            }
        }.start();
    }

    private float pixelToDp(float px){
        return px / (densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
