package org.durmiendo.mitamod.render.light;

import arc.math.geom.Vec3;
import arc.struct.Seq;

public class Lights {
    public static Seq<Light> all = new Seq<>();
    public static Seq<Light> tmp = new Seq<>(32);
    public static Light[] none = new Light[0];


    public static Light[] get(Vec3 pos, Vec3 size) {
        tmp.clear();

        for (Light light : all) {
            float lightSize = light.getSize();
            float distance = pos.dst(light.getPosition());
            if (distance <= lightSize) {
                tmp.add(light);
            }
        }
        return tmp.toArray(Light.class);
    }

}
