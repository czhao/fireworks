package com.garena.android.fireworks;

import android.graphics.Color;

import java.util.Random;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * PLEASE FILL IN THE CLASS DESCRIPTION
 *
 * @author zhaocong
 */
public class WaterfallSpark extends Spark {

    public static Random random = new Random();

    public static final int WATERFALL_WIDTH = 60;

    public WaterfallSpark(Point3f position, Vector3f v) {
        super(position, v);
    }

    @Override
    public void onDying(NightScene scene) {
        int colorA = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));

        float ringScale = 0.95f * random.nextFloat() * 0.8f;
        float rootSpeedRing = random.nextFloat() * 1.8f + 4.8f;
        Vector3f baseV = new Vector3f(rootSpeedRing, 0, 0);

        float ry = 1 - 0.5f * random.nextFloat();
        MathHelper.rotateY(baseV, ry);
        float incremental = rootSpeedRing / WATERFALL_WIDTH;

        //explode the ring
        for (int i = 0; i < WATERFALL_WIDTH; i++){
            Vector3f newVelocity = new Vector3f(baseV);
            baseV.x -= incremental;
            Vector3f newVelocityInvert = new Vector3f(newVelocity);
            newVelocityInvert.x = - newVelocity.x;
            RingSpark sparkA = new RingSpark(this.mPosition, newVelocity, ringScale, colorA, -3f, 10);
            RingSpark sparkB = new RingSpark(this.mPosition, newVelocityInvert, ringScale, colorA, -3f, 10);
            scene.addSpark(sparkA);
            scene.addSpark(sparkB);
        }
    }
}
