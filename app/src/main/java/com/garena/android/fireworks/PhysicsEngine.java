package com.garena.android.fireworks;

/**
 * PLEASE FILL IN THE CLASS DESCRIPTION
 *
 * @author zhaocong
 */
public class PhysicsEngine {

    private static final float GRAVITY = -9.8f;

    /**
     * Apply the simple physics to calculate the position
     * @param deltaTime million seconds
     */
    static void move(SparkBase spark, long deltaTime){
        //calculate the change in velocity
        //assume velocity X does not change over time
        spark.mVelocityY = spark.mVelocityY + (float)deltaTime * GRAVITY / 1000;
        spark.mPositionX = spark.mPositionX + spark.mVelocityX * deltaTime / 1000;
        spark.mPositionY = spark.mPositionY + spark.mVelocityY * deltaTime / 1000;
    }

}
