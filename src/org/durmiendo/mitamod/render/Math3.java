package org.durmiendo.mitamod.render;

import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;

public class Math3 {
    public float[] ftmp = new float[9];
    public float[] ftmp1 = new float[9];
    public float[] ftmp2 = new float[9];
    public float[] ftmp3 = new float[9];
    public float[] ftmp4 = new float[9];
    public Mat3D mtmp = new Mat3D();
    public Mat3D mtmp1 = new Mat3D();
    public Mat3D mtmp2 = new Mat3D();
    public Mat3D mtmp3 = new Mat3D();
    public Mat3D mtmp4 = new Mat3D();

    public Mat3D rotationMatrix(Vec3 rot, Mat3D ret) {
        float roll = rot.x;
        float pitch = rot.y;
        float yaw = rot.z;

        float cr = (float)Math.cos(roll);
        float sr = (float)Math.sin(roll);
        float cp = (float)Math.cos(pitch);
        float sp = (float)Math.sin(pitch);
        float cy = (float)Math.cos(yaw);
        float sy = (float)Math.sin(yaw);

        ftmp1[0] = 1; ftmp1[3] = 0  ; ftmp1[6] = 0 ;
        ftmp1[1] = 0; ftmp1[4] = cr ; ftmp1[7] = sr;
        ftmp1[2] = 0; ftmp1[5] = -sr; ftmp1[8] = cr;
        mtmp1.set(ftmp1);

        ftmp1[0] = cp; ftmp1[3] = 0  ; ftmp1[6] = -sp;
        ftmp1[1] = 0 ; ftmp1[4] = 1  ; ftmp1[7] = 0 ;
        ftmp1[2] = sp; ftmp1[5] = 0  ; ftmp1[8] = cp;
        mtmp2.set(ftmp1);

        ftmp1[0] = cy; ftmp1[3] = -sy; ftmp1[6] = 0;
        ftmp1[1] = sy; ftmp1[4] = cy ; ftmp1[7] = 0;
        ftmp1[2] = 0 ; ftmp1[5] = 0  ; ftmp1[8] = 1;
        mtmp3.set(ftmp1);

        ret.idt();
        ret.mul(mtmp1);
        ret.mul(mtmp2);
        ret.mul(mtmp3);

        return ret;
    }
}
