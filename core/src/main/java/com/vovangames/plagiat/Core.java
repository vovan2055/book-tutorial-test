package com.vovangames.plagiat;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.vovangames.plagiat.screens.GameScreen;

public class Core extends Game {

    public static final int VIRTUAL_WIDTH = 960, VIRTUAL_HEIGHT = 540;

    @Override
    public void create() {
        setScreen(new GameScreen(this));
    }

    @Override
    public void dispose() {
    }
}
