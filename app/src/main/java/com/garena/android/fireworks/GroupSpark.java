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
public class GroupSpark extends Spark {

    public GroupSpark(Point3f p, Vector3f v) {
        super(p, v);
    }


    @Override
    public boolean isExploding() {
        return System.currentTimeMillis() - startTime > 3000L;
    }

    @Override
    public void onExplosion(NightScene scene) {
        scene.playExplosionSound();
        Random random = new Random();
        int colorA = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
        float rootSpeed = random.nextFloat() * 0.3f + 0.5f;
        Vector3f baseV = new Vector3f(rootSpeed, 2f, rootSpeed);

        for (int i = 0; i < 72; i++){
            //generate the sparks
            Vector3f newVelocity = new Vector3f(baseV);
            MathHelper.rotate(newVelocity, Math.random() * 3, Math.random() * 3, Math.random() * 3);

            Point3f position = new Point3f(this.mPosition);
            NeedleSpark spin = new NeedleSpark(position, newVelocity, colorA);
            scene.addSpark(spin);

            Vector3f newVelocityInvert = new Vector3f();
            newVelocityInvert.scale(-1f, newVelocity);
            NeedleSpark spinInvert = new NeedleSpark(position, newVelocityInvert, colorA);
            scene.addSpark(spinInvert);
        }
    }

}
