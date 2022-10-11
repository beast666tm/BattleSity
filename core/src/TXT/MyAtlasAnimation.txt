package ru.battlesity.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ru.battlesity.game.persons.Sonic;

public class MyAtlasAnimation {
    TextureAtlas atlas;
    Animation<TextureAtlas.AtlasRegion> animation;
    private float time;
    private Sound sound;
    private boolean loop;
    private float d;

    public MyAtlasAnimation(String atlas, String name, float fps, boolean playMode, String sound){
        if (playMode) loop = true;
        this.sound = Gdx.audio.newSound(Gdx.files.internal(Sonic.sound));
        this.sound.play();
        time = 0;
        this.atlas = new TextureAtlas(atlas);
        animation = new Animation<>(1/fps, this.atlas.findRegions(name));
        animation.setPlayMode(Animation.PlayMode.NORMAL);
        d = animation.getAnimationDuration()/2;
    }

    public TextureRegion draw() {return animation.getKeyFrame(time);}

    public void setTime(float dT) {
        time += dT;
        if (time > d && time < animation.getAnimationDuration()) {
            sound.play();
            d *= 2;
        } else if (time >= animation.getAnimationDuration() && loop) {
            time = 0;
            d = animation.getAnimationDuration() / 2;
            sound.play();
        }
    }

    public void putTime(float time) {
        this.time = time;
    }

    public void dispose() {
        this.atlas.dispose();
        sound.dispose();
    }

}
