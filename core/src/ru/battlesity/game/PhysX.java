package ru.battlesity.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class PhysX {
     final World world;
    private final Box2DDebugRenderer debugRenderer;

    public PhysX() {
        world = new World(new Vector2(0, -9.81f), true);
        debugRenderer = new Box2DDebugRenderer();
    }

    public void debugDraw(OrthographicCamera camera){debugRenderer.render(world, camera.combined);}
    public void step(){world.step(1/60f, 3, 3);}

    public void dispose(){
        world.dispose();
        debugRenderer.dispose();
    }
}
