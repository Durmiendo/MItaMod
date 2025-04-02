package org.durmiendo.mitamod.render.light;

import arc.graphics.Color;
import arc.graphics.gl.Shader;
import arc.math.geom.Vec3;

public class PointLight extends Light {
    public PointLight(Vec3 pos, Color color, float intensity, float size, Vec3 position) {
        super(pos, color, intensity, size);
    }

    @Override
    public void applyToShader(Shader shader, String lightType, int lightIndex) {
        String prefix = lightType + "[" + lightIndex + "].";
        shader.setUniformf(prefix + "color", color);
        shader.setUniformf(prefix + "intensity", intensity);
        shader.setUniformf(prefix + "position", position);
    }
}
