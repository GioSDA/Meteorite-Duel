package com.meteoritegames.duel;

import com.meteoritegames.duel.commands.Duel;
import com.meteoritegames.duel.objects.DuelMap;
import com.meteoritepvp.api.MeteoritePlugin;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Main extends MeteoritePlugin {
	public static Main plugin;

	private static ArrayList<DuelMap> maps = new ArrayList<>();

	@Override
	protected void onInit() {
		super.onInit();
		plugin = this;

		try {
			initMaps();
			print("Duel plugin enabled.");

			registerCommandClass(Duel.class);
		} catch (Exception e) {
			print("Error enabling duel maps! Make sure your icons are correct?");
			e.printStackTrace();
		}
	}

	private void initMaps() throws IllegalArgumentException {
		ConfigurationSection maps1 = this.getConfig().getConfigurationSection("maps");
		Set<String> keys = maps1.getKeys(false);

		for (String key : keys) {
			List<String> o = maps1.getStringList(key);
			maps.add(new DuelMap(o.get(0), Material.valueOf(o.get(1)),Integer.parseInt(o.get(2)),Integer.parseInt(o.get(3))));
		}
	}

	public static ArrayList<DuelMap> getMaps() {
		return maps;
	}
}
