package org.durmiendo.mitamod.render.light;

import arc.graphics.Color;
import arc.graphics.gl.Shader;
import arc.math.geom.Vec3;

public abstract class Light {
    protected Color color;
    protected float intensity;
    protected float size;
    protected Vec3 position;

    public Light(Vec3 pos, Color color, float intensity, float size) {
        this.color = color.cpy();
        this.intensity = intensity;
        this.size = size;
        this.position = pos;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color.set(color);
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public Vec3 getPosition() {
        return position;
    }

    public Vec3 setPosition(Vec3 position) {
        return this.position.set(position);
    }

    public abstract void applyToShader(Shader shader, String lightType, int lightIndex);
}
