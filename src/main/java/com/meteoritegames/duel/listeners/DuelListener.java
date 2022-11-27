package com.meteoritegames.duel.listeners;

import com.meteoritegames.duel.Main;
import com.meteoritegames.duel.objects.Duel;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DuelListener implements Listener {
	private final Main plugin;

	public DuelListener(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onHealEvent(EntityRegainHealthEvent e) {
		if (!e.getEntityType().equals(EntityType.PLAYER)) return;

		Duel d = plugin.playerIsInDuel((Player)e.getEntity());
		if (d == null) return;

		if (d.isActive()) {
			if (d.getDuelArgs().get(3).isEnabled()) e.setCancelled(true);
		}
	}

	@EventHandler
	public void onFoodChangeEvent(FoodLevelChangeEvent e) {
		if (!e.getEntityType().equals(EntityType.PLAYER)) return;

		Duel d = plugin.playerIsInDuel((Player)e.getEntity());
		if (d == null) return;

		if (d.isActive()) {
			if (d.getDuelArgs().get(4).isEnabled()) e.setCancelled(true);
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

}
