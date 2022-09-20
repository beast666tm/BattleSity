package ru.battlesity.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class GdxGame extends ApplicationAdapter {
    private SpriteBatch batch;  // вывод изображений
    private Texture img;        // шкурка графических объектов
    private ShapeRenderer shapeRenderer;
    private Rectangle rectangle, window;
    private MyAtlas stay, run, jump, tmpA;
    private MyInputProcessor mip;
    private float x, y;
    private int dir = 0, step = 5;

    private PhysX physX;
    private Body body;
    private OrthographicCamera camera;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    private final int a = 20;
    private final int b = 20; // отступ от левого и нижнего края
    private final float zoom = 1.25f;

    private Music music;
    private Sound sonicRunSFX, sonicJumpSFX;

    @Override
    public void create() {
        map = new TmxMapLoader().load("Map/Tile1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        physX = new PhysX();
        BodyDef def = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        def.type = BodyDef.BodyType.StaticBody;
        fdef.shape = shape;
        fdef.density = 1;                       //  плотность
        fdef.friction = 0;                      //  фрикция
        fdef.restitution = 1;                   //  упругость
        MapLayer env = map.getLayers().get("env");
        Array<RectangleMapObject> rect = env.getObjects().getByType(RectangleMapObject.class);
        for (int i = 0; i < rect.size; i++) {
            float x = rect.get(i).getRectangle().x + rect.get(i).getRectangle().width / 2;
            float y = rect.get(i).getRectangle().y + rect.get(i).getRectangle().height / 2;
            float w = rect.get(i).getRectangle().width / 2;
            float h = rect.get(i).getRectangle().height / 2;
            def.position.set(x, y);
            shape.setAsBox(w, h);
            physX.world.createBody(def).createFixture(fdef).setUserData("Kub");
        }

        def.type = BodyDef.BodyType.DynamicBody;
        def.gravityScale = 1000;
        env = map.getLayers().get("dyn");
        rect = env.getObjects().getByType(RectangleMapObject.class);
        for (int i = 0; i < rect.size; i++) {
            float x = rect.get(i).getRectangle().x + rect.get(i).getRectangle().width / 2;
            float y = rect.get(i).getRectangle().y + rect.get(i).getRectangle().height / 2;
            float w = rect.get(i).getRectangle().width / 2;
            float h = rect.get(i).getRectangle().height / 2;
            def.position.set(x, y);
            shape.setAsBox(w, h);
            fdef.density = 1;
            fdef.friction = 0;
            fdef.restitution = 1;
            physX.world.createBody(def).createFixture(fdef).setUserData("Kub");
        }

        env = map.getLayers().get("hero");
        RectangleMapObject hero = (RectangleMapObject) env.getObjects().get("Hero");
        float x = hero.getRectangle().x +hero.getRectangle().width / 2;
        float y = hero.getRectangle().y +hero.getRectangle().height / 2;
        float w = hero.getRectangle().width / 2;
        float h = hero.getRectangle().height / 2;
        def.position.set(x, y);
        shape.setAsBox(w, h);
        fdef.shape = shape;
        fdef.density = 1;
        fdef.friction = 0;
        fdef.restitution = 1;
        body = physX.world.createBody(def);
        body.createFixture(fdef).setUserData("Kub");

        shape.dispose();

//        shapeRenderer = new ShapeRenderer();
        rectangle = new Rectangle();
        window = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        mip = new MyInputProcessor();
        Gdx.input.setInputProcessor(mip);

        music = Gdx.audio.newMusic(Gdx.files.internal("Music/OST Sonic — Ending Theme.mp3"));
        music.setVolume(0.075f);
        music.setLooping(true);
        music.play();

        sonicRunSFX = Gdx.audio.newSound(Gdx.files.internal("Sounds/sonic-run.mp3"));
        sonicRunSFX.setLooping(1, true);

        sonicJumpSFX = Gdx.audio.newSound(Gdx.files.internal("Sounds/sonic-jump.mp3"));
        sonicJumpSFX.setLooping(1, true);

        batch = new SpriteBatch();
        img = new Texture("logo.png");
        stay = new MyAtlas("Atlas/sonic.atlas", "stay", 3, Animation.PlayMode.LOOP);
        run = new MyAtlas("Atlas/sonic.atlas", "run", 10, Animation.PlayMode.LOOP);
        jump = new MyAtlas("Atlas/sonic.atlas", "jump", 5, Animation.PlayMode.LOOP);
        tmpA = stay;

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1); // RGB (от 0 до 1) , a - прозрачность (от 0 до 1)

        camera.position.x = body.getPosition().x;
        camera.position.y = body.getPosition().y;
        camera.zoom = 1f / zoom;
        camera.update();

        mapRenderer.setView(camera);
        mapRenderer.render();

        tmpA = stay;
        dir = 0;

        /* Controls */

        if (mip.getOutString().contains("A")) {
            dir = -1;
            body.applyForceToCenter(new Vector2(-10_000_000f, 0f),true);
            tmpA = run;
            if (run.equals(tmpA) & Gdx.input.isKeyJustPressed(Input.Keys.A)) {
                sonicRunSFX.stop();
                sonicRunSFX.play(1, 1, 0);
                if (tmpA.equals(stay)) {
                    sonicRunSFX.stop();
                }
            }
        }

        if (mip.getOutString().contains("D")) {
            dir = 1;
            body.applyForceToCenter(new Vector2(10_000_000f, 0f),true);
            tmpA = run;
            if (run.equals(tmpA) & Gdx.input.isKeyJustPressed(Input.Keys.D)) {
                sonicRunSFX.stop();
                sonicRunSFX.play(1, 1, 0);
                if (tmpA.equals(stay)) {
                    sonicRunSFX.stop();
                }
            }
        }

        if (mip.getOutString().contains("S")) {
            tmpA = run;
            y -= 5;
            if (run.equals(tmpA) & Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                sonicRunSFX.stop();
                sonicRunSFX.play(1, 1, 0);
            }
        }

        if (mip.getOutString().contains("W")) {
            tmpA = run;
            y += 5;
            if (run.equals(tmpA) & Gdx.input.isKeyJustPressed(Input.Keys.W)) {
                sonicRunSFX.stop();
                sonicRunSFX.play(1, 1, 0);
            }
        }

        if (mip.getOutString().contains("Space")) {
            body.applyForceToCenter(new Vector2(0, 100_000_000f),true);
            y += 5;
            tmpA = jump;
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                sonicJumpSFX.stop();
                sonicJumpSFX.play();
            }
        }

        tmpA.setTime(Gdx.graphics.getDeltaTime());

        if ((dir == -1) & mip.getOutString().contains("A")) x -= step;
        if (dir == 1 & mip.getOutString().contains("D")) x += step;

        TextureRegion tmp = tmpA.draw();
        if (!tmpA.draw().isFlipX() & dir == -1) tmpA.draw().flip(true, false);
        if (tmpA.draw().isFlipX() & dir == 1) tmpA.draw().flip(true, false);

        rectangle.x = x;
        rectangle.y = y;
        rectangle.width = tmp.getRegionWidth();
        rectangle.height = tmp.getRegionHeight();

        float x = body.getPosition().x - 15 / camera.zoom;
        float y = body.getPosition().y - 15 / camera.zoom;

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(tmpA.draw(), x, y, 30f / camera.zoom, 30f / camera.zoom);  //  отрисовка текущей анимации персонажа
        batch.end();

        if (!window.contains(rectangle)) Gdx.graphics.setTitle(String.valueOf(mip.getOutString()));
        else Gdx.graphics.setTitle(String.valueOf(mip.getOutString()));

        physX.step();
        physX.debugDraw(camera);

//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(Color.ROYAL);
//        shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
//        shapeRenderer.end();

    }

    @Override
    public void resize(int width, int height) {
        camera.viewportHeight = height;
        camera.viewportWidth = width;
    }

    @Override
    public void dispose() { // закрывает ресурсы
        batch.dispose();
        img.dispose();
        stay.dispose();
        run.dispose();
        jump.dispose();
        tmpA.dispose();
        music.dispose();
        sonicRunSFX.dispose();
//        shapeRenderer.dispose();
        map.dispose();
        mapRenderer.dispose();

    }
}