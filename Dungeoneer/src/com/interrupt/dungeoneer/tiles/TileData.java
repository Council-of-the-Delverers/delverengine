package com.interrupt.dungeoneer.tiles;

import com.badlogic.gdx.graphics.Color;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.interrupt.dungeoneer.entities.Actor;
import com.interrupt.dungeoneer.entities.items.Weapon.DamageType;
import com.interrupt.dungeoneer.serializers.KryoSerializer;
import com.interrupt.dungeoneer.statuseffects.StatusEffect;
import com.interrupt.managers.EntityManager;

public class TileData {
	public boolean isWater = false;
	public boolean isLava = false;
	public int hurts = 0;
	public DamageType damageType = DamageType.FIRE;
	
	public int particleTex = 0;
	public boolean particleFullbrite = false;
	public Color particleColor = new Color(Color.WHITE);
	
	public boolean entitiesCanSpawn = true;
	public boolean darkenFloor = false;
	
	public boolean drawCeiling = true;
	public boolean drawWalls = true;
	public Integer flowTex = null;
    public Integer edgeTex = null;
    public Integer flowEdgeTex = null;
	public Color lightColor = null;
	public Color mapColor = null;
	public float friction = 1;
	public String walkSound = null;
    public String ambientSound = null;
    public float ambientSoundVolume = 1f;
    public float ambientSoundRadius = 5f;
    public Float flowingSoundVolume = null;

    public StatusEffect applyStatusEffect = null;

	public void applyStatusEffect(Actor a) {
		if(applyStatusEffect == null)
			return;

		// Update this status effect if it's already going
		if(a.statusEffects != null && a.statusEffects.size > 0) {
			for(int i = 0; i < a.statusEffects.size; i++) {
				StatusEffect e = a.statusEffects.get(i);
				if(e.name.equals(applyStatusEffect.name)) {
					e.timer = Math.max(e.timer, applyStatusEffect.timer);
					return;
				}
			}
		}

		// Otherwise, apply it
		StatusEffect copied = (StatusEffect)KryoSerializer.copyObject(applyStatusEffect);
		if (!a.isStatusEffectActive(copied)) {
			a.addStatusEffect(copied);
		}
	}
}