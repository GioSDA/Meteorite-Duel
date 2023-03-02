package com.meteoritegames.duel;

import com.meteoritegames.duel.commands.DuelCommand;
import com.meteoritegames.duel.commands.FixFlyCommand;
import com.meteoritegames.duel.listeners.DuelListener;
import com.meteoritegames.duel.objects.DuelMap;
import com.meteoritegames.duel.objects.Duel;
import com.meteoritepvp.api.MeteoritePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Main extends MeteoritePlugin {
	public HashMap<String, String> text = new HashMap<>();
	public HashMap<Player, ArrayList<ItemStack>> duelRewards = new HashMap<>();
	public HashMap<Player, Location> spectators = new HashMap<>();
	public Set<Player> noDuel = new HashSet<>();
	public Set<Duel> duels = new HashSet<>();
	private ArrayList<DuelMap> maps = new ArrayList<>();


	@Override
	protected void onInit() {
		super.onInit();

		try {
			saveDefaultConfig();

			initText();
			initMaps();
			print("Duel plugin enabled.");

			registerPlaceholderParameter("player", (sender -> getNames()));
			registerPlaceholderParameter("num", (sender -> getNumbers()));


			registerCommandObject(new DuelCommand(this));
			registerCommandObject(new FixFlyCommand(this));

			registerEventListener(new DuelListener(this));
		} catch (Exception e) {
			print("Error enabling duel maps! Make sure your icons are correct?");
			e.printStackTrace();
		}
	}

	public void initMaps() throws IllegalArgumentException {
		maps.clear();

		for (String key : getConfig().getConfigurationSection("maps").getKeys(false)) {
			String mapkey = "maps." + key + ".";

			String name = getConfig().getString(mapkey + "name");
			Material material = Material.valueOf(getConfig().getString(mapkey + "icon").toUpperCase(Locale.ROOT));
			Location spawn1 = (Location) getConfig().get(mapkey + "spawn1");
			Location spawn2 = (Location) getConfig().get(mapkey + "spawn2");
			int guiPos = getConfig().getInt(mapkey + "guiPos");

			maps.add(new DuelMap(name, material, spawn1, spawn2, guiPos));
		}
	}

	private void initText() {
		text.clear();

		for (String key : getConfig().getConfigurationSection("text").getKeys(false)) {
			if (key.equals("help") || key.equals("duel-info")) {
				StringBuilder sText = new StringBuilder();

				for (String line : (ArrayList<String>) getConfig().getList("text." + key)) {
					line = line.replaceAll("&", "§");
					sText.append(line).append("\n");
				}

				text.put(key, sText.toString());
			} else {
				text.put(key, getConfig().getString("text." + key).replaceAll("&", "§"));
			}
		}
	}

	public ArrayList<DuelMap> getMaps() {
		return maps;
	}

	public Duel mapIsActive(DuelMap map) {
		for (Duel d : duels) {
			if (d.getMap().equals(map)) {
				return d;
			}
		}

		return null;
	}

	public void addDuel(Duel d) {
		duels.add(d);
	}

	public Duel getDuel(Player p) {
		for (Duel duel : duels) {
			if (duel.getDueler1().equals(p) || duel.getDueler2().equals(p)) return duel;
		}

		return null;
	}

	public Duel playerIsInDuel(Player p) {
		for (Duel duel : duels) {
			if ((duel.getDueler1().equals(p) && duel.isActive()) || (duel.getDueler2().equals(p) && duel.isActive())) return duel;
		}

		return null;
	}

	public void removeDuel(Duel d) {
		duels.remove(d);
	}

	public void addDuelRewards(Player p, ArrayList<ItemStack> rewards) {
		duelRewards.put(p, rewards);
	}


	public List<String> getNames() {
		List<String> names = new ArrayList<>();

		Bukkit.getServer().getOnlinePlayers().forEach(e -> names.add(e.getName()));
		return names;
	}

	public List<String> getNumbers() {
		return Arrays.asList("1", "2");
	}

	public String getText(String id) {
		return text.getOrDefault(id, "TEXT COULD NOT BE LOADED");
	}

	public void reload() {
		text.clear();
		duelRewards.clear();
		spectators.clear();
		noDuel.clear();

		for (Duel d : duels) {
			d.duelTask.cancel();
		}

		duels.clear();
		maps.clear();

		initText();
		initMaps();
	}

	@Override
	protected void onRegisterMainCommand(String description) {}
}
