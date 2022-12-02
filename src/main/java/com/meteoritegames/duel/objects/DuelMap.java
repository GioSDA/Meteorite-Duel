package com.meteoritegames.duel.objects;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

public class DuelMap {
	private String name;
	private Material icon;
	private Location spawn1;
	private Location spawn2;

	public DuelMap(String name, Material icon, Location spawn1, Location spawn2) {
		this.name = ChatColor.translateAlternateColorCodes('&', name);
		this.icon = icon;
		this.spawn1 = spawn1;
		this.spawn2 = spawn2;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Material getIcon() {
		return icon;
	}

	public void setIcon(Material icon) {
		this.icon = icon;
	}

	public Location getSpawn1() {
		return spawn1;
	}

	public void setSpawn1(Location spawn1) {
		this.spawn1 = spawn1;
	}

	public Location getSpawn2() {
		return spawn2;
	}

	public void setSpawn2(Location spawn2) {
		this.spawn2 = spawn2;
	}
}
