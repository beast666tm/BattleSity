package ru.battlesity.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

import java.util.ArrayList;

public class MyInputProcessor implements InputProcessor {
    ArrayList outString = new ArrayList<>();

    public MyInputProcessor() {
        outString = new ArrayList<>();
    }

    public ArrayList getOutString(){
        return outString;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (!outString.contains(Input.Keys.toString(keycode))) {
            outString.add(Input.Keys.toString(keycode));
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
       String s = Input.Keys.toString(keycode);
       if(outString.contains(s)){
           outString.remove(s);
        }return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
