package ru.battlesity.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.battlesity.game.Label;

public class WinScreen implements Screen {
    private Game game;
    private Texture fon;
    private SpriteBatch batch;
    private Label label;
    private Music win;

    public WinScreen(Game game) {
        this.game = game;
        fon = new Texture("Img/fon/fon.jpg");
        batch = new SpriteBatch();
        label = new Label(100);
        win = Gdx.audio.newMusic(Gdx.files.internal("Music/sonic-win.mp3"));
        win.setVolume(0.25f);
        win.setLooping(false);
        win.play();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        batch.begin();
        batch.draw(fon, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        label.draw(batch,
                "You Win",
                Gdx.graphics.getWidth() / 3,
                Gdx.graphics.getHeight() >> 1);

        batch.end();

        if (Gdx.input.isTouched()) {
            dispose();
            game.setScreen(new MenuScreen(game));
        }
    }

    public int resetRings(int rings){

        return rings;
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
        this.batch.dispose();
        this.fon.dispose();
        this.label.dispose();
        win.dispose();
    }
}
