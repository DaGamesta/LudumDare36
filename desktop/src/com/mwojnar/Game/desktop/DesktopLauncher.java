package com.mwojnar.Game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mwojnar.Screens.GameScreen;
import com.mwojnar.Game.LudumDare36Game;

public class DesktopLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Ludum Dare 36";
        config.height = 720;
        config.width = 1280;
        //config.fullscreen = true;
        new LwjglApplication(new LudumDare36Game(arg, GameScreen.class), config);
    }
}