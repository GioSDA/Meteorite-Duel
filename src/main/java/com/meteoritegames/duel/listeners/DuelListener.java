package com.meteoritegames.duel.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class DuelListener implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!event.getWhoClicked().getOpenInventory().getTitle().contains("Wagering")) return;

		Player player = (Player) event.getWhoClicked();
		event.setCancelled(true);
	}

}
