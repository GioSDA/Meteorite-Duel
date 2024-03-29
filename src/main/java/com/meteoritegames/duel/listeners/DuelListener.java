package com.meteoritegames.duel.listeners;

import com.meteoritegames.duel.Main;
import com.meteoritegames.duel.objects.Duel;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;



public class DuelListener implements Listener {
	private final Main plugin;

	public DuelListener(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onHealEvent(EntityRegainHealthEvent e) {
		if (!e.getEntityType().equals(EntityType.PLAYER)) return;

		Duel d = plugin.playerIsInDuel((Player) e.getEntity());
		if (d == null) return;

		if (d.isActive()) {
			if (!d.isArgEnabled("Healing")) e.setCancelled(true);
		}
	}

	@EventHandler
	public void onFoodChangeEvent(FoodLevelChangeEvent e) {
		if (!e.getEntityType().equals(EntityType.PLAYER)) return;

		Duel d = plugin.playerIsInDuel((Player) e.getEntity());

		if (d == null) return;

		if (d.isActive()) {
			if (!d.isArgEnabled("Food Loss")) e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Duel d = plugin.playerIsInDuel(e.getEntity());

		if (d == null) return;
		if (!d.isActive()) return;

		e.setKeepInventory(true);
		d.endDuel(e.getEntity(), false);
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		Duel d = plugin.playerIsInDuel(e.getPlayer());

		if (d == null) return;
		if (!d.isActive()) return;

		d.endDuel(e.getPlayer(), false);
	}

	@EventHandler
	public void onPlayerHit(EntityDamageByEntityEvent e) {
		if (!e.getEntityType().equals(EntityType.PLAYER)) return;
		Duel d = plugin.playerIsInDuel((Player) e.getEntity());

		if (d == null) return;
		if (!d.isActive()) return;

		d.registerHit();
	}

	@EventHandler
	void onMeteoriteLevel(com.meteoritepvp.levels.event.MeteoriteLevelEvent e) {
		Duel d = plugin.playerIsInDuel(e.getPlayer());

		if (d == null) return;
		if (!d.isActive()) return;
		e.setCancelled(!d.isArgEnabled("Meteorite Energy"));
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			if (e.getPlayer().getOpenInventory().getTitle().startsWith("§8")) return;
			Duel d = plugin.getDuel((Player) e.getPlayer());

			if (d == null) return;

			String title = e.getInventory().getTitle();

			if (title.equals(plugin.getText("kit-menu")) || title.equals(plugin.getText("duel-menu"))) {
				e.getPlayer().sendMessage(plugin.getText("close-settings"));
				e.getPlayer().sendMessage(plugin.getText("cancel-duel"));
				plugin.removeDuel(d);
			} else if (title.equals(plugin.getText("map-menu"))) {
				if (plugin.getDuel((Player) e.getPlayer()) != null) return;

				e.getPlayer().sendMessage(plugin.getText("close-arena"));
				e.getPlayer().sendMessage(plugin.getText("cancel-duel"));
				plugin.removeDuel(d);
			} else if (title.equals(plugin.getText("wager-menu")) || title.equals(plugin.getText("inventory-menu"))) {
				if (d.isActive()) return;

				e.getPlayer().sendMessage(plugin.getText("close-wager"));
				d.getDueler1().sendMessage(plugin.getText("cancel-duel"));
				d.getDueler2().sendMessage(plugin.getText("cancel-duel"));

				d.getDueler1().closeInventory();
				d.getDueler2().closeInventory();

				for (ItemStack b : d.getWager1()) d.getDueler1().getInventory().addItem(b);
				for (ItemStack b : d.getWager2()) d.getDueler2().getInventory().addItem(b);
				plugin.removeDuel(d);
			}
		}, 1L);
	}
}
