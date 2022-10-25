package com.meteoritegames.duel.objects;

import org.bukkit.Material;

public class DuelMap {
	public DuelMap(String name, Material icon, double x1, double y1, double z1, double x2, double y2, double z2, boolean active) {
		this.name = name;
		this.icon = icon;
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.active = active;
	}

	private String name;
	private Material icon;
	private double x1;
	private double y1;
	private double z1;
	private double x2;
	private double y2;
	private double z2;
	private boolean active;

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

	public double getX1() {
		return x1;
	}

	public void setX1(double x) {
		this.x1 = x;
	}

	public double getY1() {
		return y1;
	}

	public void setY1(double y) {
		this.y1 = y;
	}

	public double getZ1() {
		return z1;
	}

	public void setZ1(double z) {
		this.z1 = z;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public double getX2() {
		return x2;
	}

	public void setX2(double x2) {
		this.x2 = x2;
	}

	public double getY2() {
		return y2;
	}

	public void setY2(double y2) {
		this.y2 = y2;
	}

	public double getZ2() {
		return z2;
	}

	public void setZ2(double z2) {
		this.z2 = z2;
	}
}
