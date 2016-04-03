package com.garena.android.fireworks;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * PLEASE FILL IN THE CLASS DESCRIPTION
 *
 * @author zhaocong
 */
public class Spark extends SparkBase {

    protected Paint paint;

    private final long lifeSpan = 4000l;

    public Spark(Point3f position, Vector3f v) {
        super(position, v);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        this.scale = 1.5f;
        this.gravity = -0.3f;
        this.drag = 0.92f;
    }


    @Override
    public void draw(Canvas canvas, float screenX, float screenY, float scale, boolean doEffects) {
        canvas.drawCircle(screenX, screenY, 2f * scale, paint);
    }

    @Override
    public boolean isDead() {
        return System.currentTimeMillis() - startTime > lifeSpan;
    }

    @Override
    public void onDying(NightScene scene) {
        Random random = new Random();

        int colorA = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
        int colorB = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));

        float rootSpeed = random.nextFloat() * 2.5f + 0.5f;

        Vector3f baseV = new Vector3f(rootSpeed, 0, rootSpeed);

        //explode the core
        for (int i = 0; i < 24; i++){
            //generate the sparks
            float randomFactor = (random.nextFloat() + 19)/20f;
            Vector3f newVelocity = new Vector3f(baseV);
            MathHelper.rotate(newVelocity, Math.random() * 3, Math.random() * 3, Math.random() * 3);
            newVelocity.scale(randomFactor);
            Vector3f newVelocityInvert = new Vector3f();
            newVelocityInvert.scale(-1f, newVelocity);
            ShellSpark sparkA = new ShellSpark(this.mPosition, newVelocity, colorA);
            ShellSpark sparkB = new ShellSpark(this.mPosition, newVelocityInvert, colorB);
            scene.addSpark(sparkA);
            scene.addSpark(sparkB);
        }

        //TODO explode the shell

    }
}
