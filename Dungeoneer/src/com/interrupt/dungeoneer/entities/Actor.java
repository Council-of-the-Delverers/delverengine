package com.interrupt.dungeoneer.entities;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.interrupt.dungeoneer.annotations.EditorProperty;
import com.interrupt.dungeoneer.entities.items.Weapon;
import com.interrupt.dungeoneer.entities.items.Weapon.DamageType;
import com.interrupt.dungeoneer.entities.triggers.Trigger;
import com.interrupt.dungeoneer.game.*;
import com.interrupt.dungeoneer.rpg.Stats;
import com.interrupt.dungeoneer.statuseffects.ParalyzeEffect;
import com.interrupt.dungeoneer.statuseffects.PoisonEffect;
import com.interrupt.dungeoneer.statuseffects.ShieldEffect;
import com.interrupt.dungeoneer.statuseffects.StatusEffect;
import com.interrupt.helpers.InterpolationHelper;
import com.interrupt.helpers.InterpolationHelper.InterpolationMode;

public class Actor extends Entity {

	public enum BloodType {
		Red,
		Slime,
		Insect,
		Bone,
		Purple,
		Cyan,
		Blue

	};

	@EditorProperty
	public int hp = 1;

	@EditorProperty
	public int maxHp = 1;

	@EditorProperty
	public int mp = 0;

	@EditorProperty
	public int maxMp = 0;

	@EditorProperty
	public int level = 1;

	@EditorProperty
	public int exp = 0;

	public int ac = 0;

	@EditorProperty
	public int atk = 1;

	@EditorProperty
	public int STR = 10;

	@EditorProperty
	public int DEF = 0;

	@EditorProperty
	public int DEX = 10;

	@EditorProperty
	public int SPD = 10;

	@EditorProperty
	public int INT = 10;

	@EditorProperty
	public BloodType bloodType = BloodType.Red;


	// Resistance Variables
	// NOTE: the Resistances protect against the elemental effects. not the Physical Damage from the item.
	@EditorProperty(group = "Resistances")
	public float iceResistMod = 0.0f;
	@EditorProperty(group = "Resistances")
	public float fireResistMod = 0.0f;
	@EditorProperty(group = "Resistances")
	public float poisonResistMod = 0.0f;
	@EditorProperty(group = "Resistances")
	public float lightningResistMod = 0.0f;
	@EditorProperty(group = "Resistances")
	public float slashingResistMod = 0.0f;
	@EditorProperty(group = "Resistances")
	public float piercingResistMod = 0.0f;
	@EditorProperty(group = "Resistances")
	public float bludgeoningResistMod = 0.0f;


	public Stats stats = new Stats();

	public boolean invisible = false;

	// useful for things like dialogue!
	protected Trigger useTrigger = null;

	public Array<StatusEffect> statusEffects;
	private transient Array<StatusEffect> statusEffectsToRemove = new Array<StatusEffect>();

	// step up lerp
	public transient Float stepUpLerp = null;
	public transient Float stepUpTimer = null;

	public float drunkMod = 0;

	public Actor() { shadowType = ShadowType.BLOB; canStepUpOn = false; }

	public Actor(float x, float y, int tex)
	{
		super(x, y, tex, true);
		canStepUpOn = false;
	}

	public void addExperience(int e)
	{
		exp += e;
	}

	public void initLevel(int newLevel)
	{
		level = newLevel - 1; // 1 = 0
		maxHp += (level * 2);
		hp = maxHp;

		atk += level;
		STR += level;
		DEF += level;
		DEX += level;
		INT += level;

		stats.ATK = 4 + newLevel;
		stats.DEF = 4 + newLevel;
		stats.DEX = 4 + newLevel;
		stats.MAG = 4 + newLevel;
		stats.SPD = 4 + newLevel;
	}

	public int getNextLevel()
	{
		return (level * 4) * (level * 2);
	}

	public int damageRoll(int inAttack, DamageType damageType, Entity instigator)
	{
		Random r = Game.rand;
		int damage = 0;

		float dodgeChance = 0.15f;

		if(r.nextFloat() > dodgeChance) {
			damage = r.nextInt(inAttack) + 1;

			int armorClass = GetArmorClass();
			if(armorClass > 0) {
				damage -= armorClass;
				UseArmor();
			}

			if(damage < 1) damage = 1;
		}

		takeDamage(damage, damageType, instigator);

		return damage;
	}

	protected void UseArmor() { }

	// check rpg/stats.java for adding more
	public float getAttackSpeedStatBoost() {
		return stats.attackSpeedMod;
	}

	public float magicResistMod() { return stats.magicResistMod;}

	public float getKnockbackStatBoost() {
		return stats.knockbackMod;
	}

	public float getMagicResistModBoost() { return stats.magicResistMod; }

	//Resistance Mod Boosts (Elemental)
	public float getFireResistModBoost() {
		return stats.fireResistMod;
	}
	public float getIceResistModBoost() {
		return stats.iceResistMod;
	}
	public float getPoisonResistModBoost() {
		return stats.poisonResistMod;
	}
	public float getLightningResistModBoost() {
		return stats.lightningResistMod;
	}

	//Resistance Mod Boosts (Physical)
	public float getBludgeoningResistModBoost() {
		return stats.bludgeoningResistMod;
	}
	public float getPiercingResistModBoost() { return stats.piercingResistMod; }
	public float getSlashingResistModBoost() {
		return stats.slashingResistMod;
	}


	public float fireResistMod() { return fireResistMod; }

	public float iceResistMod() { return iceResistMod; }

	public float poisonResistMod() { return poisonResistMod; }

	public float lightningResistMod() { return lightningResistMod; }

	public float slashingResistMod() { return slashingResistMod; }

	public float piercingResistMod() { return piercingResistMod; }

	public float bludgeoningResistMod() { return bludgeoningResistMod; }

	public int takeDamage(int damage, DamageType damageType, Entity instigator) {
		// Some status effects change how much damage is being dealt
		if(statusEffects != null && statusEffects.size > 0) {
			for(StatusEffect s : statusEffects) {
				if(s.active) {
					if(damageType == DamageType.PHYSICAL)
					{
						damage *= s.damageMod;
					}

					//Resistance Damages
					if(damageType == DamageType.BLUDGEONING)
					{
						damage *= s.damageMod;
					}
					if(damageType == DamageType.PIERCING)
					{
						damage *= s.piercingDamageMod;
					}
					if(damageType == DamageType.SLASHING)
					{
						damage *= s.slashingDamageMod;
					}

					if(damageType == DamageType.POISON)
					{
						damage *= s.poisonDamageMod;
					}
					if(damageType == DamageType.ICE)
					{
						damage *= s.iceDamageMod;
					}
					if(damageType == DamageType.FIRE)
					{
						damage *= s.fireDamageMod;
					}
					if(damageType == DamageType.LIGHTNING)
					{
						damage *= s.lightningDamageMod;
					}
				}
			}
		}


		if(damage == 0)
			return 0;

		// Apply damage type specific effects
		StatusEffect statusEffect = StatusEffect.getStatusEffect(damageType);
		if (statusEffect != null) {
			this.addStatusEffect(statusEffect);
		}

		// Make it so each resistance can be turned on and off
		// Some base stats affect magic damage / Resistance Mod Boosts (Elemental)
		switch(damageType) {
			//Elemental Cases
			case PHYSICAL:
				damage = (int)Math.ceil(damage - getMagicResistModBoost() * 1f);
				break;

			case FIRE:
				damage = (int)Math.ceil(damage - getFireResistModBoost() * 1f);
				break;

			case POISON:
				damage = (int)Math.ceil(damage - getPoisonResistModBoost() * 1f);
				break;

			case ICE:
				damage = (int)Math.ceil(damage - getIceResistModBoost() * 1f);
				break;

			//Physical Cases
			case BLUDGEONING:
				damage = (int)Math.ceil(damage - getBludgeoningResistModBoost() * 1f);
				break;

			case PIERCING:
				damage = (int)Math.ceil(damage - getPiercingResistModBoost() * 1f);
				break;

			case SLASHING:
				damage = (int)Math.ceil(damage - getSlashingResistModBoost() * 1f);
				break;
		}
		if(damage < 0) damage = 0;

		//if(damageType == DamageType.PHYSICAL) {
			//damage = (int)Math.ceil(damage * (1f - getMagicResistModBoost()));
		//}
		//if(damageType == DamageType.FIRE) {
			//damage = (int)Math.ceil(damage * (1f - getFireResistModBoost()));
		//}
		//if(damageType == DamageType.ICE) {
			//damage = (int)Math.ceil(damage * (1f - getIceResistModBoost()));
		//}
		//if(damageType == DamageType.POISON) {
			//damage = (int)Math.ceil(damage * (1f - getPoisonResistModBoost()));
		//}
		//if(damageType == DamageType.LIGHTNING) {
			//damage = (int)Math.ceil(damage * (1f - getLightningResistModBoost()));
		//}

		//Resistance Modboost (Physical)
		//if(damageType == DamageType.BLUDGEONING) {
			//damage = (int)Math.ceil(damage * (1f - getBludgeoningResistModBoost()));
		//}
		//if(damageType == DamageType.PIERCING) {
			//damage = (int)Math.ceil(damage * (1f - getPiercingResistModBoost()));
		//}
		//if(damageType == DamageType.SLASHING) {
			//damage = (int)Math.ceil(damage * (1f - getSlashingResistModBoost()));
		//}

		// Healing should heal
		if(damageType == DamageType.HEALING) damage *= -1f;

		// Vampire should heal the instigator
		if(damageType == DamageType.VAMPIRE && instigator != null) {
			if(instigator instanceof Actor) {
				Actor inst = (Actor)instigator;
				if(inst.isAlive()) {
					inst.takeDamage(Math.max(1, damage / 3), DamageType.HEALING, instigator);

					Vector3 fromVector = new Vector3();

					for(int i = 1; i < (10 * Options.instance.gfxQuality) + 1; i++) {
						Particle bloodParticle = CachePools.getParticle(x, y, z + 0.5f, 0f, 0f, 0f, 450, 1f, 0f, Actor.getBloodTexture(bloodType), Actor.getBloodColor(bloodType), false);
						bloodParticle.floating = true;
						bloodParticle.checkCollision = false;

						bloodParticle.x += (collision.x * 2) * Game.rand.nextFloat() - collision.x;
						bloodParticle.y += (collision.y * 2) * Game.rand.nextFloat() - collision.y;
						bloodParticle.z += collision.z * Game.rand.nextFloat() - (collision.z * 0.5f);

						fromVector.set(bloodParticle.x, bloodParticle.y, bloodParticle.z).sub(inst.x, inst.y, inst.z + 0.25f);
						float distance = fromVector.len();

						fromVector.nor().scl(-0.07f);

						bloodParticle.xa = fromVector.x;
						bloodParticle.ya = fromVector.y;
						bloodParticle.za = fromVector.z;

						bloodParticle.lifetime = distance * 80f;

						Game.instance.level.SpawnNonCollidingEntity(bloodParticle);
					}
				}
			}
		}

		hp -= damage;

		// clamp out on max hp
		if(hp > getMaxHp()) hp = getMaxHp();

		return damage;
	}


	public boolean isAlive()
	{
		return hp > 0;
	}

	public void bleedEffect(Level level) {
		//TODO: Move bleeding here
	}

	public void hitEffect(Level level, DamageType damageType) {

		Random r = Game.rand;
		int particleCount = 8;
		particleCount *= Options.instance.gfxQuality;

		if(Options.instance.gfxQuality > 0.7f) {
			Particle part = CachePools.getParticle(x, y, z, "dust_puffs", 5);
			part.floating = true;
			part.checkCollision = false;
			part.shader = "spark";
			part.spriteAtlas = "dust_puffs";
			part.lifetime = 15;
			part.color.set(Color.WHITE);
			part.color.a = 0.32f;
			part.endColor = new Color(part.color);
			part.endColor.a = 1;
			part.fullbrite = true;
			level.SpawnNonCollidingEntity(part);
		}

		for(int i = 0; i < particleCount; i++)
		{
			level.SpawnNonCollidingEntity( CachePools.getParticle(x, y, z + 0.5f, r.nextFloat() * 0.02f - 0.01f, r.nextFloat() * 0.02f - 0.01f, r.nextFloat() * 0.02f - 0.01f, 460 + r.nextInt(800), 1f, 0f, Actor.getBloodTexture(bloodType), Actor.getBloodColor(bloodType), false)) ;
		}

		if(damageType != DamageType.PHYSICAL) {
			Color dColor = Weapon.getEnchantmentColor(damageType);

			particleCount = 4;
			particleCount *= Options.instance.gfxQuality;

			for(int i = 0; i < particleCount; i++)
			{
				level.SpawnNonCollidingEntity( CachePools.getParticle(x, y, z + 0.5f, r.nextFloat() * 0.02f - 0.01f, r.nextFloat() * 0.02f - 0.01f, r.nextFloat() * 0.02f - 0.01f, 0, dColor, true)) ;
			}
		}
	}

	public void dieEffect(Level level) {
		Random r = Game.rand;
		int particleCount = 22;
		particleCount *= Options.instance.gfxQuality;

		for(int i = 0; i < particleCount; i++)
		{
			float xPos = x + r.nextFloat() * 0.2f - 0.1f;
			float yPos = y + r.nextFloat() * 0.2f - 0.1f;
			float zPos = z + r.nextFloat() * 0.2f - 0.1f;

			level.SpawnNonCollidingEntity( CachePools.getParticle(xPos, yPos, zPos + 0.5f, r.nextFloat() * 0.04f - 0.02f, r.nextFloat() * 0.04f - 0.02f, r.nextFloat() * 0.05f - 0.02f, 420 + r.nextInt(600), 1f, 0f, Actor.getBloodTexture(bloodType), Actor.getBloodColor(bloodType), false)) ;
		}
	}

	public static Color getBloodColor(BloodType bloodType) {
		Color bloodColor = Colors.DEFAULT_BLOOD;
		if(bloodType == BloodType.Slime) { bloodColor = Colors.SLIME_BLOOD; }
		else if(bloodType == BloodType.Insect) { bloodColor = Colors.INSECT_BLOOD; }
		else if(bloodType == BloodType.Bone) { bloodColor = Colors.BONE_BLOOD; }
		// Custom Blood
		else if(bloodType == BloodType.Blue) { bloodColor = Colors.BLUE_BLOOD; }
		else if(bloodType == BloodType.Cyan) { bloodColor = Colors.CYAN_BLOOD; }
		else if(bloodType == BloodType.Purple) { bloodColor = Colors.PURPLE_BLOOD; }

		return bloodColor;
	}

	public static int getBloodTexture(BloodType bloodType) {
		int ret = 24;
		if(bloodType == BloodType.Slime) { ret = 25; }
		else if(bloodType == BloodType.Insect) { ret = 26; }
		else if(bloodType == BloodType.Bone) { ret = 27; }

		return ret;
	}

	public int GetArmorClass() { return ac; }

	public void tickStatusEffects(float delta) {
		if(!isAlive()) return;

		if(statusEffects != null && statusEffects.size == 0) statusEffects = null;
		if(statusEffects == null) return;

		// tick active
		for(int i = 0; i < statusEffects.size; i++)
		{
			StatusEffect e = statusEffects.get(i);
			e.tick(this, delta);
			if(!e.active) statusEffectsToRemove.add(e);
		}

		// remove inactive
		for(StatusEffect toRemove : statusEffectsToRemove) {
			toRemove.onStatusEnd(this);
			statusEffects.removeValue(toRemove, true);
		}
		statusEffectsToRemove.clear();
	}

	public void addStatusEffect(StatusEffect newEffect) {
		if(statusEffects == null) {
			statusEffects = new Array<StatusEffect>();
		}

		boolean addNewEffect = true;

		// don't stack status effects
		for(StatusEffect e : statusEffects) {
			if(e.getClass().equals(newEffect.getClass())) {
				if(e.timer < newEffect.timer) {
					e.timer = Math.max(newEffect.timer, e.timer);
				}

				addNewEffect = false;
			}
		}

		// remove newly inactive
		for(StatusEffect toRemove : statusEffectsToRemove) {
			toRemove.onStatusEnd(this);
			statusEffects.removeValue(toRemove, true);
		}

		statusEffectsToRemove.clear();

		if (addNewEffect) {
			newEffect.onStatusBegin(this);
			statusEffects.add(newEffect);
		}
	}

	public boolean isStatusEffectActive(StatusEffect se) {
		if (se == null || se.statusEffectType == null) {
			return false;
		}

		if (this.statusEffects == null) {
			return false;
		}

		for (StatusEffect effect: this.statusEffects) {
			if (effect == null || effect.statusEffectType == null) {
				continue;
			}
			if (effect.statusEffectType.equals(se.statusEffectType)) {
				return true;
			}
		}

		return false;
	}

	public void clearStatusEffects() {
		if (this.statusEffects != null) {
			for (StatusEffect e : this.statusEffects) {
				e.onStatusEnd(this);
			}

			this.statusEffects.clear();
		}
	}

	public boolean isPoisoned() {
		if(statusEffects == null) return false;

		for(StatusEffect e : statusEffects) {
			if(e instanceof PoisonEffect) return true;
		}

		return false;
	}

	public boolean isParalyzed() {
		if (this.statusEffects == null) {
			return false;
		}

		for (StatusEffect e : this.statusEffects) {
			if (e instanceof ParalyzeEffect) {
				return true;
			}
		}

		return false;
	}

	public int getMaxHp() {
		return maxHp;
	}

	@Override
	public void stepUp(float posOffset) {
		if(Math.abs(posOffset) > 0.06f) {
			stepUpTimer = 0f;
			stepUpLerp = -posOffset;
		}
	}

	public void stepUpTick(float delta) {
		if(stepUpTimer != null && stepUpLerp != null) {
			stepUpTimer += (delta * 0.0375f);

			if(stepUpTimer > 1f) {
				stepUpTimer = null;
				stepUpLerp = null;
			}
		}
	}

	@Override
	public void use(Player player, float projx, float projy)
	{
		if(useTrigger != null) {
			useTrigger.doTriggerEvent(null);
		}
	}

	public Trigger getUseTrigger() {
		return useTrigger;
	}

	public float getStepUpValue() {
		if(stepUpTimer != null && stepUpLerp != null) {
			return InterpolationHelper.getInterpolator(InterpolationMode.exp5In).apply(1f - stepUpTimer) * stepUpLerp;
		}
		return 0;
	}

	@Override
	public String getShader() {
		if(statusEffects != null && statusEffects.size > 0) {
			for(int i = 0; i < statusEffects.size; i++) {
				StatusEffect effect = statusEffects.get(i);
				if(effect.shader != null) {
					return effect.shader;
				}
			}
		}

		return shader;
	}
}
