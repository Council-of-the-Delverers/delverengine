package com.interrupt.dungeoneer.statuseffects;

import com.interrupt.dungeoneer.entities.Actor;
import com.interrupt.dungeoneer.entities.Entity;
import com.interrupt.dungeoneer.entities.Player;
import com.interrupt.dungeoneer.entities.items.Weapon.DamageType;
import com.interrupt.dungeoneer.game.Game;
import com.interrupt.managers.StringManager;


public class StatusEffect {
	public enum StatusEffectType { BURNING, DRUNK, INVISIBLE, PARALYZE, POISON, RESTORE, SHIELD, SLOW, LEVITATE, SPEED, BLEED, SHOCK}

	public String name = StringManager.get("statuseffects.StatusEffect.defaultNameText");
	public float timer = 1000;
	public float speedMod = 1;
	public float damageMod = 1;
	public float magicDamageMod = 1;

	//Resistance Damage
	public float fireDamageMod = 1;
	public float iceDamageMod = 1;
	public float poisonDamageMod = 1;
	public float lightningDamageMod = 1;
	public float bludgeoningDamageMod = 1;
	public float piercingDamageMod = 1;
	public float slashingDamageMod = 1;
	public float frostDamageMod = 1;
	public float thunderDamageMod = 1;


	public boolean active = true;
	public boolean showParticleEffect = true;
	public String shader = null;
	public transient Entity owner = null;
	public StatusEffectType statusEffectType;

	public static StatusEffect getStatusEffect(DamageType damageType) {
		StatusEffect effect = null;

		switch (damageType) {
			case PHYSICAL:
				break;

				//Custom Damage Cases
			case BLUDGEONING:
				break;
			case PIERCING:
				break;
			case SLASHING:
				break;

			case MAGIC:
				break;
			case FIRE:
				if(Game.rand.nextBoolean()) return new BurningEffect();
				break;
			case ICE:
				return new SlowEffect();
			case LIGHTNING:
				break;
			case POISON:
				return new PoisonEffect();
			case BLEEDING:
				return new BleedEffect();
			case HEALING:
				break;
			case PARALYZE:
				return new ParalyzeEffect();
		}

		return effect;
	}

	public static StatusEffect getStatusEffect(StatusEffectType effectType) { //Status Effects
		switch (effectType) {
			case BURNING:
				return new BurningEffect();

			case DRUNK:
				return new DrunkEffect();

			case INVISIBLE:
				return new InvisibilityEffect();

			case PARALYZE:
				return new ParalyzeEffect();

			case POISON:
				return new PoisonEffect();

			case BLEED:
				return new BleedEffect();

			case RESTORE:
				return new RestoreHealthEffect();

			case SHIELD:
				return new ShieldEffect();

			case SLOW:
				return new SlowEffect();

			case SPEED:
				return new SpeedEffect();

		}

		return null;
	}

	public StatusEffect() { }

	public void tick(Actor owner, float delta) {
		if (this.timer > 0) {
			this.timer -= 1 * delta;
		}
		else if (this.active) {
			this.active = false;
		}

		if (this.active) {
			this.doTick(owner, delta);
		}
	}

	public void forPlayer(Player player) { }

	// Override this for different status effects
	public void doTick(Actor owner, float delta) {}

	// Override this for beginning any special status effects
	public void onStatusBegin(Actor owner) {}

	// Override this for ending any special status effects
	public void onStatusEnd(Actor owner) {}
}
