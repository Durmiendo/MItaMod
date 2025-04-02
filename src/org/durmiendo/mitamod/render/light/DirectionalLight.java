package org.durmiendo.mitamod.render.light;

import arc.graphics.Color;
import arc.graphics.gl.Shader;
import arc.math.geom.Vec3;

public class DirectionalLight extends Light {
    private final Vec3 direction;

    public DirectionalLight(Vec3 pos, Color color, float intensity, float size, Vec3 direction) {
        super(pos, color, intensity, size);
        this.direction = direction.cpy().nor();
    }

    public Vec3 getDirection() {
        return direction;
    }

    public void setDirection(Vec3 direction) {
        this.direction.set(direction).nor();
    }

    @Override
    public void applyToShader(Shader shader, String lightType, int lightIndex) {
        String prefix = lightType + "[" + lightIndex + "].";

        shader.setUniformf(prefix + "color", color);
        shader.setUniformf(prefix + "intensity", intensity);
        shader.setUniformf(prefix + "direction", direction);
        shader.setUniformf(prefix + "position", position);
    }
}
