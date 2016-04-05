package com.garena.android.fireworks;

import android.graphics.Color;

import java.util.Random;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * Ball-shape spark
 *
 * @author zhaocong
 */
public class BallSpark extends Spark {

    public BallSpark(Point3f position, Vector3f v) {
        super(position, v);
    }

    @Override
    public void onExplosion(NightScene scene) {
        Random random = new Random();

        int colorA = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));

        float rootSpeed = random.nextFloat() * 0.3f + 1.0f;
        Vector3f baseV = new Vector3f(rootSpeed, rootSpeed, rootSpeed);
        //explode the core
        for (int i = 0; i < 12; i++){
            //generate the sparks
            float randomFactor = (random.nextFloat() + 19)/20f;
            Vector3f newVelocity = new Vector3f(baseV);
            MathHelper.rotate(newVelocity, Math.random() * 3, Math.random() * 3, Math.random() * 3);
            newVelocity.scale(randomFactor);

            Vector3f newVelocityInvert = new Vector3f();
            newVelocityInvert.scale(-1f, newVelocity);
            ShellSpark sparkA = new ShellSpark(this.mPosition, newVelocity, 0.5f, colorA);
            ShellSpark sparkB = new ShellSpark(this.mPosition, newVelocityInvert, 0.5f, colorA);
            scene.addSpark(sparkA);
            scene.addSpark(sparkB);
        }

        //explore the shell
        float shellScale = 0.3f * random.nextFloat() + 0.3f;
        float rootSpeedShell = random.nextFloat() * 1.8f + 1.2f; // 1.2 - 3.0
        Vector3f shellVelocity = new Vector3f(rootSpeed, rootSpeedShell, rootSpeedShell);
        for (int i = 0; i < 72; i++){
            Vector3f newVelocity = new Vector3f(shellVelocity);
            MathHelper.rotate(newVelocity, Math.random() * 6, Math.random() * 6, Math.random() * 6);

            Vector3f newVelocityInvert = new Vector3f();
            newVelocityInvert.scale(-1f, newVelocity);

            ShellSpark sparkA = new ShellSpark(this.mPosition, newVelocity, shellScale, colorA);
            ShellSpark sparkB = new ShellSpark(this.mPosition, newVelocityInvert, shellScale, colorA);

            scene.addSpark(sparkA);
            scene.addSpark(sparkB);
        }
    }
}
