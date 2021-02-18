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

public class Obsidian extends Item {
	
	public Obsidian() {
		tex = 0;
		spriteAtlas = "LeoOther";
		artType = ArtType.item;
		name = StringManager.get("items.Obsidian.defaultNameText");
		collidesWith = CollidesWith.staticOnly;
		dropSound = "drops/drop_gold.mp3";
		collision.x = 0.1f;
		collision.y = 0.1f;
	}
	
	@EditorProperty
	public int obsidianAmount = 1;
	
	public boolean autoPickup = false;
	
	public boolean playedDropSound = false;

	public Obsidian(float x, float y) {
		super(x, y, 0, ItemType.gold, StringManager.get("items.Obsidian.defaultNameText"));
	}
	
	public Obsidian(int amount) {
		this();
		obsidianAmount = amount;
		this.name = StringManager.get("items.Obsidian.defaultNameText");

		if(obsidianAmount <= 0) obsidianAmount = 1;
		if(obsidianAmount > 5) tex = 90;
		
		pickupSound = "pu_gold.mp3";
	}

	@Override
	public String GetItemText() {
		return MessageFormat.format(StringManager.get("items.Obsidian.goldItemText"), this.obsidianAmount);
	}
	
	@Override
	public void tick(Level level, float delta)
	{
		super.tick(level, delta);
		
		if(isActive && autoPickup) {
			Player p = Game.instance.player;
			if(Math.abs(p.x + 0.5f - x) < 0.3f && Math.abs(p.y + 0.5f - y ) < 0.3f) {
				p.gold++;
				isActive = false;
			}
		}
	}
	
	protected void pickup(Player player) {
		if(isActive) {
			player.obsidian += obsidianAmount;
			isActive = false;
			Audio.playSound(pickupSound, 0.3f, 1f);
			makeItemPickupAnimation(player);
		}
	}
}
