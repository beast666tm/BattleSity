package ru.battlesity.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.battlesity.game.Label;

public class GameOverScreen implements Screen {
    Game game;
    Texture gameOverFon;
    SpriteBatch batch;
    Label label;

    public GameOverScreen(Game game) {
        this.game = game;
        gameOverFon = new Texture("Img/fon/GameOver.jpg");
        batch = new SpriteBatch();
        label = new Label(50);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        batch.begin();
        batch.draw(gameOverFon, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        label.draw(batch,
                "Try again",
                Gdx.graphics.getWidth()/3,
                Gdx.graphics.getHeight() >> 1);
        batch.end();

        if (Gdx.input.isTouched()) {
            dispose();
            game.setScreen(new GameScreen(game));
//            MyConstantListener.setRings(0);
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
        this.gameOverFon.dispose();
        this.batch.dispose();
        this.label.dispose();
    }
}
