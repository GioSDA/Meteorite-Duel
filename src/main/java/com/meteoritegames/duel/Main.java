package com.meteoritegames.duel;

import com.meteoritegames.duel.commands.Duel;
import com.meteoritegames.duel.objects.DuelMap;
import com.meteoritepvp.api.MeteoritePlugin;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Locale;

public class Main extends MeteoritePlugin {
	public static Main plugin;

	private static ArrayList<DuelMap> maps = new ArrayList<>();

	@Override
	protected void onInit() {
		super.onInit();
		plugin = this;

		try {
			saveDefaultConfig();

			initMaps();
			print("Duel plugin enabled.");

			registerCommandClass(Duel.class);
		} catch (Exception e) {
			print("Error enabling duel maps! Make sure your icons are correct?");
			e.printStackTrace();
		}
	}

	private void initMaps() throws IllegalArgumentException {
		for (String key : getConfig().getConfigurationSection("maps").getKeys(false)) {
			String mapkey = "maps." + key + ".";
			//this is the line that isn't working

			String name = getConfig().getString(mapkey + "name");
			print(name);
			Material material = Material.valueOf(getConfig().getString(mapkey + "icon").toUpperCase(Locale.ROOT));
			double x = getConfig().getDouble(mapkey + "xpos");
			double y = getConfig().getDouble(mapkey + "ypos");
			double z = getConfig().getDouble(mapkey + "zpos");

			maps.add(new DuelMap(name, material, x, y, z));
		}
	}

	public static ArrayList<DuelMap> getMaps() {
		return maps;
	}
}
