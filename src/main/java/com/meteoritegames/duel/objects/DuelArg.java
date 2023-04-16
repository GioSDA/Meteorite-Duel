package com.meteoritegames.duel.objects;

import org.bukkit.inventory.ItemStack;

public class DuelArg {
	private ItemStack icon;
	private String name;
	private boolean enabled;
	private int slot;

	public ItemStack getIcon() {
		return icon;
	}

	public void setIcon(ItemStack icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getSlot() {
		return slot;
	}

	public DuelArg(ItemStack icon, String name, boolean enabled, int slot) {
		this.icon = icon;
		this.name = name;
		this.enabled = enabled;
		this.slot = slot;
	}

}
