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
        rotateX(origin,x);
        rotateY(origin,y);
        rotateZ(origin,z);
    }

    public static void rotateX(Tuple3f origin, double x){
        double yd = (double)origin.y, zd = (double)origin.z;

        double sinX =  Math.sin(x);
        double cosX = Math.cos(x);

        double yy = yd * cosX - zd * sinX;
        double zz = yd * sinX + zd * cosX;

        origin.y = (float)yy;
        origin.z = (float)zz;
    }


    public static void rotateY(Tuple3f origin, double y){
        double xd = (double)origin.x, zd = (double)origin.z;
        double sinY =  Math.sin(y);
        double cosY = Math.cos(y);

        double xx = xd * cosY + zd * sinY;
        double zz = - xd * sinY + zd * cosY;
        origin.x = (float)xx;
        origin.z = (float)zz;
    }

    public static void rotateZ(Tuple3f origin, double z){
        double xd = (double)origin.x, yd = (double)origin.y;
        double sinZ =  Math.sin(z);
        double cosZ = Math.cos(z);
        double xx = xd * cosZ - yd * sinZ;
        double yy = xd * sinZ + yd * cosZ;
        origin.x = (float)xx;
        origin.y = (float)yy;
    }
}
