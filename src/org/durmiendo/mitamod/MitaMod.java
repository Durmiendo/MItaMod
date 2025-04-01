package org.durmiendo.mitamod;

import arc.Events;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.scene.Element;
import arc.scene.Group;
import arc.scene.ui.Dialog;
import arc.util.Reflect;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Planets;
import mindustry.game.EventType;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.graphics.g3d.PlanetParams;
import mindustry.mod.Mod;
import mindustry.type.Planet;
import mindustry.ui.dialogs.BaseDialog;


public class MitaMod extends Mod {
    public MitaMod() {}

    public static InternalFileTree internalFileTree = new InternalFileTree(MitaMod.class);

    public static S3Renderer renderer;

    public static Planet p;


    @Override
    public void init() {
        Pal.accent = Color.valueOf("D23EB5");
        Color.yellow.set(Pal.accent);
        Color.gray.set(Color.valueOf("561A5B"));
        Color.lightGray.set(Color.valueOf("9F16A8"));
        Color.darkGray.set(Color.valueOf("260B28"));
        Pal.accentBack = Color.valueOf("391740");
        Pal.shield = Color.valueOf("A31CFF");
        Pal.reactorPurple = Color.valueOf("AC2AB4");
        Pal.reactorPurple2 = Color.valueOf("8431B4");



        Events.on(EventType.ClientLoadEvent.class, e -> {
            Vars.renderer.planets.cam.far = 1000;
            final Vec3 lightDir = new Vec3(0, -1, 0).nor();
            final Vec3 rs = new Vec3(0,0, 0f);
            final Vec3 scl = new Vec3(2f, 2f, 2f).scl(90f);
            final Vec3 ps = new Vec3(0, -1.0f, -0.22f).scl(scl).scl(1/4f);
            Planets.sun.mesh = new MultiMesh(
                    Planets.sun.mesh,
                    (planetParams, mat3D, mat3D1) -> MitaMod.renderer.mitaMenu.render(ps, rs, mat3D, mat3D1, Vars.renderer.planets.cam, planetParams.planet.position, true, scl)
            );

//            p = new Planet("hiddenSun", Planets.sun, 1f, 2){{
//                position = Planets.sun.position;
//                generator = new TantrosPlanetGenerator();
//                meshLoader = () -> new HexMesh(this, 4);
//                accessible = false;
//                visible = true;
//                atmosphereColor = Color.valueOf("3db899");
//                iconColor = Color.valueOf("597be3");
//                startSector = 10;
//                atmosphereRadIn = -0.01f;
//                atmosphereRadOut = 0.3f;
//                defaultEnv = Env.underwater | Env.terrestrial;
//                ruleSetter = r -> {};
//            }
//
//
//                public void draw(PlanetParams params, Mat3D projection, Mat3D transform) {
//                    if (mesh == null) {
//
//                    }
//                    super.draw(params, projection, transform);
//                }
//            };

            renderer = new S3Renderer();

            Element menu = ((Element) Reflect.get(Vars.ui.menufrag, "container")).parent.parent;
            Group menuCont = menu.parent;
            menuCont.addChildBefore(menu, new Element(){
                public final PlanetParams params = new PlanetParams(){{
                    planet = Planets.sun;
                    camPos = new Vec3(Mathf.cosDeg(Time.time), 0f, Mathf.sinDeg(Time.time)).nor();
                    zoom = 4f;
                }};


                @Override
                public void draw() {
                    params.alwaysDrawAtmosphere = true;
                    params.drawUi = false;

                    params.camPos.set(Mathf.cosDeg(Time.time/6f), 0.2f, Mathf.sinDeg(Time.time/6f));

                    Vars.renderer.planets.render(params);

                }
            });

            Vars.ui.menufrag.addButton("debug", () -> {
                Dialog d = new BaseDialog("debug");
                d.cont.pane(t -> {
                    for (Obj o : renderer.all) {
                        t.table(r -> {
                            r.add("scl " + o.scl).row();
                            for (Obj.Material m : o.materials) {
                                r.table(c -> {
                                    c.label(() -> m.on + "    ");
                                    c.check("rendering", m.renderer, b -> m.renderer = b).row();
                                }).fillX().pad(4f).margin(4f).row();
                            }
                        }).fillX().pad(4f).margin(4f).row();
                    }
                }).scrollX(false).scrollY(true).pad(8f).minSize(300, 300).row();
                d.row();
                d.addCloseButton();
                d.show();
            });
        });

    }
}
