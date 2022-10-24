package com.meteoritegames.duel;

import com.meteoritegames.duel.commands.DuelCommand;
import com.meteoritegames.duel.objects.DuelMap;
import com.meteoritegames.duel.objects.Duel;
import com.meteoritepvp.api.MeteoritePlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Locale;

public class Main extends MeteoritePlugin {
	public static Main plugin;

	public static ArrayList<Duel> duels = new ArrayList<>();
	private static ArrayList<DuelMap> maps = new ArrayList<>();

	@Override
	protected void onInit() {
		super.onInit();
		plugin = this;

		try {
			saveDefaultConfig();

			initMaps();
			print("Duel plugin enabled.");

			registerCommandClass(DuelCommand.class);
		} catch (Exception e) {
			print("Error enabling duel maps! Make sure your icons are correct?");
			e.printStackTrace();
		}
	}

	public static void mapActive(int index, boolean active) {
		maps.get(index).setActive(active);
	}

	private void initMaps() throws IllegalArgumentException {
		for (String key : getConfig().getConfigurationSection("maps").getKeys(false)) {
			String mapkey = "maps." + key + ".";

			String name = getConfig().getString(mapkey + "name");
			Material material = Material.valueOf(getConfig().getString(mapkey + "icon").toUpperCase(Locale.ROOT));
			double x = getConfig().getDouble(mapkey + "xpos");
			double y = getConfig().getDouble(mapkey + "ypos");
			double z = getConfig().getDouble(mapkey + "zpos");

			maps.add(new DuelMap(name, material, x, y, z, true));
		}
	}

	public static ArrayList<DuelMap> getMaps() {
		return maps;
	}

	public static void addDuel(Duel d) {
		duels.add(d);
	}

	public static Duel getDuel(Player p) {
		for (Duel duel : duels) {
			if (duel.getDueler1().equals(p)) return duel;
		}

		return null;
	}

	public static boolean playerIsInDuel(Player p) {
		for (Duel duel : duels) {
			if ((duel.getDueler1().equals(p) && duel.isActive()) || (duel.getDueler2().equals(p) && duel.isActive())) return true;
		}

		return false;
	}

	public static void removeDuel(Duel d) {
		duels.remove(d);
	}
}
