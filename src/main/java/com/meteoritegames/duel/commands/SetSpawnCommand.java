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

	@Command(name="setspawn",
			description="Set an arena's spawnpoint",
			params="@num")
	public void setSpawn(Player p, String[] params) {
		if (p.isOp()) {
			ArrayList<DuelMap> maps = plugin.getMaps();

			MeteoriteInventory inventory = new MeteoriteInventory(plugin, "§8Map Selection", 9, 1, true);
			BasicInventory page = new BasicInventory(9, 1);
			page.fill(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7));

			for (DuelMap map : plugin.getMaps()) {
				ItemStack item = new ItemStack(map.getIcon());
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(map.getName());

				item.setItemMeta(meta);

				page.setItem(map.getInvPos(), item);
			}

			page.setOnSlotClickListener(e -> {
				if (e.getEvent().getSlotType().equals(InventoryType.SlotType.OUTSIDE)) return;
				Optional<DuelMap> m = maps.stream().filter(n -> n.getInvPos() == e.getEvent().getRawSlot()).findAny();

				if (!m.isPresent()) {
					p.sendMessage("§cPlease choose a valid map!");
					return;
				}

				for (String key : plugin.getConfig().getConfigurationSection("maps").getKeys(false)) {
					String mapkey = "maps." + key + ".";

					if (plugin.getConfig().getInt(mapkey + "guiPos") == m.get().getInvPos()) {
						plugin.getConfig().set(mapkey + "spawn" + params[0], p.getLocation());
						plugin.saveConfig();
						plugin.initMaps();
					}
				}

				plugin.getConfig().get("maps");

				p.closeInventory();
				e.getEvent().getWhoClicked().sendMessage("§eSpawn position updated!");
			});

			inventory.applyPage(page);
			inventory.show(p);
		} else {
			p.sendMessage("§eYou do not have permission to use that command!");
		}
	}

}
