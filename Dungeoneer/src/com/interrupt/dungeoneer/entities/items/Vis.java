package com.interrupt.dungeoneer.entities.items;

import com.badlogic.gdx.math.Vector3;
import com.interrupt.dungeoneer.Audio;
import com.interrupt.dungeoneer.annotations.EditorProperty;
import com.interrupt.dungeoneer.entities.Item;
import com.interrupt.dungeoneer.entities.Player;
import com.interrupt.dungeoneer.game.Game;
import com.interrupt.dungeoneer.game.Level;
import com.interrupt.managers.StringManager;

import java.text.MessageFormat;

public class Vis extends Item {

    public Vis() {
        tex = 88;
        artType = ArtType.item;
        name = StringManager.get("items.Vis.defaultNameText");
        collidesWith = CollidesWith.staticOnly;
        dropSound = "drops/drop_gold.mp3";
        collision.x = 0.1f;
        collision.y = 0.1f;