package com.garena.android.fireworks;

import javax.vecmath.Tuple3f;

/**
 * PLEASE FILL IN THE CLASS DESCRIPTION
 *
 * @author zhaocong
 */
public class MathHelper {

    /**
     * Rotate the tuple in place
     * @param origin the original tuple
     * @param x rotate around x
     * @param y rotate around y
     * @param z rotate around z
     */
    public static void rotate(Tuple3f origin, double x, double y, double z){
        //apply x

        double xd = (double)origin.x, yd = (double)origin.y, zd = (double)origin.z;

        double sinX =  Math.sin(x);
        double cosX = Math.cos(x);

        double xx = xd * cosX - yd * sinX;
        double yy = xd * sinX + yd * cosX;

        double sinY = Math.sin(y);
        double cosY = Math.cos(y);

        double xxx = xx * cosY + zd * sinY;
        double zz = - xx * sinY + z * cosY;

        //xxx, yy, zz
        double sinZ  = Math.sin(z);
        double cosZ = Math.cos(z);

        double yyy = yy * cosZ - zz * sinZ;
        double zzz = yy * sinZ + zz * cosZ;

        origin.x = (float)xxx;
        origin.y = (float)yyy;
        origin.z = (float)zzz;

    }

}
