package org.durmiendo.mitamod.render.loaders;

import arc.graphics.Color;
import arc.graphics.Gl;
import arc.graphics.Mesh;
import arc.graphics.Texture;
import arc.graphics.g3d.Camera3D;
import arc.graphics.gl.Shader;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import arc.util.Disposable;
import org.durmiendo.mitamod.render.S3Renderer;
import org.durmiendo.mitamod.render.light.Light;
import org.durmiendo.mitamod.render.light.Lights;

public class Obj implements Disposable {
    public static Obj zero = new Obj(){};
    public Vec3 size;

    public Seq<Material> materials = new Seq<>();
    public static Shader baseShader = S3Renderer.shader;
    public float scl = 1;

    private static Mat3D tmp = new Mat3D();

    public void render(Vec3 pos, Vec3 rot, Mat3D projection, Mat3D transform, Camera3D cam, Vec3 scale) {
        for (int i = 0; i < materials.size; i++) {
            Material m = materials.get(i);
            if (m.mtl == null) m.mtl = Mtl.zerom;
            if (!m.renderer) continue;

            Shader shader = baseShader;
            shader.bind();

            Texture tex = m.mtl.texture;
            if (tex != null) tex.bind();

            apply(projection, transform, m, shader);

            shader.setUniformf("u_pos", pos);
            shader.setUniformf("u_rot", rot);
            shader.setUniformf("u_campos", cam.position);
            shader.setUniformf("u_camdir", cam.direction);
            shader.setUniformf("u_scale", scale.x * scl, scale.y * scl, scale.z * scl);

            Light[] lights = Lights.get(pos, size);
            for (int j = 0; j < lights.length; j++) {
                Light light = lights[j];
                light.applyToShader(shader, "u_lights", j);
            }

            shader.setUniformi("u_numLights", Lights.all.size);

            m.mesh.bind(shader);
            m.mesh.render(shader, Gl.triangles);
        }
    }

    private void apply(Mat3D projection, Mat3D transform, Material i, Shader shader) {
        shader.setUniformMatrix4("u_proj", projection.val);
        shader.setUniformMatrix4("u_trans", transform.val);

        shader.setUniformf("u_ambient", i.mtl.ambient);
        shader.setUniformf("u_diffuse", i.mtl.diffuse);
        shader.setUniformf("u_specular", i.mtl.specular);
        shader.setUniformf("u_shininess", i.mtl.shininess);
        shader.setUniformi("u_illum", i.mtl.illum);
    }

    public static class Material implements Disposable {
        public String on;
        public Mtl mtl = Mtl.zerom;
        public boolean renderer = true;
        public Mesh mesh;

        @Override
        public void dispose() {
            if (mesh != null) mesh.dispose();
        }
    }

    public static class Mtl {
        public static Mtl zerom = new Mtl();
        public String name;
        public Texture texture;

        public Color ambient = Color.white;
        public Color diffuse = Color.white;
        public Color specular = Color.white;
        public float shininess;

        public int illum = 1;

        public Mtl() {}
    }

    public void dispose() {
        for (int i = 0; i < materials.size; i++) materials.get(i).dispose();
    }
}


