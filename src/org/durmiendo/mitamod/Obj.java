package org.durmiendo.mitamod;

import arc.graphics.Color;
import arc.graphics.Gl;
import arc.graphics.Mesh;
import arc.graphics.Texture;
import arc.graphics.g3d.Camera3D;
import arc.graphics.gl.Shader;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import arc.struct.Seq;

public class Obj {
    public static Obj zero = new Obj(){
        public void render(Vec3 pos, Vec3 rot, Mat3D projection, Mat3D transform, Camera3D cam, Vec3 light, Vec3 scale) {}
    };

    public Seq<Material> materials = new Seq<>();
    public Shader shader1 = S3Renderer.shader;
    public Shader shader2 = S3Renderer.shader2;
    public float scl = 1;

    public void render(Vec3 pos, Vec3 rot, Mat3D projection, Mat3D transform, Camera3D cam, Vec3 light, boolean lt, Vec3 scale) {
        for (int i = 0; i < materials.size; i++) {
            Material m = materials.get(i);
            if (m.mtl == null) m.mtl = Mtl.zerom;
            if (!m.renderer) continue;

            Shader shader = lt ? shader2 : shader1;
            shader.bind();

            Texture tex = m.mtl.texture;
            if (tex != null) tex.bind();

            apply(projection, transform, m, shader);

            shader.setUniformf("u_pos", pos);
            shader.setUniformf("u_rot", rot);
            shader.setUniformf("u_campos", cam.position);
            shader.setUniformf("u_camdir", cam.direction);
            shader.setUniformf(lt ? "u_lightpos" : "u_lightdir", light);
            shader.setUniformf("u_scale", scale.x * scl, scale.y * scl, scale.z * scl);

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

    public static class Material {
        public String on;
        public Mtl mtl = Mtl.zerom;
        public boolean renderer = true;
        public Mesh mesh;
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
}


