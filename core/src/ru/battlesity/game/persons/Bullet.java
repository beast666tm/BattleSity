package ru.battlesity.game.persons;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import ru.battlesity.game.PhysX;

public class Bullet {
    private final Body bull;
    private final static float SPD = 120;
    private float time;

    public Bullet(PhysX physX, float x, float y, int dir) {
        bull = physX.addBullets(x, y);
        bull.setBullet(true);
        bull.setLinearVelocity(new Vector2(SPD * dir, 0));
//        bull.setLinearVelocity(new Vector2((Gdx.graphics.getWidth() / 2 - Gdx.input.getX()) * -1, (Gdx.graphics.getHeight() / 2 - Gdx.input.getY()))); // принимает координаты курсора мыши
//        bull.setGravityScale(1);
        time = 3;
    }

    public Body update(float dTime) {
        time -= dTime;
        return (time <= 0) ? bull : null;
    }


}
