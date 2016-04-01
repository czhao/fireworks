package com.garena.android.fireworks;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

/**
 * Stage for animation
 *
 * @author zhaocong
 */
public class NightScene extends SurfaceView{

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
    float sceneWidth, sceneHeight = 200f; //expect to support scene with 200 m
    private boolean isShowOngoing = true;

    protected void init(){
        //add the sparks
        pixelToMeterRatio =  getHeight() / sceneHeight;
        sceneWidth = getWidth() / pixelToMeterRatio;
        sparks.add(new Spark(sceneWidth/2, 0, 0 ,40f));

    }

    protected void addSpark(SparkBase base){
        sparks.add(base);
    }

    long time;

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
                            int pixelX = (int) (s.mPositionX * pixelToMeterRatio);
                            int pixelY = (int) (screenHeight - s.mPositionY * pixelToMeterRatio);
                            s.draw(canvas, pixelX, pixelY, true);
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

                        isShowOngoing = false;
                    }
                    time = newTime;
                    getHolder().unlockCanvasAndPost(canvas);
                }
            }
        }.start();
    }
}
