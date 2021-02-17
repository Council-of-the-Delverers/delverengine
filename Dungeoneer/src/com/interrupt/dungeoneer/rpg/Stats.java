package com.interrupt.dungeoneer.rpg;

import com.badlogic.gdx.utils.Array;
import com.interrupt.dungeoneer.entities.Item;
import com.interrupt.dungeoneer.entities.Player;
import com.interrupt.dungeoneer.entities.items.Armor;
import com.interrupt.dungeoneer.entities.items.ItemModification;

public class Stats {

	public Stats() { }

	public Stats(int ATK, int DEF, int DEX, int SPD, int MAG, int END) {
		this.ATK = ATK;
		this.DEF = DEF;
		this.DEX = DEX;
		this.SPD = SPD;
		this.MAG = MAG;
		this.END = END;
	}

	public int ATK = 4;
	public int DEF = 4;
	public int DEX = 4;
	public int SPD = 4;
	public int MAG = 4;
	public int END = 4;
	public int HP = 8;

	public float attackSpeedMod = 0f;

	//Custom Damage Mods
	public float iceDamageMod = 0f;
	public float knockbackMod = 0f;
	public float magicResistMod = 0f;
	public float fireResistMod = 0f;
	public float iceResistMod = 0f;
	public float poisonResistMod = 0f;
	public float lightningResistMod = 0f;
	public float slashingResistMod = 0f;
	public float piercingResistMod = 0f;
	public float bludgeoningResistMod = 0f;
	public float frostResistMod = 0f;
	public float thunderResistMod = 0f;

	private transient int LAST_ATK;
	private transient int LAST_DEF;
	private transient int LAST_DEX;
	private transient int LAST_SPD;
	private transient int LAST_MAG;
	private transient int LAST_END;
	private transient int LAST_HP;

	private transient float LAST_attackSpeedMod;
	private transient float LAST_iceDamageMod;
	private transient float LAST_knockbackMod;
	private transient float LAST_magicResistMod;
	private transient float LAST_fireResistMod;
	private transient float LAST_iceResistMod;
	private transient float LAST_poisonResistMod;
	private transient float LAST_lightningResistMod;
	private transient float LAST_slashingResistMod;
	private transient float LAST_piercingResistMod;
	private transient float LAST_bludgeoningResistMod;
	private transient float LAST_frostResistMod;
	private transient float LAST_thunderResistMod;

	public void ResetStats() {

		LAST_ATK = ATK;
		LAST_DEF = DEF;
		LAST_DEX = DEX;
		LAST_SPD = SPD;
		LAST_MAG = MAG;
		LAST_END = END;
		LAST_HP = HP;

		LAST_attackSpeedMod = attackSpeedMod;
		LAST_iceDamageMod = iceDamageMod;
		LAST_knockbackMod = knockbackMod;
		LAST_magicResistMod = magicResistMod;
		LAST_fireResistMod = fireResistMod;
		LAST_iceResistMod = iceResistMod;
		LAST_poisonResistMod = poisonResistMod;
		LAST_lightningResistMod = lightningResistMod;
		LAST_slashingResistMod = slashingResistMod;
		LAST_piercingResistMod = piercingResistMod;
		LAST_bludgeoningResistMod = bludgeoningResistMod;
		LAST_frostResistMod = frostResistMod;
		LAST_thunderResistMod = thunderResistMod;

		ATK = 0;
		DEF = 0;
		DEX = 0;
		SPD = 0;
		MAG = 0;
		END = 0;
		HP = 0;

		attackSpeedMod = 0;
		knockbackMod = 0;
		magicResistMod = 0;

		//Resistance Mods
		fireResistMod = 0;
		iceResistMod = 0;
		poisonResistMod = 0;
		lightningResistMod = 0;
		slashingResistMod = 0;
		piercingResistMod = 0;
		bludgeoningResistMod = 0;
		frostResistMod = 0;
		thunderResistMod = 0f;

		iceDamageMod = 0;
	}

	public void Recalculate(Player player) {
		ResetStats();
		boolean holdingTwoHanded = player.isHoldingTwoHanded();

		for(Item item : player.equippedItems.values()) {
			// Offhand items should be skipped when the player is holding a two handed weapon
			if(item == null) continue;
			if((item.equipLoc != null && item.equipLoc.equals("OFFHAND")) && holdingTwoHanded) continue;
			addItemStats(item);
		}

		addItemStats(player.GetHeldItem());
	}

	public void addItemStats(Item item) {
		if(item == null) return;

		for(ItemModification m : item.getEnchantments()) {
			addItemStats(item, m);
		}

		if(item instanceof Armor) {
			Armor armor = (Armor)item;
			DEF += armor.GetArmor();
			addItemStats(armor, armor.baseMods);
		}
	}

	public void addItemStats(Item i, ItemModification m) {
		if(m == null) return;
		HP += m.getHpMod(i);
		SPD += m.getMoveSpeedMod(i);
		MAG += m.getMagicMod(i);
		DEX += m.getAgilityMod(i);
		ATK += m.getAttackMod(i);
		DEF += m.getArmorMod(i);
		attackSpeedMod += m.getAttackSpeedMod(i);
		knockbackMod += m.getKnockbackMod(i);
		magicResistMod += m.getMagicResistMod(i);
		fireResistMod += m.getFireResistMod(i);
		iceResistMod += m.getIceResistMod(i);
		poisonResistMod += m.getPoisonResistMod(i);
		lightningResistMod += m.getLightningResistMod(i);

		frostResistMod = m.getFrostResistMod(i);
		thunderResistMod = m.getThunderResistMod(i);

		iceDamageMod += m.getIceDamageMod(i);

		bludgeoningResistMod += m.getBludgeoningResistMod(i);
		piercingResistMod += m.getPiercingResistMod(i);
		slashingResistMod += m.getSlashingResistMod(i);
	}

	public boolean statsChanged() {
		return (ATK != LAST_ATK || DEF != LAST_DEF || DEX != LAST_DEX || SPD != LAST_SPD || MAG != LAST_MAG || END != LAST_END || HP != LAST_HP || attackSpeedMod != LAST_attackSpeedMod || knockbackMod != LAST_knockbackMod || magicResistMod != LAST_magicResistMod || fireResistMod != LAST_fireResistMod || iceResistMod != LAST_iceResistMod || poisonResistMod != LAST_poisonResistMod || lightningResistMod != LAST_lightningResistMod || slashingResistMod != LAST_slashingResistMod || piercingResistMod != LAST_piercingResistMod || bludgeoningResistMod != LAST_bludgeoningResistMod || iceDamageMod != LAST_iceDamageMod || frostResistMod != LAST_frostResistMod || thunderResistMod != LAST_thunderResistMod );
	}
}
