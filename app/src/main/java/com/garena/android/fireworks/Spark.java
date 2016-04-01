package com.garena.android.fireworks;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

/**
 * PLEASE FILL IN THE CLASS DESCRIPTION
 *
 * @author zhaocong
 */
public class Spark extends SparkBase {

    protected Paint paint;

    private final long lifeSpan = 4000l;

    public Spark(float positionX, float positionY, float initVx, float initVy) {
        super(positionX, positionY, initVx, initVy);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
    }

    public void draw(Canvas canvas, float screenX, float screenY, boolean doEffects) {
        canvas.drawCircle(screenX, screenY, 10f, paint);
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

        for (int i = 1; i < 50 ; i++) {
            scene.addSpark(new DyingSpark(this.mPositionX, this.mPositionY, random.nextInt(20), mVelocityY + random.nextInt(20), colorA ));
        }

        for (int i = 1; i < 50 ; i++) {
            scene.addSpark(new DyingSpark(this.mPositionX, this.mPositionY, -random.nextInt(20), mVelocityY + random.nextInt(20), colorA));
        }

        for (int i = 1; i < 50 ; i++) {
            scene.addSpark(new DyingSpark(this.mPositionX, this.mPositionY, -random.nextInt(30) + 10, mVelocityY + random.nextInt(20), colorB));
        }

        for (int i = 1; i < 50 ; i++) {
            scene.addSpark(new DyingSpark(this.mPositionX, this.mPositionY, -random.nextInt(30) - 10, mVelocityY + random.nextInt(20), colorB));
        }
    }
}
