package com.garena.android.fireworks;


import javax.vecmath.Vector3f;

/**
 * PLEASE FILL IN THE CLASS DESCRIPTION
 *
 * @author zhaocong
 */
public class PhysicsEngine {

    private static final float WIND = -0.1f;

    static float[] velocity = new float[3];

    static Vector3f tempV = new Vector3f();

    /**
     * Apply the simple physics to calculate the position
     * @param deltaTime million seconds
     */
    static void move(SparkBase spark, long deltaTime){
        //calculate the change in velocity
        //assume velocity X does not change over time
        float delaTimeF = (float)deltaTime;
        //apply the drag
        //Log.d("delta", "delta value" + ((1 - spark.drag * delaTimeF/1000f) * spark.drag));
        spark.mVelocity.scale((1 - spark.drag * delaTimeF / 200000f) * spark.drag);
        spark.mVelocity.get(velocity);
        //x
        velocity[0] += WIND * (float)deltaTime / 1000f;
        //y
        velocity[1] += (float)deltaTime * spark.gravity / 1000f;
        spark.mVelocity.set(velocity);
        tempV.set(velocity);
        tempV.scale(delaTimeF / 1000f);
        spark.mPosition.add(tempV);
    }

}
