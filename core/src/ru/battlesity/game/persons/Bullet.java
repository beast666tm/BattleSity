package ru.battlesity.game.persons;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import ru.battlesity.game.PhysX;

public class Bullet {
    private final Body body;
    private final static float SPD = 120;
    private float time;

    public Bullet(PhysX physX, float x, float y, int dir){
        body = physX.addBullet(x, y);
        body.setBullet(true);
        body.setLinearVelocity(new Vector2(SPD*dir, 0));
        //body.setGravityScale(0);
        time = 5;
    }

    public Body update(float dTime){
        time -= dTime;
        return (time<=0)?body:null;
    }

}
