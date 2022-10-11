package com.meteoritegames.duel.objects;

import org.bukkit.Material;

public class DuelMap {
	public DuelMap(String name, Material icon, double x, double y) {
		this.name = name;
		this.icon = icon;
		this.x = x;
		this.y = y;
	}

	private String name;
	private Material icon;
	private double x;
	private double y;

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

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

}
