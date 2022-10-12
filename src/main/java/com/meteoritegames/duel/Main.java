package com.meteoritegames.duel;

import com.meteoritegames.duel.commands.Duel;
import com.meteoritegames.duel.objects.DuelMap;
import com.meteoritepvp.api.MeteoritePlugin;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
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
		print(getConfig().getConfigurationSection("maps").getKeys(false));
		for (String key : getConfig().getConfigurationSection("maps").getKeys(false)) {
			print(key);
			List<String> o = getConfig().getStringList("maps." + key);
			print(getConfig().getStringList("maps.default"));
			//this is the line that isn't working

			maps.add(new DuelMap(o.get(0), Material.valueOf(o.get(1).toUpperCase(Locale.ROOT)),Integer.parseInt(o.get(2)),Integer.parseInt(o.get(3)), Integer.parseInt(o.get(4))));
		}
	}

	public static ArrayList<DuelMap> getMaps() {
		return maps;
	}
}
