package com.meteoritegames.duel.commands;

import com.meteoritegames.duel.Main;
import com.meteoritegames.duel.objects.Duel;
import com.meteoritegames.duel.objects.DuelMap;
import com.meteoritepvp.api.command.Command;
import com.meteoritepvp.api.command.CommandClass;
import com.meteoritepvp.api.inventory.MeteoriteInventory;
import com.meteoritepvp.api.inventory.presets.BasicInventory;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Optional;

public class SetSpawnCommand implements CommandClass {
	private final Main plugin;

	public SetSpawnCommand(Main plugin) {
		this.plugin = plugin;
	}

	@Command(name="duel",
			args="setspawn",
			description="Set an arena's spawnpoint",
			params="@map @spawnNumber")
	public void setSpawn(Player p, String[] params) {
		if (p.isOp()) {
			String config = "maps." + params[0];

			if (plugin.getConfig().get(config + ".name") == null) {
				p.sendMessage(plugin.getText("map-invalid"));
				return;
			}

			plugin.getConfig().set(config + "." + params[1], p.getLocation());
			plugin.saveConfig();
			plugin.initMaps();

			p.sendMessage(plugin.getText("spawn-updated"));
		} else {
			p.sendMessage(plugin.getText("no-permission"));
		}
	}

}
