package ru.battlesity.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import ru.battlesity.game.*;
import ru.battlesity.game.enums.Actions;
import ru.battlesity.game.enums.Types;
import ru.battlesity.game.persons.Bullet;
import ru.battlesity.game.persons.Sonic;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {
    Game game;
    private SpriteBatch batch;
    private Texture img;
    private Music music;
    private MyInputProcessor myInputProcessor;
    private OrthographicCamera camera;
    private PhysX physX;
    private Body body;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private int[] front, rear;
    private final Sonic sonic;
    private final MyAnimation ringAnm;
    public static List<Body> bodyToDelete;
    public static List<Bullet> bullets;
    private final Label font;
    private int ring;
    private int bulletsCnt;
    private final double viewZoom;


    public GameScreen(Game game) {
        viewZoom = 1.5f; //    ZOOM камеры соника

        bulletsCnt = 100;
        ring = 0;

        font = new Label(12);

        bodyToDelete = new ArrayList<>();
        bullets = new ArrayList<>();
        ringAnm = new MyAnimation("Img/ring.png", 4, 8, 15, Animation.PlayMode.LOOP);
        this.game = game;

        map = new TmxMapLoader().load("map/Tile1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        front = new int[1];
        front[0] = map.getLayers().getIndex("front");
        rear = new int[1];
        rear[0] = map.getLayers().getIndex("rear");

//        TiledMapTileMapObject mo = (TiledMapTileMapObject) map.getLayers().get("damage").getObjects().get("monster1");

        physX = new PhysX();

//        int c = (int) map.getProperties().get("coinsCnt");
        Array<RectangleMapObject> objects = map.getLayers().get("static").getObjects().getByType(RectangleMapObject.class);
        objects.addAll(map.getLayers().get("dynamic").getObjects().getByType(RectangleMapObject.class));
        for (int i = 0; i < objects.size; i++) {
            physX.addObject(objects.get(i));
        }

        Array<PolylineMapObject> shape = map.getLayers().get("static").getObjects().getByType(PolylineMapObject.class);
        for (int i = 0; i < shape.size; i++) {
            physX.addObject(shape.get(i));
        }

        objects.clear();
        objects.addAll(map.getLayers().get("damage").getObjects().getByType(RectangleMapObject.class));
        for (int i = 0; i < objects.size; i++) {
            physX.addDmgObject(objects.get(i));
        }

        body = physX.addObject((RectangleMapObject) map.getLayers().get("hero").getObjects().get("Hero"));
        body.setFixedRotation(true);
        sonic = new Sonic(body);

        TiledMapTileMapObject chest = (TiledMapTileMapObject) map.getLayers().get("static").getObjects().get("chest");

        myInputProcessor = new MyInputProcessor();
        Gdx.input.setInputProcessor(myInputProcessor);

        music = Gdx.audio.newMusic(Gdx.files.internal("Music/OST Sonic — Ending Theme.mp3"));
        music.setVolume(0.025f);
        music.setLooping(false);
        music.getPosition();
        music.isPlaying();
        music.play();

        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.zoom = (float) (1f / viewZoom);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(final float delta) {
        ScreenUtils.clear(Color.BLACK);
        camera.position.x = body.getPosition().x * physX.PPM;
        camera.position.y = body.getPosition().y * physX.PPM;
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render(rear);

        sonic.setTime(delta);
        Vector2 vector = myInputProcessor.getVector();
        Body tBody = sonic.setFPS(body.getLinearVelocity(), true);
        if (tBody != null & bulletsCnt >= 1) {
            bulletsCnt--;
            bullets.add(new Bullet(physX, tBody.getPosition().x, tBody.getPosition().y, sonic.getDir()));
            vector.set(0, 0);
        } else if (tBody != null) {
            vector.set(0, 0);
            sonic.setState(Actions.STAY);
        }
        if (MyContactListener.cnt < 1) {
            vector.set(vector.x, 0);
        }
        body.applyForceToCenter(vector, true);

        ArrayList<Bullet> bTmp = new ArrayList<>();
        batch.begin();
        for (Bullet b : bullets) {
            Body tB = b.update(delta);
            if (tB != null) {
                bodyToDelete.add(tB);
                bTmp.add(b);
            }
        }
        batch.end();
        bullets.removeAll(bTmp);

        Rectangle tmp = sonic.getRect(camera, sonic.getFrame());
        ((PolygonShape) body.getFixtureList().get(0).getShape()).setAsBox(tmp.width / 2, tmp.height / 2);
        ((PolygonShape) body.getFixtureList().get(1).getShape()).setAsBox(
                tmp.width / 3,
                tmp.height / 10,
                new Vector2(0, -tmp.height / 2), 0);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(sonic.getFrame(), tmp.x, tmp.y, tmp.width * PhysX.PPM, tmp.height * PhysX.PPM);

        Array<Body> rings = physX.getBodys("ring");
        if (rings.size == 1) {
            sonic.setFilter((short) (Types.Rings | Types.Chain | Types.Stone));
        }
        font.draw(batch,        // HP
                "HP: " + (sonic.getHit(0)),
                (camera.position.x - tmp.getWidth() - 20),
                (camera.position.y + tmp.getHeight() + 25));
        font.draw(batch,        // Bullets
                "lightning: " + bulletsCnt,
                camera.position.x - (Gdx.graphics.getWidth() / 3.5f),
                camera.position.y - (Gdx.graphics.getHeight() / 3.5f));
        font.draw(batch,        // Rings
                "Rings: " + ring,
                camera.position.x - (Gdx.graphics.getWidth() / 3.5f),
                camera.position.y - (Gdx.graphics.getHeight() / 4f));
        ringAnm.setTime(delta);
        mapRenderer.render(front);
        TextureRegion tr = ringAnm.draw();
        float dScale = 3.2f;
        for (Body bd : rings) {
            float cx = bd.getPosition().x * PhysX.PPM - tr.getRegionWidth() / 2 / dScale;
            float cy = bd.getPosition().y * PhysX.PPM - tr.getRegionHeight() / 2 / dScale;
            float cW = tr.getRegionWidth() / PhysX.PPM / dScale;
            float cH = tr.getRegionHeight() / PhysX.PPM / dScale;
            ((PolygonShape) bd.getFixtureList().get(0).getShape()).setAsBox(cW / 2, cH / 2);
            batch.draw(tr, cx, cy, cW * PhysX.PPM, cH * PhysX.PPM);
        }
        batch.end();


        for (Body bd : bodyToDelete) {
            if (bd.getUserData() != null && bd.getUserData().equals("ring")) {
                ring += 1;
                sonic.getRingCollect().stop();
                sonic.getRingCollect().play(0.5f, 1, 0);
            }
            if (bd.getUserData() != null && bd.getUserData().equals("bullet")) ;

            physX.destroyBody(bd);
        }
        bodyToDelete.clear();

        physX.step();
        physX.debugDraw(camera);

        if (MyContactListener.isDamage) {
            if (sonic.getHit(1) < 1) {
                dispose();
                game.setScreen(new GameOverScreen(game));
                ring = 0;
            }
        }
        if (rings.size == bodyToDelete.size()) {
            dispose();
            ring = 0;
            game.setScreen(new WinScreen(game));
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
        batch.dispose();
        music.dispose();
        map.dispose();
        mapRenderer.dispose();
        this.sonic.dispose();
        this.font.dispose();
        this.physX.dispose();
        this.ringAnm.dispose();
    }
}