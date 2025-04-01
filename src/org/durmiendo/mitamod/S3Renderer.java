package org.durmiendo.mitamod;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.Gl;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.g3d.Camera3D;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.math.Mat;
import arc.math.Mathf;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Disposable;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Unit;


public class S3Renderer implements Disposable {
    public Camera3D cam;
    public FrameBuffer build;
    public FrameBuffer unit;

    Mat projection = new Mat();
    Mat transformation = new Mat();

    public Obj mitaMenu = ObjParser.loadObj("mita/mitaMenu", 1f/5f);
    public Obj mitaPlane = ObjParser.loadObj("mita/mitapPlane", 1f/7f);

    public Seq<Obj> models = new Seq<>(){{
        add(ObjParser.loadObj("mita/mita1", 1f/5f));
    }};

    public Seq<Obj> all = new Seq<>(){{
        add(models);
        add(mitaMenu);
        add(mitaPlane);
    }};

    public ObjectMap<Building, Obj> objs = new ObjectMap<>();
    public ObjectMap<Unit, Obj> objsu = new ObjectMap<>();

    public static Shader shader = new Shader(
            MitaMod.internalFileTree.child("shaders/3d.vert"),
            MitaMod.internalFileTree.child("shaders/3d.frag")
    );

    public static Shader shader2 = new Shader(
            MitaMod.internalFileTree.child("shaders/3d2.vert"),
            MitaMod.internalFileTree.child("shaders/3d2.frag")
    );

    public TextureRegion r = new TextureRegion();
    public TextureRegion r2 = new TextureRegion();


    public S3Renderer(){
        cam = new Camera3D();
        cam.fov = 50;
        cam.near = 0.1f;
        cam.far = 1000;

        build = new FrameBuffer(2, 2, true);
        unit = new FrameBuffer(2, 2, true);

        Events.on(EventType.WorldLoadEndEvent.class, e -> {
            objs.clear();
            Vars.world.tiles.eachTile(t -> {
                Building building = t.build;

                if (Mathf.chance(0.2d) && building != null) {
                    objs.put(building, models.random());
                }
            });

            Groups.unit.each(u -> {
                if (Mathf.chance(0.2d)) {
                    objsu.put(u, mitaPlane);
                }
            });
        });

        Events.on(EventType.UnitChangeEvent.class, e -> {
            if (Mathf.chance(0.2d)) {
                objsu.put(e.unit, mitaPlane);
            }
        });

        Events.on(EventType.BlockBuildEndEvent.class, e -> {
            Building building = e.tile.build;

            if (Mathf.chance(0.2d) && building != null) {
                objs.put(building, models.random());
            }
        });


        Events.run(EventType.Trigger.preDraw, () -> {
            render(build, true);
            render(unit ,false);
        });

        r.set(build.getTexture());
        r2.set(unit.getTexture());
        Events.run(EventType.Trigger.draw, () -> {
            Draw.z(52f);
            r.set(build.getTexture());
            Draw.rect(r, Core.camera.position.x, Core.camera.position.y, Core.graphics.getWidth() / Vars.renderer.getDisplayScale(), Core.graphics.getHeight() / Vars.renderer.getDisplayScale());

            Draw.z(92f);
            r2.set(unit.getTexture());
            Draw.rect(r2, Core.camera.position.x, Core.camera.position.y, Core.graphics.getWidth() / Vars.renderer.getDisplayScale(), Core.graphics.getHeight() / Vars.renderer.getDisplayScale());
        });
    }


    public Vec3 tmp = new Vec3(0f, 0f, 0f);
    public Vec3 tmp2 = new Vec3(0f, 0f, 0f);
    public Vec3 tmp4 = new Vec3(0f, 0f, 0f);
    public Vec3 lights = new Vec3(0f, 0, 1f);

    public Mat3D mtmp2 = new Mat3D();

    public void render(FrameBuffer buffer, boolean t) {
        Draw.flush();
        Gl.clear(Gl.depthBufferBit);
        Gl.enable(Gl.depthTest);
        Gl.depthMask(true);

        Gl.enable(Gl.cullFace);
        Gl.cullFace(Gl.back);

        cam.up.set(Vec3.Y);

        projection.setOrtho(0, 0, Core.graphics.getWidth(), Core.graphics.getHeight());
        transformation.idt();

        cam.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
        cam.position.set(0, 0, 0);
//        cam.lookAt(0, 0f, -1);
        cam.direction.set(0, 0,-1f);
        cam.update();

        buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());


        buffer.begin(Color.clear);
        shader2.bind();

        if (t) baseRender();
        else mitaRender();

        buffer.end();

        Gl.disable(Gl.cullFace);
        Gl.disable(Gl.depthTest);
        Gl.depthMask(false);

        Draw.blit(buffer, shader2);
    }

    private void mitaRender() {
        mtmp2.set(transformation);


        for (ObjectMap.Entry<Unit, Obj> b : objsu) {
            if (b.key.dead() || !b.key.isValid()) {
                objsu.remove(b.key);
                continue;
            }
            scale.set(1f, 1f, 1f).scl(1f/7f).scl(b.key.type.hitSize/8f);
            tmp2.set(-Mathf.PI/2f,Mathf.PI, 0f);
            tmp4.set(b.key.getX()+2, b.key.getY(), 0f);

            tmp.set(tmp4);
            tmp.sub(Core.camera.position.x, Core.camera.position.y, 0);
            tmp.y *= -1f;

            tmp.x *= ((2.3645161f*1.9f)/Core.graphics.getHeight());
            tmp.y *= ((2.3645161f*1.9f)/Core.graphics.getHeight());
            tmp.z = -2.3645161f*2f/Vars.renderer.getDisplayScale();

            b.value.render(tmp, tmp2, cam.combined, mtmp2, cam, lights, false, scale);
        }
    }


    Vec3 scale = new Vec3(1f, 1f, 1f);

    void baseRender() {
        mtmp2.set(transformation);


        for (ObjectMap.Entry<Building, Obj> b : objs) {
            if (b.key.dead() || !b.key.isValid()) {
                objs.remove(b.key);
                continue;
            }
            scale.set(1f, 1f, 1f).scl(1f/7f).scl(b.key.block.size);
            tmp2.set(-Mathf.PI/2f,Mathf.PI, 0f);
            tmp4.set(b.key.getX()+2, b.key.getY(), 0f);

            tmp.set(tmp4);
            tmp.sub(Core.camera.position.x, Core.camera.position.y, 0);
            tmp.y *= -1f;

            tmp.x *= ((2.3645161f*1.9f)/Core.graphics.getHeight());
            tmp.y *= ((2.3645161f*1.9f)/Core.graphics.getHeight());
            tmp.z = -2.3645161f*2f/Vars.renderer.getDisplayScale();

            b.value.render(tmp, tmp2, cam.combined, mtmp2, cam, lights, false, scale);
        }
    }

    public void dispose() {
        if (shader != null) {
            shader.dispose();
        }
    }
}

