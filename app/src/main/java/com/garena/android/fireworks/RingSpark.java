package com.garena.android.fireworks;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * PLEASE FILL IN THE CLASS DESCRIPTION
 *
 * @author zhaocong
 */
public class RingSpark extends SparkBase{

    //protected Paint paint;

    final long lifeSpan;
    private int blurFactor = 8;
    private int color;
    private int cacheIndex = 0;
    private int alpha;
    private float[][] drawingCache;
    private boolean isCacheFilled = false;

    static Paint paint;
    static  {
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public RingSpark(Point3f position, Vector3f v, float scale, int color) {
        super(position, v);
        //paint = new Paint();
        this.scale = scale;
        this.gravity = -0.75f;
        this.drag = 0.988f;
        this.color = color;
        this.alpha = 50;
        blurFactor = 8;
        drawingCache = new float[blurFactor][2];
        lifeSpan = 5000l;
    }

    public RingSpark(Point3f position, Vector3f v, float scale, int color, float gravity, int streak) {
        super(position, v);
        //paint = new Paint();
        this.scale = scale;
        this.gravity = gravity;
        this.drag = 0.988f;
        this.color = color;
        this.alpha = 50;
        blurFactor = streak;
        lifeSpan = streak * 300;
        drawingCache = new float[blurFactor][2];
    }

    @Override
    public void draw(Canvas canvas, float screenX, float screenY, float scale, boolean doEffects) {
        //reset the painter
        paint.setColor(color);
        drawingCache[cacheIndex][0] = screenX;
        drawingCache[cacheIndex][1] = screenY;
        cacheIndex++;
        if (cacheIndex == blurFactor){
            isCacheFilled = true;
            cacheIndex = 0;
        }

        if (System.currentTimeMillis() - this.startTime > 1000l) {
            alpha = alpha - 5;
        }else{
            //cached not fill yet
            alpha = alpha + 50;
        }

        if (alpha > 255){
            alpha = 255;
        }else if (alpha < 0){
            alpha = 0;
        }

        if (isCacheFilled && alpha >= 0) {
            paint.setAlpha(alpha);
            Path p = new Path();
            p.moveTo(screenX, screenY);
            for (int i = blurFactor - 1; i >= 0; i--) {
                p.lineTo(drawingCache[i][0], drawingCache[i][1]);
                canvas.drawPath(p,paint);
            }
        }
    }

    @Override
    public boolean isExploding() {
        return System.currentTimeMillis() - startTime > lifeSpan;
    }
}
