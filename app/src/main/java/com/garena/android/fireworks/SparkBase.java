package com.garena.android.fireworks;

import android.graphics.Canvas;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * PLEASE FILL IN THE CLASS DESCRIPTION
 *
 * @author zhaocong
 */
public abstract class SparkBase {

    protected final long startTime;

    //value manipulated by the physics engine
    protected Point3f mPosition;
    protected Vector3f mVelocity;
    protected float gravity; //customized gravity
    protected float drag; //drag coefficient posed by the air
    protected float scale; //used when convert 3D to 2D

    public SparkBase(Point3f p, Vector3f v) {
        startTime = System.currentTimeMillis();
        //copy the data
        mPosition = new Point3f(p);
        mVelocity = new Vector3f(v);
    }

    public abstract void draw(Canvas canvas, float screenX, float screenY, float scale, boolean doEffects);

    public abstract boolean isExploding();

    public void onExplosion(NightScene sc){}
}
