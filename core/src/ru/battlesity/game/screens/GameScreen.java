package ru.battlesity.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import ru.battlesity.game.*;
import ru.battlesity.game.persons.Sonic;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {
    Game game;
    private final double viewZoom = 1.5f;
    private SpriteBatch batch;
    private Music music;
    private MyInputProcessor myInputProcessor;
    private OrthographicCamera camera;
    private PhysX physX;
    private Body body;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private int[] front, rear;
    private final MyAnimation ringAnm;
    public static List<Body> bodyToDelete;

    private final Label font;
    private final Sonic sonic;

    public GameScreen(Game game) {

        font = new Label(20);

        bodyToDelete = new ArrayList<>();
        ringAnm = new MyAnimation("Img/ring.png", 4, 8, 15, Animation.PlayMode.LOOP);
        this.game = game;

        map = new TmxMapLoader().load("Map/Tile1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        front = new int[1];
        front[0] = map.getLayers().getIndex("front");
        rear = new int[1];
        rear[0] = map.getLayers().getIndex("rear");

        physX = new PhysX();

        Array<RectangleMapObject> objects = map.getLayers().get("env").getObjects().getByType(RectangleMapObject.class);
        objects.addAll(map.getLayers().get("dyn").getObjects().getByType(RectangleMapObject.class));
        for (int i = 0; i < objects.size; i++) {
            physX.addObject(objects.get(i));
        }

        objects.clear();
        objects.addAll(map.getLayers().get("damage").getObjects().getByType(RectangleMapObject.class));
        for (int i = 0; i < objects.size; i++) {
            physX.addDmgObject(objects.get(i));
        }

        body = physX.addObject((RectangleMapObject) map.getLayers().get("hero").getObjects().get("Hero"));
        body.setFixedRotation(true);
        sonic = new Sonic(body);

        myInputProcessor = new MyInputProcessor();
        Gdx.input.setInputProcessor(myInputProcessor);

        music = Gdx.audio.newMusic(Gdx.files.internal("Music/OST Sonic — Ending Theme.mp3"));
        music.setVolume(0.025f);
        music.setLooping(true);
        music.play();

        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.zoom = (float) (1 / viewZoom);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        MyInputProcessor mip = new MyInputProcessor();
        ScreenUtils.clear(Color.BLACK);

        camera.position.x = body.getPosition().x * physX.PPM;
        camera.position.y = body.getPosition().y * physX.PPM;
        camera.update();

        mapRenderer.setView(camera);
        mapRenderer.render(rear);

        sonic.setTime(delta);
        Vector2 vector = myInputProcessor.getVector();
        if (MyConstantListener.cnt < 1) vector.set(vector.x, 0);
        body.applyForceToCenter(vector, true);
        sonic.setFPS(body.getLinearVelocity(), true);

        Rectangle tmp = sonic.getRect(camera, sonic.getFrame());
        ((PolygonShape) body.getFixtureList().get(0).getShape()).setAsBox(tmp.width / 2, tmp.height / 2);
        ((PolygonShape) body.getFixtureList().get(1).getShape()).setAsBox(
                tmp.width / 3,
                tmp.height / 10,
                new Vector2(0, -tmp.height / 2), 0);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(sonic.getFrame(), tmp.x, tmp.y, tmp.width * PhysX.PPM, tmp.height * PhysX.PPM);

        font.draw(
                batch,
                MyConstantListener.titleString,
                tmp.x - (Gdx.graphics.getWidth() / 3.5f),
                tmp.y + (Gdx.graphics.getHeight() / 3));

        font.draw(
                batch,
                "Health Points: " + String.valueOf(sonic.getHit(0)),
                (camera.position.x - Gdx.graphics.getWidth() / 3),
                (camera.position.y + Gdx.graphics.getHeight() / 3));

        Array<Body> rings = physX.getBodys("ring");
        ringAnm.setTime(delta);
        TextureRegion tr = ringAnm.draw();
        float dScale = 3f;
        for (Body bd : rings) {
            float cx = bd.getPosition().x * PhysX.PPM - tr.getRegionWidth() / 2 / dScale;
            float cy = bd.getPosition().y * PhysX.PPM - tr.getRegionHeight() / 2 / dScale;
            float cW = tr.getRegionWidth() / PhysX.PPM / dScale;
            float cH = tr.getRegionHeight() / PhysX.PPM / dScale;
            ((PolygonShape) bd.getFixtureList().get(0).getShape()).setAsBox(cW / 2, cH / 2);
            batch.draw(tr, cx, cy, cW * PhysX.PPM, cH * PhysX.PPM);
        }

        batch.end();

        mapRenderer.render(front);

        for (Body bd : bodyToDelete) {
            sonic.getRingCollect().stop();
            sonic.getRingCollect().play(0.5f, 1, 0);
            physX.destroyBody(bd);
        }
        bodyToDelete.clear();

        physX.step();
//        physX.debugDraw(camera);

        if (rings.size == bodyToDelete.size()){
            dispose();
            game.setScreen(new WinScreen(game));
        }

        if (MyConstantListener.isDamage) {
            if (sonic.getHit(1) < 1) {
                dispose();
                MyConstantListener.setRings(0);
                game.setScreen(new GameOverScreen(game));
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportHeight = height;
        camera.viewportWidth = width;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

        this.physX.dispose();
        this.batch.dispose();
        this.music.dispose();
        this.map.dispose();
        this.mapRenderer.dispose();
        this.ringAnm.dispose();
        this.sonic.dispose();
        this.font.dispose();
    }
}