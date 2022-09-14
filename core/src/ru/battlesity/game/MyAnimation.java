package ru.battlesity.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MyAnimation {
    Texture img;
    Animation<TextureRegion> animation;
    private float time;

    public MyAnimation(String name, int row, int col, float fps, Animation.PlayMode playMode) {
        time = 0f;
        img = new Texture(name);
        TextureRegion region1 = new TextureRegion(img);
        TextureRegion[][] regions = region1.split(img.getWidth() / col, img.getHeight() / row);
        animation = new Animation<TextureRegion>(1 / fps, regions[0]);
        animation.setPlayMode(playMode);
    }

    public TextureRegion draw() {
        return animation.getKeyFrame(time);
    }
    public void setTime(float deltaTime){
        time += deltaTime;
    }

    public void dispose(){
        this.img.dispose();
    }

}
