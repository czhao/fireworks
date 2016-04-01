package com.garena.android.fireworks;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * PLEASE FILL IN THE CLASS DESCRIPTION
 *
 * @author zhaocong
 */
public class DyingSpark extends SparkBase{

    protected Paint paint;

    final long lifeSpan = 2000; // life span 2 seconds


    public DyingSpark(float startX, float startY, float vx, float vy, int color) {
        super(startX, startY, vx, vy);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
    }

    @Override
    public void draw(Canvas canvas, float screenX, float screenY, boolean doEffects) {
        canvas.drawCircle(screenX, screenY , 5f, paint);
    }

    @Override
    public boolean isDead() {
        return System.currentTimeMillis() - startTime > lifeSpan;
    }
}
