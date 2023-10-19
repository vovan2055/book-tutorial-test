package com.vovangames.plagiat.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.vovangames.plagiat.Core;
import com.vovangames.plagiat.GameWorld;

public class GameScreen extends ScreenAdapter {

    Core game;
    GameWorld gameWorld;
    public GameScreen(Core game) {
        this.game = game;
        gameWorld = new GameWorld();
        Gdx.input.setCursorCatched(true);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK, true);
        gameWorld.render(delta);
    }
    @Override
    public void resize(int width, int height) {
        gameWorld.resize(width, height);
    }
    @Override
    public void dispose() {
        gameWorld.dispose();
    }
}
