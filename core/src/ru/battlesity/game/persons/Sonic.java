package ru.battlesity.game.persons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import ru.battlesity.game.PhysX;
import ru.battlesity.game.enums.Actions;
import ru.battlesity.game.enums.Types;

import java.util.HashMap;

public class Sonic {
    HashMap<Actions, Animation<TextureRegion>> sonicAnim;
    private Sound sonicRunSFX, sonicJumpSFX, sonicLightSFX, ringCollect;

    public Sound getRingCollect() {
        return ringCollect;
    }

    public Sound getSonicLightSFX() {
        return sonicLightSFX;
    }

    public Sound getSonicRunSFX() {
        return sonicRunSFX;
    }

    public Sound getSonicJumpSFX() {
        return sonicJumpSFX;
    }

    private final float FPS = 1 / 7f;
    private float time;
    public static boolean canJump, isFire;
    private Animation<TextureRegion> baseAnm;
    private boolean loop;
    private TextureAtlas atl;
    private Body body;
    private Dir dir;
    private static float dScale = 2.8f;
    private float hitPoints, live;

    public enum Dir {LEFT, RIGHT}

    public Sonic(Body body) {
        sonicLightSFX = Gdx.audio.newSound(Gdx.files.internal("Sounds/light.mp3"));
        sonicLightSFX.setLooping(0, false);

        sonicRunSFX = Gdx.audio.newSound(Gdx.files.internal("Sounds/sonic-run.mp3"));
        sonicRunSFX.setLooping(0, true);

        sonicJumpSFX = Gdx.audio.newSound(Gdx.files.internal("Sounds/sonic-jump.mp3"));
        sonicJumpSFX.setLooping(0, false);

        ringCollect = Gdx.audio.newSound(Gdx.files.internal("Sounds/collect-ring.mp3"));
        ringCollect.setLooping(0, false);

        hitPoints = live = 100;
        this.body = body;
        sonicAnim = new HashMap<>();
        atl = new TextureAtlas("atlas/sonic.atlas");
        sonicAnim.put(Actions.JUMP, new Animation<TextureRegion>(FPS, atl.findRegions("jump")));
        sonicAnim.put(Actions.RUN, new Animation<TextureRegion>(FPS, atl.findRegions("run")));
        sonicAnim.put(Actions.FAST_RUN, new Animation<TextureRegion>(FPS, atl.findRegions("fast-run"))); // fast-run
        sonicAnim.put(Actions.STAY, new Animation<TextureRegion>(FPS, atl.findRegions("stay")));
        sonicAnim.put(Actions.SHOOT, new Animation<TextureRegion>(FPS, atl.findRegions("energy")));
        baseAnm = sonicAnim.get(Actions.STAY);
        loop = true;
        dir = Dir.LEFT;
    }

    public void setFilter(short f) {
        Filter filter = new Filter();
        filter.categoryBits = Types.Hero;
        filter.maskBits = f;
        body.getFixtureList().get(0).setFilterData(filter);
    }

    public float getHit(float damage) {
        hitPoints -= damage;
        return hitPoints;
    }

    public int getDir() {
        return (dir == Dir.LEFT) ? -1 : 1;
    }

    public boolean isCanJump() {
        return canJump;
    }

    public static void setCanJump(boolean isJump) {
        canJump = isJump;
    }

    public void setDir(Dir dir) {
        this.dir = dir;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public Body setFPS(Vector2 vector, boolean onGround) {
        if (vector.x > 0.1f) setDir(Dir.RIGHT);
        if (vector.x < -0.1f) setDir(Dir.LEFT);
        float tmp = (float) (Math.sqrt(vector.x * vector.x + vector.y * vector.y)) * 10;
        setState(Actions.STAY);
        if (isFire) {
            setState(Actions.SHOOT);
            return body;
        }
        if (Math.abs(vector.x) > 0.25f && Math.abs(vector.y) < 10 && onGround) {
            setState(Actions.RUN);
            if (Math.abs(vector.x) > 6.25f && Math.abs(vector.y) < 10 && onGround) {
                setState(Actions.FAST_RUN);
                baseAnm.setFrameDuration(5 / tmp);
                return null;
            }
            baseAnm.setFrameDuration(1 / tmp);
            return null;
        }
        if (Math.abs(vector.y) > 1 && canJump & Gdx.input.isKeyPressed(Input.Keys.W)) {
            setState(Actions.JUMP);
            sonicJumpSFX.stop();
            sonicJumpSFX.play(0.5f, 1, 1);
            baseAnm.setFrameDuration(FPS);
            return null;
        }
        return null;
    }

    public float setTime(float deltaTime) {
        time += deltaTime;
        return time;
    }

    public void setState(Actions state) {
        baseAnm = sonicAnim.get(state);
        switch (state) {
            case STAY:
                loop = true;
                baseAnm.setFrameDuration(FPS);
                break;
            case SHOOT:
                loop = true;
                baseAnm.setFrameDuration(FPS);
                break;
            case JUMP:
                loop = false;
                break;
            default:
                loop = true;
        }
    }

    public TextureRegion getFrame() {
        if (time > baseAnm.getAnimationDuration() && loop) time = 0;
        if (time > baseAnm.getAnimationDuration()) time = 0;
        TextureRegion tr = baseAnm.getKeyFrame(time);
        if (!tr.isFlipX() && dir == Dir.LEFT) tr.flip(true, false);
        if (tr.isFlipX() && dir == Dir.RIGHT) tr.flip(true, false);
        return tr;
    }

    public Rectangle getRect(OrthographicCamera camera, TextureRegion region) {
        TextureRegion tr = baseAnm.getKeyFrame(time);
        float cx = body.getPosition().x * PhysX.PPM - tr.getRegionWidth() / 2 / dScale;
        float cy = body.getPosition().y * PhysX.PPM - tr.getRegionHeight() / 2 / dScale;
        float cW = tr.getRegionWidth() / PhysX.PPM / dScale;
        float cH = tr.getRegionHeight() / PhysX.PPM / dScale;
        return new Rectangle(cx, cy, cW, cH);
    }

    public void dispose() {
        atl.dispose();
        this.sonicAnim.clear();
    }
}
