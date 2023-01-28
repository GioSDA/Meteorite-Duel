package com.meteoritegames.duel.objects;

import org.bukkit.inventory.ItemStack;

public class Kit {
	String name;
	ItemStack symbol;
	ItemStack[] items;

	public Kit(String name, ItemStack symbol, ItemStack[] items) {
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


	public ItemStack getSymbol() {
		return symbol;
	}

	public void setSymbol(ItemStack symbol) {
		this.symbol = symbol;
	}

	public ItemStack[] getItems() {
		return items;
	}

	public void setItems(ItemStack[] items) {
		this.items = items;
	}

}
