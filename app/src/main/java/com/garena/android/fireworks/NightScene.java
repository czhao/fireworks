package com.garena.android.fireworks;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * PLEASE FILL IN THE CLASS DESCRIPTION
 *
 * @author zhaocong
 */
public class NightScene extends View{

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

    float pixelToMeterRatio; //pixels per meter
    float sceneWidth, sceneHeight = 200f; //expect to support scene with 200 m

    protected void init(){
        //add the sparks
        pixelToMeterRatio =  getHeight() / sceneHeight;
        sceneWidth = getWidth() / pixelToMeterRatio;
        sparks.add(new Spark(sceneWidth/2, 0, 0 ,40f));
        time = System.currentTimeMillis();
        invalidate();
    }

    protected void addSpark(SparkBase base){
        sparks.add(base);
    }

    long time;

    ArrayList<SparkBase> recycleList = new ArrayList<>();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int screenHeight = getHeight();
        long newTime = System.currentTimeMillis();
        long timeDelta = newTime - time;

        for (SparkBase s:sparks){
            if (s.isDead()){
                recycleList.add(s);
            }else {
                PhysicsEngine.move(s, timeDelta);
                int pixelX = (int)(s.mPositionX * pixelToMeterRatio);
                int pixelY = (int)(screenHeight - s.mPositionY * pixelToMeterRatio);
                s.draw(canvas, pixelX, pixelY, true);
            }
        }

        sparks.removeAll(recycleList);
        for (SparkBase s:recycleList){
            s.onDying(this);
        }
        recycleList.clear();

        if (sparks.size() > 0) {
            invalidate();
        }
        time = newTime;
    }
}
