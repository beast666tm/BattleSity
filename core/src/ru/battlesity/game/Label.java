package ru.battlesity.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Label {
    private BitmapFont font;

    public Label(int size){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("DiloWorld-mLJLv.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.characters = "QWERTYUIOPASDFGHJKLZXCVBNM*qwertyuiopasdfghjklzxcvbnm_ЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ-йцукенгшщзхъфывапролджэячсмитьбю!.,:1234567890";
        parameter.genMipMaps = true;
        parameter.magFilter = Texture.TextureFilter.MipMapLinearNearest;
        font = generator.generateFont(parameter);
        font.setColor(Color.GOLD);
    }


    public void draw(SpriteBatch batch, String text,float x, float y){font.draw(batch, text, x, y);}

    public void dispose(){
        font.dispose();
    }
}
