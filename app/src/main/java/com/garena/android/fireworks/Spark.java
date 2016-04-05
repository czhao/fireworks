package com.garena.android.fireworks;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * Showcase of basic spark component
 *
 * @author zhaocong
 */
public class Spark extends SparkBase {

    protected static Paint paint;

    static {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.YELLOW);
    }

    private final long lifeSpan = 2000l;

    private int streak = 4;
    private float cacheScreenX, cacheScreenY;

    public Spark(Point3f position, Vector3f v) {
        super(position, v);
        this.scale = 2.5f;
        this.gravity = -0.5f;
        this.drag = 1f;
    }

    @Override
    public void draw(Canvas canvas, float screenX, float screenY, float scale, boolean doEffects) {
        paint.setAlpha(255);
        canvas.drawCircle(screenX, screenY, 1.2f * scale, paint);

        if (System.currentTimeMillis() - startTime > 200){
            float dx = screenX - cacheScreenX;
            float dy = screenY - cacheScreenY;

            for (int i = streak; i > 0; i--){
                paint.setAlpha(255 * i / streak);
                canvas.drawCircle(cacheScreenX, cacheScreenY, 1.5f * scale * i / streak, paint);
                cacheScreenX = cacheScreenX - dx;
                cacheScreenY = cacheScreenY - dy;
            }
        }
        cacheScreenX = screenX;
        cacheScreenY = screenY;
    }

    @Override
    public boolean isDead() {
        return System.currentTimeMillis() - startTime > lifeSpan;
    }

    @Override
    public void onExplosion(NightScene scene) {
        scene.playExplosionSound();
        Random random = new Random();

        int colorA = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
        int colorB = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
        int colorC = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));

        float rootSpeed = random.nextFloat() * 0.5f + 0.5f;

        Vector3f baseV = new Vector3f(rootSpeed, rootSpeed, rootSpeed);

        //explode the core
        for (int i = 0; i < 24; i++){
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
        float rootSpeedShell = random.nextFloat() * 1.8f + 0.8f; // 0.8 - 2.6
        Vector3f shellVelocity = new Vector3f(rootSpeed, rootSpeedShell, rootSpeedShell);
        for (int i = 0; i < 72; i++){
            Vector3f newVelocity = new Vector3f(shellVelocity);
            MathHelper.rotate(newVelocity, Math.random() * 6, Math.random() * 6, Math.random() * 6);

            Vector3f newVelocityInvert = new Vector3f();
            newVelocityInvert.scale(-1f, newVelocity);

            ShellSpark sparkA = new ShellSpark(this.mPosition, newVelocity, shellScale, colorC);
            ShellSpark sparkB = new ShellSpark(this.mPosition, newVelocityInvert, shellScale, colorC);

            scene.addSpark(sparkA);
            scene.addSpark(sparkB);
        }

        float ringScale = 0.95f * random.nextFloat() * 0.8f;
        float rootSpeedRing = random.nextFloat() * 0.8f + .8f;
        baseV = new Vector3f(rootSpeedRing, 0, rootSpeedRing);

        float rx = 1 - 2 * random.nextFloat();
        float rz = 1 - 2 * random.nextFloat();
        Vector3f ringVelocity = new Vector3f(baseV);
        //explode the ring
        for (int i = 0; i < 36; i++){
            MathHelper.rotateY(ringVelocity, random.nextDouble() * 3);
            Vector3f newVelocity = new Vector3f(ringVelocity);
            MathHelper.rotateX(newVelocity,rx);
            MathHelper.rotateZ(newVelocity, rz);
            newVelocity.scale(random.nextFloat() / 5 + 1.5f);

            Vector3f newVelocityInvert = new Vector3f();
            newVelocityInvert.scale(-1f, newVelocity);

            RingSpark sparkA = new RingSpark(this.mPosition, newVelocity, ringScale, colorB);
            RingSpark sparkB = new RingSpark(this.mPosition, newVelocityInvert, ringScale, colorB);
            scene.addSpark(sparkA);
            scene.addSpark(sparkB);
        }
    }
}
