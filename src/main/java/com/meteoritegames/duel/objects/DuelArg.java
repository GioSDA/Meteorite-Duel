package com.meteoritegames.duel.objects;

import org.bukkit.Material;

public class DuelArg {
	private Material material;
	private String name;
	private boolean enabled;

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
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

	public DuelArg(Material material, String name, boolean enabled) {
		this.material = material;
		this.name = name;
		this.enabled = enabled;
	}

}
