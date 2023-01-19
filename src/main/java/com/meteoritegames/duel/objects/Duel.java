package com.meteoritegames.duel.objects;

import com.meteoritegames.duel.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

public class Duel {
	private final Main plugin;
	private ArrayList<DuelArg> duelArgs = new ArrayList<>();
	BukkitTask duelTask;

	private Player dueler1;
	private Player dueler2;
	private Location start1;
	private Location start2;
	private DuelMap map;
	private boolean active;
	private boolean accepted1;
	private boolean accepted2;
	private ArrayList<ItemStack> wager1;
	private ArrayList<ItemStack> wager2;
	private PlayerInventory inventory1;
	private PlayerInventory inventory2;
	private int timer = 0;
	private int hitClock = 0;
	private boolean firstHit = false;

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

	public Duel(Main plugin, Player dueler1, Player dueler2) {
		this.plugin = plugin;
		this.dueler1 = dueler1;
		this.dueler2 = dueler2;
		this.active = false;
		this.wager1 = new ArrayList<>();
		this.wager2 = new ArrayList<>();
		this.accepted1 = false;
		this.accepted2 = false;
		this.start1 = dueler1.getLocation();
		this.start2 = dueler2.getLocation();

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

	public void setId(int id) {

	}

	public PlayerInventory getInventory1() {
		return inventory1;
	}

	public void setInventory1(PlayerInventory inventory1) {
		this.inventory1 = inventory1;
	}

	public PlayerInventory getInventory2() {
		return inventory2;
	}

	public void setInventory2(PlayerInventory inventory2) {
		this.inventory2 = inventory2;
	}

	public void startDuel() {
		dueler1.setGameMode(GameMode.ADVENTURE);
		dueler2.setGameMode(GameMode.ADVENTURE);
		dueler1.setNoDamageTicks(140);
		dueler2.setNoDamageTicks(140);
		dueler1.closeInventory();
		dueler2.closeInventory();

		duelTask = new BukkitRunnable() {
			final Player p1 = dueler1;
			final Player p2 = dueler2;

			@Override
			public void run() {
				updateScoreboard(p1);
				updateScoreboard(p2);

				if (hitClock == 30 && !firstHit) {
					p1.sendMessage("§e§l[/duel] §eHit the opponent in 30 seconds otherwise it will be a draw.");
					p2.sendMessage("§e§l[/duel] §eHit the opponent in 30 seconds otherwise it will be a draw.");
				}
				if (hitClock >= 60 && !firstHit) endDuel(p1, true);
				if (hitClock == 120) {
					p1.sendMessage("§e§l[/duel] §eHit the opponent in 60 seconds otherwise it will be a draw.");
					p2.sendMessage("§e§l[/duel] §eHit the opponent in 60 seconds otherwise it will be a draw.");
				}
				if (hitClock >= 180) endDuel(p1, true);

				hitClock++;
				timer++;
			}
		}.runTaskTimer(plugin, 0L, 20);

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

		dueler1.teleport(this.getMap().getSpawn1());
		dueler2.teleport(this.getMap().getSpawn2());
		dueler1.playNote(dueler1.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.F));
		dueler2.playNote(dueler2.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.F));

		dueler1.sendTitle("§e§l[/duel]", "§b§l%player1% vs %player2%".replace("%player1%", dueler1.getName()).replace("%player2%", dueler2.getName()));
		dueler2.sendTitle("§e§l[/duel]", "§b§l%player1% vs %player2%".replace("%player1%", dueler2.getName()).replace("%player2%", dueler1.getName()));
		dueler1.sendMessage("§b§m§l====================§6§m§l=====================§r\n" +
				"\n§r" +
				"           §8[ §6§lDUEL STARTED - FIGHT! §8]\n§r" +
				"                      §b§l%player1%\n§r".replace("%player1%", dueler1.getName()) +
				"                 §c§l§m---- VS §c§l§m----\n§r" +
				"                      §b§l%player2%\n".replace("%player2%", dueler2.getName()) +
				"\n" +
				"§b§m§l====================§6§m§l=====================§r");
		dueler2.sendMessage("§b§m§l====================§6§m§l=====================§r\n" +
				"\n§r" +
				"           §8[ §6§lDUEL STARTED - FIGHT! §8]\n§r" +
				"                      §b§l%player1%\n§r".replace("%player1%", dueler2.getName()) +
				"                 §c§l§m---- VS §c§l§m----\n§r" +
				"                      §b§l%player2%\n".replace("%player2%", dueler1.getName()) +
				"\n" +
				"§b§m§l====================§6§m§l=====================§r");
	}

	public void endDuel(Player loser, boolean stalemate) {
		dueler1.setGameMode(GameMode.SURVIVAL);
		dueler2.setGameMode(GameMode.SURVIVAL);
		dueler1.setAllowFlight(false);
		dueler2.setAllowFlight(false);
		
		Player winner;
		if (loser.equals(dueler1)) winner = dueler2;
		else winner = dueler1;

		dueler1.getInventory().setContents(inventory1.getContents());
		dueler2.getInventory().setContents(inventory2.getContents());

		if (!stalemate) {
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
				meta.setDisplayName("§r§l%player%'s Death Certificate".replace("%player%", loser.getName()));
				List<String> lore = new ArrayList<>();
				lore.add("§f%player% §7was defeated in a §f1v1".replace("%player%", loser.getName()));

				SimpleDateFormat dateformat = new SimpleDateFormat("EEE MM/dd/yy");
				SimpleDateFormat timeformat = new SimpleDateFormat("hh:mmaa");
				Date now = Date.from(Instant.now());
				lore.add("§7duel on §f%date% at %time%".replace("%date%", dateformat.format(now)).replace("%time%", timeformat.format(now)));

				lore.add("§7in the %arena%§r§7 by §f%winner%".replace("%arena%", map.getName()).replace("%winner%", winner.getName()));
				meta.setLore(lore);
				cert.setItemMeta(meta);
				rewards.add(cert);
			}

			winner.sendMessage("§eYou have won the duel! use §6/duel collect §eto claim your winnings.");
			if (rewards.size() != 0) winner.sendTitle("§e§l[/duel]", "§aItem(s) in your '/duel collect' bin!");
			else winner.sendTitle("§e§l[/duel]", "§a%player% won this duel.".replace("%player%", winner.getName()));
			plugin.addDuelRewards(winner, rewards);
		} else {
			for (ItemStack item : wager1) winner.getInventory().addItem(item);
			for (ItemStack item : wager2) loser.getInventory().addItem(item);

			loser.sendTitle("§e§l[/duel]", "§7This duel has no winner (draw).");
			winner.sendTitle("§e§l[/duel]", "§7This duel has no winner (draw).");

			loser.sendMessage("§eDuel cancelled.");
			winner.sendMessage("§eDuel cancelled.");
		}

		dueler1.teleport(start1);
		dueler2.teleport(start2);

		duelTask.cancel();

		dueler1.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		dueler2.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

		plugin.removeDuel(this);
	}

	private void updateScoreboard(Player p) {
		ScoreboardManager m = Bukkit.getScoreboardManager();
		Scoreboard b = m.getNewScoreboard();

		Objective o = b.registerNewObjective("duel", "dummy");
		o.setDisplayName("§6§l1v1 Duel §r§7" + LocalDate.now().getMonthValue() + "/" + LocalDate.now().getDayOfMonth());
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		Score s = o.getScore("§c§lOpponent");
		s.setScore(10);
		Score s1 = o.getScore("§f" + (dueler1.equals(p) ? dueler2.getName() : dueler1.getName()));
		s1.setScore(9);
		Score s2 = o.getScore("§r");
		s2.setScore(8);
		Score s6 = o.getScore("§6§lRuntime");
		s6.setScore(4);
		Score s7 = o.getScore("§e" + timer + "s");
		s7.setScore(3);
		Score s8 = o.getScore("§r§r§r§r");
		s8.setScore(2);
		Score s3 = o.getScore("§6§lArena");
		s3.setScore(1);
		Score s4 = o.getScore("§e" + map.getName());
		s4.setScore(0);
		Score s9 = o.getScore("§r§r§r§r§r§r");
		s9.setScore(-1);
		Score s10 = o.getScore("§6§lAccount");
		s10.setScore(-2);
		Score s11 = o.getScore("§e" + p.getName());
		s11.setScore(-3);

		p.setScoreboard(b);
	}

	public void registerHit() {
		if (!firstHit) firstHit = true;
		hitClock = 0;
	}

	public int getTimer() {
		return timer;
	}
}
