package com.meteoritegames.duel.objects;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Kit {
	String name;
	Material symbol;
	ItemStack[] items;

	public Kit(String name, Material symbol, ItemStack[] items) {
		this.name = name;
		this.symbol = symbol;
		this.items = items;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Material getSymbol() {
		return symbol;
	}

	public void setSymbol(Material symbol) {
		this.symbol = symbol;
	}

	public ItemStack[] getItems() {
		return items;
	}

	public void setItems(ItemStack[] items) {
		this.items = items;
	}

}
