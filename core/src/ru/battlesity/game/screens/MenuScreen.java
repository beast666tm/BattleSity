package ru.battlesity.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.battlesity.game.MyAnimation;

import java.awt.*;

public class MenuScreen implements Screen {
    Game game;
    Texture fon, sign;
    MyAnimation animation;
    Music music;
    SpriteBatch batch;
    int x, y;
    Rectangle rectangle;

    public MenuScreen(Game game) {
        this.game = game;
        fon = new Texture("Img/fon/fon.jpg");
        animation = new MyAnimation("animation/title/titleAnim.png", 2, 5, 3, Animation.PlayMode.LOOP);
        sign = new Texture("Img/StartButtonDown.gif");

        music = Gdx.audio.newMusic(Gdx.files.internal("Music/OST Sonic — Ending Theme.mp3"));
        x = Gdx.graphics.getWidth() / 2 - sign.getWidth() / 2;
//        y = Gdx.graphics.getHeight() / 2 - startButton.getHeight() / 2;
        rectangle = new Rectangle(x, 0, sign.getWidth(), sign.getHeight());
        batch = new SpriteBatch();

        music.setVolume(0.05f);
        music.setLooping(true);
        music.play();

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        animation.setTime(Gdx.graphics.getDeltaTime());
        batch.begin();
//        batch.draw(fon, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(animation.draw(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(sign, x, 0);
        batch.end();

        if (Gdx.input.isTouched()) {
            if (rectangle.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) {
                music.stop();
                dispose();
                game.setScreen(new GameScreen(game));
            }
        }
    }

    @Override
    public void resize(int width, int height) {

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
        this.sign.dispose();
        this.fon.dispose();
        this.batch.dispose();
        animation.dispose();
        music.dispose();

    }
}
