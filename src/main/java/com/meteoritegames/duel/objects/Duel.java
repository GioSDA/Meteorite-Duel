package com.meteoritegames.duel.objects;

import com.meteoritegames.duel.Main;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

public class Duel {
	private ArrayList<DuelArg> duelArgs = new ArrayList<>();

	private Player dueler1;
	private Player dueler2;
	private DuelMap map;
	private boolean active;
	private boolean accepted1;
	private boolean accepted2;
	private ArrayList<ItemStack> wager1;
	private ArrayList<ItemStack> wager2;
	private Inventory inventory1;
	private Inventory inventory2;

	private static final Set<Material> ARMORTYPES = EnumSet.of(
			Material.LEATHER_HELMET,
			Material.LEATHER_CHESTPLATE,
			Material.LEATHER_LEGGINGS,
			Material.LEATHER_BOOTS,
			Material.CHAINMAIL_HELMET,
			Material.CHAINMAIL_CHESTPLATE,
			Material.CHAINMAIL_LEGGINGS,
			Material.CHAINMAIL_BOOTS,
			Material.GOLD_HELMET,
			Material.GOLD_CHESTPLATE,
			Material.GOLD_LEGGINGS,
			Material.GOLD_BOOTS,
			Material.IRON_HELMET,
			Material.IRON_CHESTPLATE,
			Material.IRON_LEGGINGS,
			Material.IRON_BOOTS,
			Material.DIAMOND_HELMET,
			Material.DIAMOND_CHESTPLATE,
			Material.DIAMOND_LEGGINGS,
			Material.DIAMOND_BOOTS
	);

	private static final Set<Material> SWORDTYPES = EnumSet.of(
			Material.DIAMOND_SWORD,
			Material.GOLD_SWORD,
			Material.STONE_SWORD,
			Material.WOOD_SWORD,
			Material.IRON_SWORD
	);

	public Duel(Player dueler1, Player dueler2) {
		this.dueler1 = dueler1;
		this.dueler2 = dueler2;
		this.active = false;
		this.wager1 = new ArrayList<>();
		this.wager2 = new ArrayList<>();
		this.accepted1 = false;
		this.accepted2 = false;

		duelArgs.add(new DuelArg(Material.GOLDEN_APPLE, "Golden Apples", true));
//		duelArgs.add(new DuelArg(Material.DIAMOND_AXE, "MCMMO", true));
		duelArgs.add(new DuelArg(Material.BREWING_STAND_ITEM, "Potions", true));
		duelArgs.add(new DuelArg(Material.BOW, "Bows", true));
		duelArgs.add(new DuelArg(Material.GHAST_TEAR, "Healing", true));
		duelArgs.add(new DuelArg(Material.COOKED_BEEF, "Food Loss", true));
		duelArgs.add(new DuelArg(Material.ENDER_PEARL, "Ender Pearls", true));
		duelArgs.add(new DuelArg(Material.BONE, "Risk Inventory", false));
		duelArgs.add(new DuelArg(Material.DIAMOND_CHESTPLATE, "Armor", true));
		duelArgs.add(new DuelArg(Material.DIAMOND_SWORD, "Weapons", true));
		duelArgs.add(new DuelArg(Material.ANVIL, "/fix", true));
		duelArgs.add(new DuelArg(Material.ANVIL, "/fix all", true));
		duelArgs.add(new DuelArg(Material.FEATHER, "/fly", false));
		duelArgs.add(new DuelArg(Material.PAPER, "Death Certificates", true));
		duelArgs.add(new DuelArg(Material.MONSTER_EGG, "Inventory Pets", false));
	}


	public Player getDueler1() {
		return dueler1;
	}

	public void setDueler1(Player dueler) {
		this.dueler1 = dueler;
	}

	public Player getDueler2() {
		return dueler2;
	}

	public void setDueler2(Player dueler2) {
		this.dueler2 = dueler2;
	}

	public ArrayList<DuelArg> getDuelArgs() {
		return duelArgs;
	}

	public DuelMap getMap() {
		return map;
	}

	public void setMap(DuelMap map) {
		this.map = map;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	public ArrayList<ItemStack> getWager1() {
		return wager1;
	}

	public void setWager1(ArrayList<ItemStack> wager1) {
		this.wager1 = wager1;
	}

	public ArrayList<ItemStack> getWager2() {
		return wager2;
	}

	public void setWager2(ArrayList<ItemStack> wager2) {
		this.wager2 = wager2;
	}


	public boolean isAccepted1() {
		return accepted1;
	}

	public void setAccepted1(boolean accepted1) {
		this.accepted1 = accepted1;
	}

	public boolean isAccepted2() {
		return accepted2;
	}

	public void setAccepted2(boolean accepted2) {
		this.accepted2 = accepted2;
	}


	public Inventory getInventory1() {
		return inventory1;
	}

	public void setInventory1(Inventory inventory1) {
		this.inventory1 = inventory1;
	}

	public Inventory getInventory2() {
		return inventory2;
	}

	public void setInventory2(Inventory inventory2) {
		this.inventory2 = inventory2;
	}

	public void startCountdown() {
		BukkitTask task = new BukkitRunnable() {
			int count = 3;

			final Player p1 = dueler1;
			final Player p2 = dueler2;

			@Override
			public void run() {
				p1.closeInventory();
				p2.closeInventory();
				if(count == 3) {
					p1.sendTitle("§a3", "");
					p1.playNote(p1.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.A));
					p2.sendTitle("§a3", "");
					p2.playNote(p2.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.A));
				} else if(count == 2) {
					if (!accepted1 || !accepted2 || !dueler1.isOnline() || !dueler2.isOnline()) this.cancel();
					p1.sendTitle("§62", "");
					p1.playNote(p1.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.A));
					p2.sendTitle("§62", "");
					p2.playNote(p2.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.A));
				} else if(count == 1) {
					if (!accepted1 || !accepted2 || !dueler1.isOnline() || !dueler2.isOnline()) this.cancel();
					p1.sendTitle("§c1", "");
					p1.playNote(p1.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.A));
					p2.sendTitle("§c1", "");
					p2.playNote(p2.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.A));
				} else if (count <= 0) {
					if (!accepted1 || !accepted2 || !dueler1.isOnline() || !dueler2.isOnline()) startDuel();
					this.cancel();
				}
				count--;
			}
		}.runTaskTimer(Main.plugin, 0L, 30);
	}

	public void startDuel() {
		this.active = true;

		this.inventory1 = dueler1.getInventory();
		this.inventory2 = dueler2.getInventory();

		if (!this.getDuelArgs().get(0).isEnabled()) { //Golden Apples
			dueler1.getInventory().remove(Material.GOLDEN_APPLE);
			dueler2.getInventory().remove(Material.GOLDEN_APPLE);
		}

		if (!this.getDuelArgs().get(1).isEnabled()) { //Potions
			dueler1.getInventory().remove(Material.POTION);
			dueler2.getInventory().remove(Material.POTION);
		}

		if (!this.getDuelArgs().get(2).isEnabled()) { //Bows
			dueler1.getInventory().remove(Material.BOW);
			dueler2.getInventory().remove(Material.BOW);
		}

		if (!this.getDuelArgs().get(5).isEnabled()) { //Ender Pearls
			dueler1.getInventory().remove(Material.ENDER_PEARL);
			dueler2.getInventory().remove(Material.ENDER_PEARL);
		}

		if (!this.getDuelArgs().get(7).isEnabled()) { //Armor
			ARMORTYPES.forEach(e -> {
				dueler1.getInventory().remove(e);
				dueler2.getInventory().remove(e);
			});
		}

		if (!this.getDuelArgs().get(8).isEnabled()) { //Swords
			SWORDTYPES.forEach(e -> {
				dueler1.getInventory().remove(e);
				dueler2.getInventory().remove(e);
			});
		}

		if (this.getDuelArgs().get(12).isEnabled()) { //Flight
			dueler1.setAllowFlight(true);
			dueler2.setAllowFlight(true);
		}

		if (!this.getDuelArgs().get(13).isEnabled()) { //Pets
			dueler1.getInventory().remove(Material.SKULL_ITEM);
		}

		dueler1.setHealth(20.0);
		dueler2.setHealth(20.0);

		dueler1.setFoodLevel(20);
		dueler2.setFoodLevel(20);

		dueler1.teleport(new Location(dueler1.getServer().getWorlds().get(0), this.getMap().getX1(), this.getMap().getY1(), this.getMap().getZ1()));
		dueler2.teleport(new Location(dueler2.getServer().getWorlds().get(0), this.getMap().getX2(), this.getMap().getY2(), this.getMap().getZ2()));
		dueler1.playNote(dueler1.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.F));
		dueler2.playNote(dueler2.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.F));

	}

	public void endDuel(Player loser) {
		dueler1.setAllowFlight(false);
		dueler2.setAllowFlight(false);

		Player winner;
		if (loser.equals(dueler1)) winner = dueler2;
		else winner = dueler1;

		ArrayList<ItemStack> rewards = new ArrayList<>();
		rewards.addAll(getWager1());
		rewards.addAll(getWager2());

		if (duelArgs.get(6).isEnabled()) {
			for (int i = 0; i < loser.getInventory().getSize(); i++) {
				rewards.add(loser.getInventory().getItem(i));
				loser.getInventory().setItem(i, new ItemStack(Material.AIR));
			}
		}

		if (duelArgs.get(12).isEnabled()) {
			ItemStack cert = new ItemStack(Material.PAPER);
			ItemMeta meta = cert.getItemMeta();
			meta.setDisplayName("§6" + loser.getName() + " §ewas defeated by §6" + winner.getName());
			cert.setItemMeta(meta);
			rewards.add(cert);
		}

		winner.sendMessage("§eYou have won the duel! use §6/duel collect §eto claim your winnings.");
		Main.addDuelRewards(winner, rewards);
		Main.removeDuel(this);
	}
}
