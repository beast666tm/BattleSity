package ru.battlesity.game;

import com.badlogic.gdx.Game;
import ru.battlesity.game.screens.MenuScreen;

public class Main extends Game {

    @Override
    public void create() {setScreen(new MenuScreen(this));}

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {

    }
}
