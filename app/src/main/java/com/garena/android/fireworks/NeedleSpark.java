package com.garena.android.fireworks;

import android.graphics.Paint;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * PLEASE FILL IN THE CLASS DESCRIPTION
 *
 * @author zhaocong
 */
public class NeedleSpark extends RingSpark {

    protected static Paint paint;

    final long lifeSpan = 2500L; // life span 4 seconds


    static {
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public NeedleSpark(Point3f p, Vector3f v, int color) {
        super(p, v, 1f, color);
        this.gravity = -0.75f;
        this.drag = 0.999f;
    }

    @Override
    public boolean isExploding() {
        return System.currentTimeMillis() - startTime > lifeSpan;
    }
}
