package com.garena.android.fireworks;

import android.graphics.Canvas;
import android.graphics.Paint;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * PLEASE FILL IN THE CLASS DESCRIPTION
 *
 * @author zhaocong
 */
public class ShellSpark extends SparkBase{

    protected Paint paint;

    final long lifeSpan = 2000; // life span 2 seconds


    public ShellSpark(Point3f position, Vector3f v, int color) {
        super(position, v);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        this.scale = 0.5f;
        this.gravity = -0.2f;
        this.drag = 0.92f;
    }

    @Override
    public void draw(Canvas canvas, float screenX, float screenY, float scale, boolean doEffects) {
        canvas.drawCircle(screenX, screenY , 1f * scale, paint);
    }

    @Override
    public boolean isDead() {
        return System.currentTimeMillis() - startTime > lifeSpan;
    }
}