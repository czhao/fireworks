package com.garena.android.fireworks;

import android.graphics.Canvas;

/**
 * PLEASE FILL IN THE CLASS DESCRIPTION
 *
 * @author zhaocong
 */
public abstract class SparkBase {

    protected final long startTime;

    //value manipulated by the physics engine
    protected float mVelocityX, mVelocityY, mPositionX, mPositionY;

    public SparkBase(float x, float y, float vx, float vy) {
        startTime = System.currentTimeMillis();
        mPositionX = x;
        mPositionY = y;
        mVelocityX = vx;
        mVelocityY = vy;
    }

    public abstract void draw(Canvas canvas, float screenWidth, float screenHeight, boolean doEffects);

    public abstract boolean isDead();

    public void onDying(NightScene sc){}
}
