package com.interrupt.dungeoneer.entities.triggers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.interrupt.dungeoneer.Art;
import com.interrupt.dungeoneer.annotations.EditorProperty;
import com.interrupt.dungeoneer.game.Level;
import com.interrupt.dungeoneer.overlays.WorldMapOverlay;
import com.interrupt.dungeoneer.overlays.OverlayManager;

public class MapDialogue extends Trigger {

    @EditorProperty
    public String messageFile = "ink-test.json";

    @EditorProperty
    public String backgroundImage = null;

    @EditorProperty
    public Color textColor = null;

    @EditorProperty
    public String triggerIdAfter = null;

    @EditorProperty
    public boolean pausesGame = false;

    public MapDialogue() { hidden = true; spriteAtlas = "editor"; tex = 16; }

    public void init(Level level, Level.Source source) {
        super.init(level, source);
    }

    @Override
    public void doTriggerEvent(String value) {
        // Set a background, if one was given
        NinePatchDrawable background = null;
        if(backgroundImage != null && !backgroundImage.isEmpty()) {
            Texture bg = Art.loadTexture(backgroundImage);
            if(bg != null) {
                background = new NinePatchDrawable(new NinePatch(new TextureRegion(bg), 64, 64, 64, 64));
            }
        }

        WorldMapOverlay overlay = new WorldMapOverlay(messageFile, background, textColor);
        overlay.triggerOnClose = triggerIdAfter;
        overlay.pausesGame = pausesGame;
        OverlayManager.instance.push(overlay);

        super.doTriggerEvent(value);
    }
}
