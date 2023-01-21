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
			description="Fix items ONLY IN DUEL")
	public void fix(CommandSender sender) {
		if (!(sender instanceof Player)) return;
		Player p = (Player) sender;

		Duel d = plugin.playerIsInDuel(p);
		if (d == null) {
			p.sendMessage("§cYou are not in a duel!");
			return;
		}

		if (d.getDuelArgs().get(9).isEnabled()) {
			ItemStack item = p.getInventory().getItemInHand();
			item.setDurability((short) 0);
			p.getInventory().setItemInHand(item);
			p.playSound(p.getLocation(), Sound.ANVIL_USE, 1, 1);
			p.updateInventory();
		} else {
			p.sendMessage("§c/fix is not enabled!");
		}
	}

	@Command(name="fix",
			description="Fix all items ONLY IN DUEL",
			args="all")
	public void fixAll(CommandSender sender) {
		if (!(sender instanceof Player)) return;
		Player p = (Player) sender;

		Duel d = plugin.playerIsInDuel(p);
		if (d == null) {
			p.sendMessage("§cYou are not in a duel!");
			return;
		}

		if (d.getDuelArgs().get(10).isEnabled()) {
			for (int i = 0; i < p.getInventory().getSize(); i++) {
				ItemStack item = p.getInventory().getItem(i);
				if (item == null) continue;
				item.setDurability((short) 0);
				p.getInventory().setItem(i, item);
			}

			p.playSound(p.getLocation(), Sound.ANVIL_USE, 1, 1);
			p.updateInventory();
		} else {
			p.sendMessage("§c/fix all is not enabled!");
		}
	}

	@Command(name="fly",
			description="Fly ONLY IN DUEL")
	public void fly(CommandSender sender) {
		if (!(sender instanceof Player)) return;
		Player p = (Player) sender;

		Duel d = plugin.playerIsInDuel(p);
		if (d == null) {
			p.sendMessage("§cYou are not in a duel!");
			return;
		}

		if (d.getDuelArgs().get(11).isEnabled()) {
			p.setAllowFlight(!p.getAllowFlight());
		} else {
			p.sendMessage("§c/fly is not enabled!");
		}
	}
}
