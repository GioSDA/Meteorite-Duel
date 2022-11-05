package com.meteoritegames.duel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meteoritegames.duel.commands.DuelCommand;
import com.meteoritegames.duel.commands.FixFlyCommand;
import com.meteoritegames.duel.objects.DuelMap;
import com.meteoritegames.duel.objects.Duel;
import com.meteoritepvp.api.MeteoritePlugin;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class Main extends MeteoritePlugin {
	public static Main plugin;
	public static HashMap<Player, ArrayList<ItemStack>> duelRewards = new HashMap<>();
	public static Set<Player> noDuel = new HashSet<>();
	public static ArrayList<Duel> duels = new ArrayList<>();
	private static ArrayList<DuelMap> maps = new ArrayList<>();

	@Override
	protected void onInit() {
		super.onInit();
		plugin = this;

		try {
			saveDefaultConfig();

			initToggle();
			initMaps();
			print("Duel plugin enabled.");

			registerCommandClass(DuelCommand.class);
			registerCommandClass(FixFlyCommand.class);
		} catch (Exception e) {
			print("Error enabling duel maps! Make sure your icons are correct?");
			e.printStackTrace();
		}
	}

	private void initToggle() throws IOException {
		Gson gson = new Gson();
		File file = new File(this.getDataFolder().getAbsolutePath() + "/toggles.json");

		if (file.exists()){
			Reader reader = new FileReader(file);
			Type setType = new TypeToken<HashSet<String>>(){}.getType();
			noDuel = gson.fromJson(reader, setType);
		}
	}

	private void saveToggle() throws IOException {
		Gson gson = new Gson();
		File file = new File(this.getDataFolder().getAbsolutePath() + "/toggles.json");
		file.getParentFile().mkdir();
		file.createNewFile();
		Writer writer = new FileWriter(file, false);
		gson.toJson(noDuel, writer);
		writer.flush();
		writer.close();
		System.out.println("Saved duel toggles.");
	}

	private void initMaps() throws IllegalArgumentException {
		for (String key : getConfig().getConfigurationSection("maps").getKeys(false)) {
			String mapkey = "maps." + key + ".";

			String name = getConfig().getString(mapkey + "name");
			Material material = Material.valueOf(getConfig().getString(mapkey + "icon").toUpperCase(Locale.ROOT));
			double x1 = getConfig().getDouble(mapkey + "spawn1x");
			double y1 = getConfig().getDouble(mapkey + "spawn1y");
			double z1 = getConfig().getDouble(mapkey + "spawn1z");
			double x2 = getConfig().getDouble(mapkey + "spawn2x");
			double y2 = getConfig().getDouble(mapkey + "spawn2y");
			double z2 = getConfig().getDouble(mapkey + "spawn2z");

			maps.add(new DuelMap(name, material, x1, y1, z1, x2, y2, z2));
		}
	}

	public static ArrayList<DuelMap> getMaps() {
		return maps;
	}

	public static boolean mapIsActive(DuelMap map) {
		for (Duel d : duels) {
			if (d.getMap().equals(map)) {
				return true;
			}
		}

		return false;
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

	public static Duel playerIsInDuel(Player p) {
		for (Duel duel : duels) {
			if ((duel.getDueler1().equals(p) && duel.isActive()) || (duel.getDueler2().equals(p) && duel.isActive())) return duel;
		}

		return null;
	}

	public static void removeDuel(Duel d) {
		duels.remove(d);
	}

	public static void addDuelRewards(Player p, ArrayList<ItemStack> rewards) {
		duelRewards.put(p, rewards);
	}
}
