package com.meteoritegames.duel.commands;

import com.meteoritegames.duel.Main;
import com.meteoritepvp.api.command.Command;
import com.meteoritepvp.api.command.CommandClass;
import org.bukkit.entity.Player;

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
