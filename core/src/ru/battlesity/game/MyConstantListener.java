package ru.battlesity.game;

import com.badlogic.gdx.physics.box2d.*;
import ru.battlesity.game.screens.GameScreen;

public class MyConstantListener implements ContactListener {
    public static int cnt=0;
    @Override
    public void beginContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        if (a.getUserData().equals("Hero") && b.getUserData().equals("coins")) {
            GameScreen.bodyToDelete.add(b.getBody());
        }
        if (b.getUserData().equals("Hero") && a.getUserData().equals("coins")) {
            GameScreen.bodyToDelete.add(a.getBody());
        }

        if (a.getUserData().equals("legs") && b.getUserData().equals("stone")) {
            //b.getBody().getLinearVelocity();
            cnt++;
        }
        if (b.getUserData().equals("legs") && a.getUserData().equals("stone")) {
            cnt++;
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        if (a.getUserData().equals("legs") && b.getUserData().equals("stone")) {
            cnt--;
        }
        if (b.getUserData().equals("legs") && a.getUserData().equals("stone")) {
            cnt--;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
