package com.meteoritegames.duel.objects;

import com.meteoritegames.duel.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
	public BukkitTask duelTask;

	private Player dueler1;
	private Player dueler2;
	private DuelMap map;
	private boolean active;
	private boolean accepted1;
	private boolean accepted2;
	private ArrayList<ItemStack> wager1;
	private ArrayList<ItemStack> wager2;
	private ArrayList<Kit> kits;
	private Kit kit;
	private ItemStack[] inventory1;
	private ItemStack[] inventory2;
	private ItemStack[] armor1;
	private ItemStack[] armor2;
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

		KitItems kitItemGen = new KitItems();
		kits = new ArrayList<>();

		kits.add(new Kit(plugin.getText("kit-none"), Material.DIAMOND_HELMET, new ItemStack[]{}));
		kits.add(new Kit(plugin.getText("kit-soup"), Material.MUSHROOM_SOUP, kitItemGen.getSoupItems()));
		kits.add(new Kit(plugin.getText("kit-potion"), Material.SPIDER_EYE, kitItemGen.getPotionItems()));
		kits.add(new Kit(plugin.getText("kit-nodebuff"), Material.BREWING_STAND_ITEM, kitItemGen.getNoDebuffItems()));

		kit = kits.get(0);
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

	public ItemStack[] getInventory1() {
		return inventory1;
	}

	public void setInventory1(ItemStack[] inventory1) {
		this.inventory1 = inventory1;
	}

	public ItemStack[] getInventory2() {
		return inventory2;
	}

	public void setInventory2(ItemStack[] inventory2) {
		this.inventory2 = inventory2;
	}

	public void startDuel() {
		if (active) return;
		active = true;

		dueler1.setGameMode(GameMode.ADVENTURE);
		dueler2.setGameMode(GameMode.ADVENTURE);
		dueler1.setNoDamageTicks(140);
		dueler2.setNoDamageTicks(140);
		dueler1.getActivePotionEffects().forEach(e -> dueler1.removePotionEffect(e.getType()));
		dueler2.getActivePotionEffects().forEach(e -> dueler1.removePotionEffect(e.getType()));
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
					p1.sendMessage(plugin.getText("hit-30"));
					p2.sendMessage(plugin.getText("hit-30"));
				}
				if (hitClock >= 60 && !firstHit) endDuel(p1, true);
				if (hitClock == 120) {
					p1.sendMessage(plugin.getText("hit-60"));
					p2.sendMessage(plugin.getText("hit-60"));
				}
				if (hitClock >= 180) endDuel(p1, true);

				hitClock++;
				timer++;
			}
		}.runTaskTimer(plugin, 0L, 20);

		this.inventory1 = dueler1.getInventory().getContents().clone();
		this.inventory2 = dueler2.getInventory().getContents().clone();
		this.armor1 = dueler1.getInventory().getArmorContents().clone();
		this.armor2 = dueler2.getInventory().getArmorContents().clone();

		if (!kit.getName().equals(plugin.getText("kit-none"))) {
			dueler1.getInventory().clear();
			dueler2.getInventory().clear();
			dueler1.getInventory().setArmorContents(new ItemStack[dueler1.getInventory().getArmorContents().length]);
			dueler2.getInventory().setArmorContents(new ItemStack[dueler2.getInventory().getArmorContents().length]);

			dueler1.getInventory().setContents(kit.getItems());
			dueler2.getInventory().setContents(kit.getItems());
		}

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
			dueler1.getInventory().setArmorContents(new ItemStack[dueler1.getInventory().getArmorContents().length]);
			dueler2.getInventory().setArmorContents(new ItemStack[dueler2.getInventory().getArmorContents().length]);
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

		dueler1.sendTitle(plugin.getText("duel-title"), plugin.getText("versus-description").replace("%player1%", dueler1.getName()).replace("%player2%", dueler2.getName()));
		dueler2.sendTitle(plugin.getText("duel-title"), plugin.getText("versus-description").replace("%player1%", dueler1.getName()).replace("%player2%", dueler2.getName()));
		dueler1.sendMessage(plugin.getText("duel-info").replace("%dueler1%", dueler1.getName()).replace("%dueler2%", dueler2.getName()));
		dueler2.sendMessage(plugin.getText("duel-info").replace("%dueler1%", dueler1.getName()).replace("%dueler2%", dueler2.getName()));
	}

	public void endDuel(Player loser, boolean stalemate) {
		duelTask.cancel();

		dueler1.setGameMode(GameMode.SURVIVAL);
		dueler2.setGameMode(GameMode.SURVIVAL);
		dueler1.setAllowFlight(false);
		dueler2.setAllowFlight(false);
		
		Player winner;
		if (loser.equals(dueler1)) winner = dueler2;
		else winner = dueler1;

		dueler1.getActivePotionEffects().forEach(e -> dueler1.removePotionEffect(e.getType()));
		dueler2.getActivePotionEffects().forEach(e -> dueler2.removePotionEffect(e.getType()));

		if (!duelArgs.get(6).isEnabled()) {
			dueler1.getInventory().clear();
			dueler2.getInventory().clear();
			dueler1.getInventory().setContents(inventory1);
			dueler2.getInventory().setContents(inventory2);
			dueler1.getInventory().setArmorContents(armor1);
			dueler2.getInventory().setArmorContents(armor2);
			dueler1.updateInventory();
			dueler2.updateInventory();
		}

		if (!stalemate) {
			ArrayList<ItemStack> rewards = new ArrayList<>();
			rewards.addAll(getWager1());
			rewards.addAll(getWager2());

			if (duelArgs.get(6).isEnabled()) { //Risk Inventory
				ItemStack[] loserInv;
				if (loser.equals(dueler2)) loserInv = inventory2;
				else loserInv = inventory1;

				for (ItemStack itemStack : loserInv) {
					if (itemStack == null) continue;
					if (itemStack.getType() == Material.AIR) continue;
					rewards.add(itemStack);
				}

				ItemStack[] loserArmor;
				if (loser.equals(dueler2)) loserArmor = armor2;
				else loserArmor = armor1;

				for (ItemStack itemStack : loserArmor) {
					if (itemStack == null) continue;
					if (itemStack.getType() == Material.AIR) continue;
					rewards.add(itemStack);
				}
			}

			if (duelArgs.get(12).isEnabled()) { //Death Certificate
				ItemStack cert = new ItemStack(Material.PAPER);
				ItemMeta meta = cert.getItemMeta();
				meta.setDisplayName(plugin.getText("certificate-title").replace("%player%", loser.getName()));
				List<String> lore = new ArrayList<>();
				lore.add(plugin.getText("certificate-lore1").replace("%player%", loser.getName()));

				SimpleDateFormat dateformat = new SimpleDateFormat("EEE MM/dd/yy");
				SimpleDateFormat timeformat = new SimpleDateFormat("hh:mmaa");
				Date now = Date.from(Instant.now());
				lore.add(plugin.getText("certificate-lore2").replace("%date%", dateformat.format(now)).replace("%time%", timeformat.format(now)));

				lore.add(plugin.getText("certificate-lore3").replace("%arena%", map.getName()).replace("%winner%", winner.getName()));
				meta.setLore(lore);
				cert.setItemMeta(meta);
				rewards.add(cert);
			}

			winner.sendMessage(plugin.getText("duel-win"));
			if (rewards.size() != 0) winner.sendTitle(plugin.getText("duel-title"), plugin.getText("collect-def"));
			else winner.sendTitle(plugin.getText("duel-title"), plugin.getText("won-def").replace("%player%", winner.getName()));
			plugin.addDuelRewards(winner, rewards);
		} else {
			for (ItemStack item : wager1) winner.getInventory().addItem(item);
			for (ItemStack item : wager2) loser.getInventory().addItem(item);

			loser.sendTitle(plugin.getText("duel-title"), plugin.getText("no-winner"));
			winner.sendTitle(plugin.getText("duel-title"), plugin.getText("no-winner"));

			loser.sendMessage(plugin.getText("duel-cancelled"));
			winner.sendMessage(plugin.getText("duel-cancelled"));
		}

		dueler1.teleport(dueler1.getWorld().getSpawnLocation());
		dueler2.teleport(dueler2.getWorld().getSpawnLocation());

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

	public ArrayList<Kit> getKits() {
		return kits;
	}

	public void setKits(ArrayList<Kit> kits) {
		this.kits = kits;
	}

	public Kit getKit() {
		return kit;
	}

	public void setKit(Kit kit) {
		this.kit = kit;
	}

}
