package ru.battlesity.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import ru.battlesity.game.enums.Actions;

import java.util.HashMap;

public class GdxGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture img;
    private HashMap<Actions, MyAtlasAnimation> manAssetss;
    private Music music;
    private Sound sound;
    private MyInputProcessor myInputProcessor;
    private OrthographicCamera camera;
    private PhysX physX;
    private Body body;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Actions actions;

    @Override
    public void create () {
        map = new TmxMapLoader().load("map/безымянный.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        physX = new PhysX();

        Array<RectangleMapObject> objects = map.getLayers().get("env").getObjects().getByType(RectangleMapObject.class);
        objects.addAll(map.getLayers().get("dyn").getObjects().getByType(RectangleMapObject.class));
        for (int i = 0; i < objects.size; i++) {
            physX.addObject(objects.get(i));
        }
        body = physX.addObject((RectangleMapObject) map.getLayers().get("hero").getObjects().get("Hero"));
        body.setFixedRotation(true);

        myInputProcessor = new MyInputProcessor();
        Gdx.input.setInputProcessor(myInputProcessor);

        music = Gdx.audio.newMusic(Gdx.files.internal("MC_Hammer_-_U_Cant_Touch_This_b128f0d256.mp3"));
        music.setVolume(0.025f);
        music.setLooping(true);
        music.play();

        sound = Gdx.audio.newSound(Gdx.files.internal("7b999d49fa57974.mp3"));

        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");

        manAssetss = new HashMap<>();
        manAssetss.put(Actions.STAY, new MyAtlasAnimation("atlas/jamp.atlas", "stand", 17, false, "single_on_dirty_stone_step_flip_flop_007_30443.mp3"));
        manAssetss.put(Actions.RUN, new MyAtlasAnimation("atlas/jamp.atlas", "run", 17, true, "single_on_dirty_stone_step_flip_flop_007_30443.mp3"));
        manAssetss.put(Actions.JUMP, new MyAtlasAnimation("atlas/jamp.atlas", "jamp", 17, true, "single_on_dirty_stone_step_flip_flop_007_30443.mp3"));
        actions = Actions.STAY;

        camera = new OrthographicCamera();
    }
    @Override
    public void render () {
        ScreenUtils.clear(Color.BLACK);

        camera.position.x = body.getPosition().x;
        camera.position.y = body.getPosition().y;
        camera.zoom = 0.5f;
        camera.update();

        mapRenderer.setView(camera);
        mapRenderer.render();

        manAssetss.get(actions).setTime(Gdx.graphics.getDeltaTime());
        body.applyForceToCenter(myInputProcessor.getVector(), true);

        if (body.getLinearVelocity().len() < 0.6f) actions = Actions.STAY;
        else if (Math.abs(body.getLinearVelocity().x) > 0.6f) {actions = Actions.RUN;}

        manAssetss.get(actions).setTime(Gdx.graphics.getDeltaTime());
        if (!manAssetss.get(actions).draw().isFlipX() & body.getLinearVelocity().x < -0.6f) {manAssetss.get(actions).draw().flip(true, false);}
        if (manAssetss.get(actions).draw().isFlipX() & body.getLinearVelocity().x > 0.6f) {manAssetss.get(actions).draw().flip(true, false);}

        float x = body.getPosition().x - 2.5f/camera.zoom;
        float y = body.getPosition().y - 2.5f/camera.zoom;

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(manAssetss.get(actions).draw(), x, y);
        batch.end();

        Gdx.graphics.setTitle(String.valueOf(body.getLinearVelocity()));
        physX.step();
        physX.debugDraw(camera);
    }
    @Override
    public void resize(int width, int height) {
        camera.viewportHeight = height;
        camera.viewportWidth = width;
    }
    @Override
    public void dispose () {
        batch.dispose();
        img.dispose();
        music.dispose();
        sound.dispose();
        map.dispose();
        mapRenderer.dispose();
    }
}