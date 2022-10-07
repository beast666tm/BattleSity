package ru.battlesity.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
    public static int starting = 1;

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setTitle("Sonic");
        startApp(starting, config);
        config.setWindowedMode(800, 600); // задаёт размер окна
        config.setAudioConfig(32, 512, 512);

    }

    public static void startApp(int i, Lwjgl3ApplicationConfiguration config){
        switch (i){
            case 1:  new Lwjgl3Application(new Main(), config);
                break;
            case 2: new Lwjgl3Application(new GdxGame(), config);
                break;
        }
    }
}