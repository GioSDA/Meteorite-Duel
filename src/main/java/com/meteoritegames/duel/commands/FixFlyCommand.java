package com.meteoritegames.duel.commands;

import com.meteoritegames.duel.Main;
import com.meteoritegames.duel.objects.Duel;
import com.meteoritepvp.api.command.Command;
import com.meteoritepvp.api.command.CommandClass;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FixFlyCommand implements CommandClass {
	private final Main plugin;

	public FixFlyCommand(Main plugin) {
		this.plugin = plugin;
	}

	@Command(name="fix",
			description="Fix items")
	public void fix(CommandSender sender) {
		if (!plugin.commandAllowed.get("fix")) return;

		if (!(sender instanceof Player)) return;
		Player p = (Player) sender;

		Duel d = plugin.playerIsInDuel(p);
		if (d != null) {
			if (!d.isArgEnabled("/fix")) {
				p.sendMessage(plugin.getText("not-enabled").replace("%command%", "/fix"));
				return;
			}
		}

		ItemStack item = p.getInventory().getItemInHand();
		item.setDurability((short) 0);
		p.getInventory().setItemInHand(item);
		p.playSound(p.getLocation(), Sound.ANVIL_USE, 1, 1);
		p.updateInventory();
	}

	@Command(name="fix",
			description="Fix all items",
			args="all")
	public void fixAll(CommandSender sender) {
		if (!plugin.commandAllowed.get("fixAll")) return;

		if (!(sender instanceof Player)) return;
		Player p = (Player) sender;

		Duel d = plugin.playerIsInDuel(p);
		if (d != null) {
			if (!d.isArgEnabled("/fix all")) {
				p.sendMessage(plugin.getText("not-enabled").replace("%command%", "/fix all"));
				return;
			}
		}

		for (int i = 0; i < p.getInventory().getSize(); i++) {
			ItemStack item = p.getInventory().getItem(i);
			if (item == null) continue;
			item.setDurability((short) 0);
			p.getInventory().setItem(i, item);
		}

		p.playSound(p.getLocation(), Sound.ANVIL_USE, 1, 1);
		p.updateInventory();
	}

	@Command(name="fly",
			description="Fly")
	public void fly(CommandSender sender) {
		if (!plugin.commandAllowed.get("fly")) return;

		if (!(sender instanceof Player)) return;
		Player p = (Player) sender;

		Duel d = plugin.playerIsInDuel(p);
		if (d != null) {
			if (!d.isArgEnabled("/fly")) {
				p.sendMessage(plugin.getText("not-enabled").replace("%command%", "/fly"));
				return;
			}
		}

		p.setAllowFlight(!p.getAllowFlight());
	}
}
