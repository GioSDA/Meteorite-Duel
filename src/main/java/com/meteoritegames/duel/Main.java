package com.meteoritegames.duel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meteoritegames.duel.commands.DuelCommand;
import com.meteoritegames.duel.commands.FixFlyCommand;
import com.meteoritegames.duel.listeners.DuelListener;
import com.meteoritegames.duel.objects.DuelMap;
import com.meteoritegames.duel.objects.Duel;
import com.meteoritepvp.api.MeteoritePlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class Main extends MeteoritePlugin {
	public HashMap<Player, ArrayList<ItemStack>> duelRewards = new HashMap<>();
	public HashMap<Player, Location> spectators = new HashMap<>();
	public Set<Player> noDuel = new HashSet<>();
	public ArrayList<Duel> duels = new ArrayList<>();
	private ArrayList<DuelMap> maps = new ArrayList<>();

	@Override
	protected void onInit() {
		super.onInit();

		try {
			saveDefaultConfig();

			initToggle();
			initMaps();
			print("Duel plugin enabled.");

			registerCommandObject(new DuelCommand(this));
			registerCommandObject(new FixFlyCommand(this));

			registerEventListener(new DuelListener(this));
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
			Location spawn1 = (Location) getConfig().get(mapkey + "spawn1");
			Location spawn2 = (Location) getConfig().get(mapkey + "spawn2");

			maps.add(new DuelMap(name, material, spawn1, spawn2));
		}
	}

	public ArrayList<DuelMap> getMaps() {
		return maps;
	}

	public boolean mapIsActive(DuelMap map) {
		for (Duel d : duels) {
			if (d.getMap().equals(map)) {
				return true;
			}
		}

		return false;
	}

	public void addDuel(Duel d) {
		duels.add(d);
	}

	public Duel getDuel(Player p) {
		for (Duel duel : duels) {
			if (duel.getDueler1().equals(p)) return duel;
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
}
