package ru.battlesity.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

public class GdxGame extends ApplicationAdapter {
    private SpriteBatch batch;  // вывод изображений
    private Texture img;        // шкурка графических объектов
    private ShapeRenderer shapeRenderer;
    private Rectangle rectangle, window;
    private Texture goldCoin;
    private Texture rolCoin;
    private MyAtlas stay, run, jump, tmpA;
    MyInputProcessor mip;
    private float x, y;
    int dir = 0, step = 5;

    private int a = 20, b = 20; // отступ от левого и нижнего края

    private Music music;
    private Sound sonicRunSFX, sonicJumpSFX;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        rectangle = new Rectangle();
        window = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        mip = new MyInputProcessor();
        Gdx.input.setInputProcessor(mip);

        music = Gdx.audio.newMusic(Gdx.files.internal("Music/OST Sonic — Ending Theme.mp3"));
        music.setVolume(0.075f);
        music.setLooping(true);
//        music.setPan(0, 0.5f);
        music.play();

        sonicRunSFX = Gdx.audio.newSound(Gdx.files.internal("Sounds/sonic-run.mp3"));
        sonicRunSFX.setLooping(1, true);

        sonicJumpSFX = Gdx.audio.newSound(Gdx.files.internal("Sounds/sonic-jump.mp3"));

        batch = new SpriteBatch();
        img = new Texture("logo.png");
        stay = new MyAtlas("Atlas/sonnic.atlas", "stay", 3, Animation.PlayMode.LOOP);
        run = new MyAtlas("Atlas/sonnic.atlas", "run", 10, Animation.PlayMode.LOOP);
        jump = new MyAtlas("Atlas/sonnic.atlas", "jump", 15, Animation.PlayMode.LOOP);
        tmpA = stay;

    }

    @Override
    public void render() {
        ScreenUtils.clear(1, 1, 1, 1); // RGB (от 0 до 1) , a - прозрачность (от 0 до 1)

        tmpA = stay;
        dir = 0;

        /** Controls */

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            sonicJumpSFX.stop();
            sonicJumpSFX.play();
        }

        if (mip.getOutString().contains("A")) {
            dir = -1;
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
            tmpA = run;
            if (run.equals(tmpA) & Gdx.input.isKeyJustPressed(Input.Keys.D)) {
                sonicRunSFX.stop();
                sonicRunSFX.play(1, 1, 0);
                if (tmpA.equals(stay)) {
                    sonicRunSFX.stop();
                }
            }
        }
        if (mip.getOutString().contains("S")) y -= 5;
        if (mip.getOutString().contains("W")) y += 5;

        if (mip.getOutString().contains("Space")) {
            x = 20;
            y = 20;
            tmpA = jump;

        }

        if (dir == -1) x -= step;
        if (dir == 1) x += step;

        TextureRegion tmp = tmpA.draw();
        if (!tmpA.draw().isFlipX() & dir == -1) tmpA.draw().flip(true, false);
        if (tmpA.draw().isFlipX() & dir == 1) tmpA.draw().flip(true, false);

        rectangle.x = x;
        rectangle.y = y;
        rectangle.width = tmp.getRegionWidth();
        rectangle.height = tmp.getRegionHeight();

        tmpA.setTime(Gdx.graphics.getDeltaTime());

//        System.out.println(mip.getOutString());

        batch.begin();

        batch.draw(img, a, b, Gdx.graphics.getWidth() - 2 * a, Gdx.graphics.getHeight() - 2 * b);
        batch.draw(tmpA.draw(), x, y);

        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        shapeRenderer.end();

        if (!window.contains(rectangle)) Gdx.graphics.setTitle("Out");
        else Gdx.graphics.setTitle("In");
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

    }
}