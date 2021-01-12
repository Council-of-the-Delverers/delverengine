package com.interrupt.dungeoneer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.interrupt.dungeoneer.Art;
import com.interrupt.dungeoneer.GameApplication;

public class IntroScreen extends BaseScreen {
    Table fullTable;

    public IntroScreen() {
        this.useBackgroundLevel = true;
        this.backgroundColor.set(0.0F, 0.0F, 0.0F, 1.0F);
        this.ui = new Stage(this.viewport);
        this.fullTable = new Table(this.skin);
        this.fullTable.setFillParent(true);
        this.fullTable.align(1);
        this.ui.addActor(this.fullTable);
        Gdx.input.setInputProcessor(this.ui);
    }

    public void show() {
        this.splashLevel = SplashScreen.splashScreenInfo.backgroundLevel;
        super.show();
        Texture logoTextureOne = Art.loadTexture("SET PIC FILE HERE");
        logoTextureOne.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Image logoOne = new Image(logoTextureOne);
        float width = 128.0F;
        float height = 128.0F;
        this.fullTable.add((Actor)logoOne).size(width, height).align(1).fill(true, true);
        this.fullTable.addAction(

                Actions.sequence(new Action[] { Actions.fadeOut(1.0E-4F),
                        Actions.delay(2.0F),
                        Actions.fadeIn(1.0F),
                        Actions.delay(1.5F),
                        Actions.fadeOut(1.0F),
                        Actions.delay(0.2F), new Action() {
                    public boolean act(float delta) {
                        GameApplication.SetScreen(new SplashScreen());
                        return true;
                    }
                } }));
    }

    public void render(float delta) {
        super.render(delta);
        this.gl.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
        this.gl.glClear(17664);
        this.ui.draw();
    }

    public void tick(float delta) {
        this.ui.act(delta);
    }
}