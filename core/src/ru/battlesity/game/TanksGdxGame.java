package ru.battlesity.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class TanksGdxGame extends ApplicationAdapter {
	SpriteBatch batch;  // вывод изображений
	Texture img;        // шкурка графических объектов
	private Texture goldCoin;
	private Texture rolCoin;
	MyAnimation animation;

	@Override
	public void create() {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		animation = new MyAnimation("explosion.png", 1, 48, 20, Animation.PlayMode.LOOP);
	}

	@Override
	public void render() {
		ScreenUtils.clear(0, 0, 0, 1); // RGB (от 0 до 1) , a - прозрачность (от 0 до 1)

		animation.setTime(Gdx.graphics.getDeltaTime());

		float x = Gdx.input.getX() - animation.draw().getRegionWidth() / 2; // возвращает положение мышы по оси X
		float y = (Gdx.graphics.getHeight() - Gdx.input.getY()) - animation.draw().getRegionWidth() / 2; // возвращает положение мышы по оси Y

		batch.begin();

		batch.draw(img, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.draw(animation.draw(), x, y);

//        batch.draw(img, x, y,
//                img.getWidth()/2,img.getHeight()/2,
//                img.getWidth(), img.getHeight(),
//                0.2f, 0.2f,
//                0,
//                0,0,
//                img.getWidth(), img.getHeight(),
//                false,false);
		batch.end();
	}

	@Override
	public void dispose() { // закрывает ресурсы
		batch.dispose();
		img.dispose();
		animation.dispose();

	}
}
