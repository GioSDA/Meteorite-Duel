package com.meteoritegames.duel.objects;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Duel {
	private ArrayList<DuelArg> duelArgs = new ArrayList<>();

	private Player dueler1;
	private Player dueler2;
	private DuelMap map;
	private boolean active;
	private boolean accepted1;
	private boolean accepted2;
	private ArrayList<ItemStack> wager1;
	private ArrayList<ItemStack> wager2;
	private Inventory inventory1;
	private Inventory inventory2;

	public Duel(Player dueler1, Player dueler2) {
		this.dueler1 = dueler1;
		this.dueler2 = dueler2;
		this.active = false;
		this.wager1 = new ArrayList<>();
		this.wager2 = new ArrayList<>();
		this.accepted1 = false;
		this.accepted2 = false;

		duelArgs.add(new DuelArg(Material.GOLDEN_APPLE, "Golden Apples", true));
//		duelArgs.add(new DuelArg(Material.DIAMOND_AXE, "MCMMO", true));
		duelArgs.add(new DuelArg(Material.BREWING_STAND_ITEM, "Potions", true));
		duelArgs.add(new DuelArg(Material.BOW, "Bows", true));
		duelArgs.add(new DuelArg(Material.GHAST_TEAR, "Healing", true));
		duelArgs.add(new DuelArg(Material.COOKED_BEEF, "Food Loss", true));
		duelArgs.add(new DuelArg(Material.ENDER_PEARL, "Ender Pearls", true));
		duelArgs.add(new DuelArg(Material.BONE, "Risk Inventory", false));
		duelArgs.add(new DuelArg(Material.DIAMOND_CHESTPLATE, "Armor", true));
		duelArgs.add(new DuelArg(Material.DIAMOND_SWORD, "Weapons", true));
		duelArgs.add(new DuelArg(Material.ANVIL, "/fix", true));
		duelArgs.add(new DuelArg(Material.ANVIL, "/fix all", true));
		duelArgs.add(new DuelArg(Material.FEATHER, "/fly", false));
		duelArgs.add(new DuelArg(Material.PAPER, "Death Certificates", true));
		duelArgs.add(new DuelArg(Material.MONSTER_EGG, "Inventory Pets", false));
	}


	public Player getDueler1() {
		return dueler1;
	}

	public void setDueler1(Player dueler) {
		this.dueler1 = dueler;
	}

	public Player getDueler2() {
		return dueler2;
	}

	public void setDueler2(Player dueler2) {
		this.dueler2 = dueler2;
	}

	public ArrayList<DuelArg> getDuelArgs() {
		return duelArgs;
	}

	public DuelMap getMap() {
		return map;
	}

	public void setMap(DuelMap map) {
		this.map = map;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	public ArrayList<ItemStack> getWager1() {
		return wager1;
	}

	public void setWager1(ArrayList<ItemStack> wager1) {
		this.wager1 = wager1;
	}

	public ArrayList<ItemStack> getWager2() {
		return wager2;
	}

	public void setWager2(ArrayList<ItemStack> wager2) {
		this.wager2 = wager2;
	}


	public boolean isAccepted1() {
		return accepted1;
	}

	public void setAccepted1(boolean accepted1) {
		this.accepted1 = accepted1;
	}

	public boolean isAccepted2() {
		return accepted2;
	}

	public void setAccepted2(boolean accepted2) {
		this.accepted2 = accepted2;
	}


	public Inventory getInventory1() {
		return inventory1;
	}

	public void setInventory1(Inventory inventory1) {
		this.inventory1 = inventory1;
	}

	public Inventory getInventory2() {
		return inventory2;
	}

	public void setInventory2(Inventory inventory2) {
		this.inventory2 = inventory2;
	}


}
