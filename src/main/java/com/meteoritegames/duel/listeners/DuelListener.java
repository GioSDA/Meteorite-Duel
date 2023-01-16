package com.meteoritegames.duel.listeners;

import com.meteoritegames.duel.Main;
import com.meteoritegames.duel.objects.Duel;
import org.bukkit.Instrument;
import org.bukkit.Note;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

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

	@EventHandler
	public void onPlayerHit(EntityDamageByEntityEvent e) {
		if (!e.getEntityType().equals(EntityType.PLAYER)) return;
		Duel d = plugin.playerIsInDuel((Player) e.getEntity());

		if (d == null) return;
		if (!d.isActive()) return;

		d.registerHit();
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if (e.getPlayer().getOpenInventory().getTitle().startsWith("§8")) return;

		switch (e.getInventory().getTitle()) {
			case "§8Duel Settings":
				e.getPlayer().sendMessage("§e§l(!) §eYou closed the §nSETTINGS§e selection.");
				e.getPlayer().sendMessage("§7The duel has been cancelled.");
				plugin.removeDuel(plugin.getDuel((Player) e.getPlayer()));
				break;
			case "§8Map Settings":
				e.getPlayer().sendMessage("§e§l(!) §eYou closed the §nARENA§e selection.");
				e.getPlayer().sendMessage("§7The duel has been cancelled.");
				plugin.removeDuel(plugin.getDuel((Player) e.getPlayer()));
				break;
			case "§8Duel Wager":
			case "§8Inventory view":
				if (!plugin.getDuel((Player) e.getPlayer()).isActive()) return;

				e.getPlayer().sendMessage("§e§l(!) §eYou closed the §nRISK INVENTORY§e selection.");
				e.getPlayer().sendMessage("§7The duel has been cancelled.");
				plugin.removeDuel(plugin.getDuel((Player) e.getPlayer()));
				break;
		}
	}

}
