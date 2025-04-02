package org.durmiendo.mitamod.render.light;

import arc.graphics.Color;
import arc.math.geom.Vec3;

public class SunLight extends DirectionalLight {
    public SunLight(Vec3 pos, Color color, float intensity, float size, Vec3 direction) {
        super(pos, color, intensity, size, direction);
    }
}
