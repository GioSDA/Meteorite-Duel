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

public class DuelListener implements Listener {

	@EventHandler
	public void onHealEvent(EntityRegainHealthEvent e) {
		if (!e.getEntityType().equals(EntityType.PLAYER)) return;

		Duel d = Main.playerIsInDuel((Player)e.getEntity());
		if (d == null) return;

		if (d.isActive()) {
			if (d.getDuelArgs().get(3).isEnabled()) e.setCancelled(true);
		}
	}

	@EventHandler
	public void onFoodChangeEvent(FoodLevelChangeEvent e) {
		if (!e.getEntityType().equals(EntityType.PLAYER)) return;

		Duel d = Main.playerIsInDuel((Player)e.getEntity());
		if (d == null) return;

		if (d.isActive()) {
			if (d.getDuelArgs().get(4).isEnabled()) e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Duel d = Main.playerIsInDuel(e.getEntity());

		if (d == null) return;
		if (!d.isActive()) return;

		d.endDuel(e.getEntity());
	}

}
