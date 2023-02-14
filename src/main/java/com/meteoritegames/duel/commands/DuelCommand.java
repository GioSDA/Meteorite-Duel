package com.meteoritegames.duel.commands;

import com.meteoritegames.duel.Main;
import com.meteoritegames.duel.objects.DuelArg;
import com.meteoritegames.duel.objects.DuelMap;
import com.meteoritegames.duel.objects.Duel;
import com.meteoritegames.duel.objects.Kit;
import com.meteoritepvp.api.command.Command;
import com.meteoritepvp.api.command.CommandClass;
import com.meteoritepvp.api.command.DefaultCommand;
import com.meteoritepvp.api.inventory.MeteoriteInventory;
import com.meteoritepvp.api.inventory.presets.BasicInventory;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.sql.Array;
import java.util.*;

public class DuelCommand implements CommandClass {
	private final Main plugin;
	
	public DuelCommand(Main plugin) {
		this.plugin = plugin;
	}

	@Command(name="duel",
			description="Invite another player to a duel",
			params="@player")
	public void duelPlayer(Player p, String[] params) {
		if (params[0].length() == 0) p.sendMessage(plugin.getText("help"));

		Player d = p.getServer().getPlayer(params[0]);

		if (p.equals(d)) {
			p.sendMessage(plugin.getText("duel-self"));
			return;
		}

		if (d == null) {
			p.sendMessage(plugin.getText("player-offline"));
			return;
		}

		if (plugin.playerIsInDuel(d) != null) {
			p.sendMessage(plugin.getText("player-in-duel"));
			return;
		}

		if (plugin.playerIsInDuel(p) != null) {
			p.sendMessage(plugin.getText("in-duel"));
			return;
		}

		if (plugin.getDuel(p) != null) {
			plugin.removeDuel(plugin.getDuel(p));
		}

		if (plugin.noDuel.contains(p)) {
			p.sendMessage(plugin.getText("requests-disabled"));
			return;
		}

		if (plugin.duelRewards.get(p) != null) {
			if (plugin.duelRewards.get(p).size() != 0) {
				p.sendMessage(plugin.getText("rewards-waiting"));
				return;
			}
		}

		Duel duel = new Duel(plugin, p,d);

		createArgsGui(duel);
	}

	@Command(name="duel",
			description="Toggle duel invites from other players",
			args="toggle")
	public void duelToggle(Player sender) {
		if (!plugin.noDuel.contains(sender)) {
			plugin.noDuel.add(sender);
			sender.sendMessage(plugin.getText("disable-requests"));
		} else {
			plugin.noDuel.remove(sender);
			sender.sendMessage(plugin.getText("enable-requests"));
		}
	}

	@Command(name="duel",
			description="Collect your duel winnings",
			args="collect")
	public void duelCollect(Player sender) {
		ArrayList<ItemStack> rewards = plugin.duelRewards.get(sender);

		if (rewards != null && rewards.size() != 0) {
			MeteoriteInventory inventory = new MeteoriteInventory(plugin, plugin.getText("collect-menu"), 9, 6, true);
			BasicInventory page = new BasicInventory(9, 6);

			ItemStack hopper = new ItemStack(Material.HOPPER);
			ItemMeta hopperMeta = hopper.getItemMeta();
			hopperMeta.setDisplayName(plugin.getText("collect-item"));
			hopperMeta.setLore(Collections.singletonList(plugin.getText("collect-desc")));
			hopper.setItemMeta(hopperMeta);
			page.setItem(4, Material.HOPPER);

			for (int i = 0; i < 45 && i < rewards.size(); i++) {
				page.setItem(9+i, rewards.get(i));
			}

			page.setOnSlotClickListener(e -> {
				if (e.getEvent().getRawSlot() == 4) {

					plugin.duelRewards.get(sender).removeIf(reward -> sender.getInventory().addItem(reward).size() == 0);

					if (plugin.duelRewards.get(sender).size() != 0) {
						sender.sendMessage(plugin.getText("not-collected"));
					}

					sender.closeInventory();
				} else if (e.getEvent().getRawSlot() > 8 && e.getEvent().getRawSlot() - 9 < rewards.size()) {
					ItemStack reward = rewards.get(e.getEvent().getRawSlot() - 9);

					if (sender.getInventory().addItem(reward).size() == 0) {
						plugin.duelRewards.get(sender).remove(reward);

						if (plugin.duelRewards.get(sender).size() == 0) {
							plugin.duelRewards.remove(sender);
							sender.sendMessage(plugin.getText("winnings-collected"));
							sender.closeInventory();
						} else {
							duelCollect(sender);
						}
					} else {
						sender.sendMessage(plugin.getText("item-not-collected"));
						sender.closeInventory();
					}
				}
			});

			if (plugin.duelRewards.get(sender).size() == 0) {
				plugin.duelRewards.remove(sender);
				sender.sendMessage(plugin.getText("winnings-collected"));
				sender.closeInventory();
			}

			inventory.applyPage(page);
			inventory.show(sender);
		} else {
			sender.sendMessage(plugin.getText("collect-empty"));
		}

	}

	@Command(name="duel",
			description="Spectate a player",
			args="spectate",
			params="@player")
	public void duelSpectate(Player sender, String[] params) {
		if (plugin.playerIsInDuel(sender) != null) return;

		if (params[0].equalsIgnoreCase("stop")) {
			if (!plugin.spectators.containsKey(sender)) {
				sender.sendMessage(plugin.getText("not-spectating"));
				return;
			}

			sender.teleport(plugin.spectators.get(sender));
			sender.setGameMode(GameMode.SURVIVAL);
			sender.sendMessage(plugin.getText("stop-spectating"));

			plugin.spectators.remove(sender);
			return;
		}

		Player d = sender.getServer().getPlayer(params[0]);

		Duel duel = plugin.playerIsInDuel(d);
		if (duel == null) {
			sender.sendMessage(plugin.getText("invalid-spectate"));
			return;
		}

		if (!plugin.spectators.containsKey(sender)) plugin.spectators.put(sender, sender.getLocation());

		sender.setGameMode(GameMode.SPECTATOR);
		sender.teleport(duel.getMap().getSpawn1());
		sender.sendMessage(plugin.getText("spectate-info"));
	}

	@Command(name="duel",
			description="Help menu",
			args="help")
	public void duelHelp(Player sender) {
		sender.sendMessage(plugin.getText("help"));
	}

	@Command(name="duel",
			description="Accept a duel invitation",
			args="accept",
			params="@player")
	public void duelAccept(Player sender, String[] params) {
		Player p = sender.getServer().getPlayer(params[0]);

		if (p == null) {
			sender.sendMessage(plugin.getText("player-offline"));
			return;
		}

		Duel duel = plugin.getDuel(p);

		if (duel == null || duel.isActive()) {
			sender.sendMessage(plugin.getText("duel-inactive"));
			return;
		}

		createDuelGui(duel.getDueler1(), duel, true, 0);
		createDuelGui(duel.getDueler2(), duel, true, 0);
	}

	@Command(name="duel",
			description="Reload the plugin",
			args="reload")
	public void duelReload(Player sender) {
		if (sender.isOp()) {
			plugin.reload();
		} else {
			sender.sendMessage(plugin.getText("no-permission"));
		}
	}

	@Command(name="duel",
			description="Reload the plugin",
			args="leave")
	public void duelLeave(Player sender) {
		if (plugin.getDuel(sender) == null) {
			sender.sendMessage(plugin.getText("not-in-duel"));
			return;
		}

		if (plugin.playerIsInDuel(sender) != null) {
			Duel d = plugin.playerIsInDuel(sender);
			d.endDuel(sender, false);
		}

		if (plugin.getDuel(sender) != null) {
			Duel d = plugin.getDuel(sender);

			d.getDueler1().sendMessage(plugin.getText("cancel-duel"));
			d.getDueler2().sendMessage(plugin.getText("cancel-duel"));

			d.getDueler1().closeInventory();
			d.getDueler2().closeInventory();

			for (ItemStack b : d.getWager1()) d.getDueler1().getInventory().addItem(b);
			for (ItemStack b : d.getWager2()) d.getDueler2().getInventory().addItem(b);

			plugin.removeDuel(d);
		}
	}

	private void setGuiElement(int slot, BasicInventory page, ArrayList<DuelArg> duelArgs) {
		ItemStack item = new ItemStack(duelArgs.get(slot).getMaterial());
		ItemMeta meta = item.getItemMeta();
		if (duelArgs.get(slot).isEnabled()) {
			meta.addEnchant(Enchantment.DIG_SPEED, 1,true);
			if (item.getType().equals(Material.GOLDEN_APPLE)) item = new ItemStack(Material.GOLDEN_APPLE, 1, (short)1); //Replace with notch apple for glowing effect
			meta.setLore(Arrays.asList("§a§lENABLED", "§r", "§7Click to §7§ntoggle§7 this setting."));
		} else {
			meta.setLore(Arrays.asList("§c§lDISABLED", "§r", "§7Click to §7§ntoggle§7 this setting."));
		}
		meta.setDisplayName("§e§l" + duelArgs.get(slot).getName());
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);

		if (slot > 9) slot++;
		if (slot > 13) slot++;

		page.setItem(slot, item);
	}

	private void createArgsGui(Duel duel) {
		ArrayList<DuelArg> duelArgs = duel.getDuelArgs();

		MeteoriteInventory inventory = new MeteoriteInventory(plugin, plugin.getText("duel-menu"), 9, 3, true);
		BasicInventory page = new BasicInventory(9, 3);
		page.fill(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7));

		for (int i = 0; i < duelArgs.size(); i++) {
			setGuiElement(i, page, duelArgs);
		}

		page.setItem(22, generateRulesItem(duel, plugin.getText("duel-settings"), new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13)));
		page.setItem(26, generateKitItem(duel));
		page.setItem(18, generateKitItem(duel));

		page.setOnSlotClickListener(e -> {
			if (e.getEvent().getSlotType().equals(InventoryType.SlotType.OUTSIDE)) return;

			if (e.getEvent().getRawSlot() == 18 || e.getEvent().getRawSlot() == 26) {
				duel.getDueler1().closeInventory();
				createKitGui(duel);
			}

			if (e.getEvent().getRawSlot() == 22) {
				duel.getDueler1().closeInventory();
				createMapGui(duel);
			}

			if (duelArgs.size() <= e.getEvent().getRawSlot()) return;
			duelArgs.get(e.getEvent().getRawSlot()).setEnabled(!duelArgs.get(e.getEvent().getRawSlot()).isEnabled());

			setGuiElement(e.getEvent().getRawSlot(), page, duelArgs);

			page.setItem(22, generateRulesItem(duel, plugin.getText("duel-settings"), new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13)));
			page.setItem(26, generateKitItem(duel));
			page.setItem(18, generateKitItem(duel));

			inventory.applyPage(page);
			inventory.show(duel.getDueler1());
		});

		inventory.applyPage(page);

		inventory.show(duel.getDueler1());
	}

	private ItemStack generateKitItem(Duel d) {
		ItemStack item = new ItemStack(d.getKit().getSymbol());
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(d.getKit().getName());
		List<String> lore = Collections.singletonList("§7Click to select a kit.");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		return item;
	}

	private ItemStack generateRulesItem(Duel d, String title, ItemStack item) {
		ItemMeta continueMeta = item.getItemMeta();
		continueMeta.setDisplayName(title);

		List<String> continueLore = new ArrayList<>();
		continueLore.add("");
		for (DuelArg arg : d.getDuelArgs()) {
			continueLore.add(plugin.getText("duel-args").replace("%arg%", arg.getName()).replace("%enabled%", arg.isEnabled() ? "§aENABLED" : "§cDISABLED"));
		}
		continueLore.add(plugin.getText("duel-kit").replace("%kit%", d.getKit().getName()));

		continueLore.add("");
		continueLore.add(plugin.getText("duel-select"));

		continueMeta.setLore(continueLore);

		item.setItemMeta(continueMeta);

		return item;
	}

	private void createKitGui(Duel duel) {
		MeteoriteInventory inventory = new MeteoriteInventory(plugin, plugin.getText("kit-menu"), 9, 1, true);
		BasicInventory page = new BasicInventory(9, 1);
		page.fill(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7));

		for (int i = 0; i < duel.getKits().size(); i++) {
			Kit kit = duel.getKits().get(i);
			ItemStack item = new ItemStack(kit.getSymbol());
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(kit.getName());
			meta.setLore(Collections.singletonList(plugin.getText("kit-select")));

			item.setItemMeta(meta);

			page.setItem(i, item);
		}

		page.setOnSlotClickListener(e -> {
			if (e.getEvent().getSlotType().equals(InventoryType.SlotType.OUTSIDE)) return;
			if (e.getEvent().getRawSlot() >= duel.getKits().size()) return;

			duel.setKit(duel.getKits().get(e.getEvent().getRawSlot()));
			duel.getDueler1().closeInventory();
			createArgsGui(duel);
		});

		inventory.applyPage(page);
		inventory.show(duel.getDueler1());
	}

	private void createMapGui(Duel duel) {
		ArrayList<DuelMap> maps = plugin.getMaps();

		MeteoriteInventory inventory = new MeteoriteInventory(plugin, plugin.getText("map-menu"), 9, 1, true);
		BasicInventory page = new BasicInventory(9, 1);
		page.fill(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7));

		for (DuelMap map : plugin.getMaps()) {
			ItemStack item = new ItemStack(map.getIcon());
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(map.getName());
			Duel mapDuel = plugin.mapIsActive(map);

			if (mapDuel == null) meta.setLore(Collections.singletonList(plugin.getText("map-open")));
			else meta.setLore(Collections.singletonList(plugin.getText("map-taken").replace("%player1%", mapDuel.getDueler1().getName()).replace("%player2%", mapDuel.getDueler2().getName())));

			item.setItemMeta(meta);

			page.setItem(map.getInvPos(), item);
		}

		page.setOnSlotClickListener(e -> {
			if (e.getEvent().getSlotType().equals(InventoryType.SlotType.OUTSIDE)) return;
			Optional<DuelMap> m = maps.stream().filter(n -> n.getInvPos() == e.getEvent().getRawSlot()).findAny();
			DuelMap map;
			if (m.isPresent()) map = m.get();
			else return;



			if (plugin.mapIsActive(map) != null) {
				e.getEvent().getWhoClicked().sendMessage(plugin.getText("map-unavailable"));
				return;
			}

			duel.setMap(map);
			duel.getDueler1().closeInventory();

			BaseComponent[] message =
					new ComponentBuilder(duel.getDueler1().getName()).color(ChatColor.BLUE)
							.append(" has invited you to a duel!").color(ChatColor.AQUA)
							.append(" Click here to accept.").color(ChatColor.GOLD).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel accept " + duel.getDueler1().getName())).create();

			duel.getDueler2().spigot().sendMessage(message);

			e.getEvent().getWhoClicked().sendMessage(plugin.getText("request-sent"));
			plugin.addDuel(duel);
		});

		inventory.applyPage(page);
		inventory.show(duel.getDueler1());
	}

	public void createDuelGui(Player p, Duel duel, boolean forced, int timer) {
		MeteoriteInventory inventory = new MeteoriteInventory(plugin, plugin.getText("wager-menu"), 9, 6, true);
		BasicInventory page = new BasicInventory(9, 6);

		if (p.getOpenInventory().getTitle().equals("§8Inventory view") && !forced) return;
		if (duel.isActive()) return;

		String ready1 = plugin.getText("wager-ready");
		if (duel.isAccepted1()) ready1 = ready1.replace("%player%","§a* " + duel.getDueler1().getName()).replace("%ready%", "READY");
		else ready1 = ready1.replace("%player%","§c* " + duel.getDueler1().getName()).replace("%ready%", "NOT READY");

		String ready2 = plugin.getText("wager-ready");
		if (duel.isAccepted2()) ready2 = ready2.replace("%player%","§a* " + duel.getDueler2().getName()).replace("%ready%", "READY");
		else ready2 = ready2.replace("%player%","§c* " + duel.getDueler2().getName()).replace("%ready%", "NOT READY");
		
		Player player1;
		Player player2;
		boolean accepted1;
		boolean accepted2;
		
		if (duel.getDueler1().equals(p)) {
			player1 = duel.getDueler1();
			player2 = duel.getDueler2();
			accepted1 = duel.isAccepted1();
			accepted2 = duel.isAccepted2();
		} else {
			player1 = duel.getDueler2();
			player2 = duel.getDueler1();
			accepted1 = duel.isAccepted2();
			accepted2 = duel.isAccepted1();
		}

		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());

		SkullMeta skmeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);

		skmeta.setOwner(player2.getName());
		skmeta.setDisplayName(duel.isAccepted2() ? "§aREADY " : "§cNOT READY ");
		skmeta.setLore(Arrays.asList(ready1,ready2));
		skull.setItemMeta(skmeta);

		page.setItem(4, skull);

		ItemStack item2;
		if (accepted2) {
			item2 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13);
			ItemMeta meta = item2.getItemMeta();
			meta.setDisplayName(plugin.getText("wager-accepted").replace("%player%", player2.getName()));
			item2.setItemMeta(meta);
		} else {
			item2 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
			ItemMeta meta = item2.getItemMeta();
			meta.setDisplayName(plugin.getText("wager-notaccepted").replace("%player%", player2.getName()));
			item2.setItemMeta(meta);
		}

		page.setItem(13, item2);

		page.setItem(22, generateRulesItem(duel, "§e§lSettings", new ItemStack(Material.BOOK)));

		ItemStack anvil = new ItemStack(Material.ANVIL);
		ItemMeta anvilMeta = anvil.getItemMeta();
		anvilMeta.setDisplayName(plugin.getText("wager-inventory").replace("%player%", player2.getName()));
		anvilMeta.setLore(Collections.singletonList(plugin.getText("wager-inv2")));
		anvil.setItemMeta(anvilMeta);

		page.setItem(31, anvil);

		ItemStack item1;
		if (accepted1) {
			if (accepted2) {
				item1 = new ItemStack(Material.WATCH, timer);
				ItemMeta meta = item1.getItemMeta();
				meta.setDisplayName(plugin.getText("wager-starting").replace("%seconds%", ""+ timer));
				meta.setLore(Collections.singletonList(plugin.getText("wager-prepare")));
				item1.setItemMeta(meta);
			} else {
				item1 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13);
				ItemMeta meta = item1.getItemMeta();
				meta.setDisplayName(plugin.getText("wager-notaccepted").replace("%player%", player1.getName()));
				item1.setItemMeta(meta);
			}
		} else {
			item1 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
			ItemMeta meta = item1.getItemMeta();
			meta.setDisplayName(plugin.getText("wager-notaccepted").replace("%player%", player1.getName()));
			item1.setItemMeta(meta);
		}

		page.setItem(40, item1);

		ItemStack skull2 = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());

		SkullMeta skmeta2 = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);

		skmeta2.setOwner(player1.getName());
		skmeta2.setDisplayName(duel.isAccepted1() ? "§aREADY " : "§cNOT READY ");
		skmeta2.setLore(Arrays.asList(ready1, ready2));
		skull2.setItemMeta(skmeta2);

		page.setItem(49, skull2);

		for (int i = 0; i < 24; i++) {
			if (i >= duel.getWager1().size()) break;
			page.setItem(i % 4 + ((i / 4)*9), duel.getWager1().get(i));
		}

		for (int i = 0; i < 24; i++) {
			if (i >= duel.getWager2().size()) break;
			page.setItem(i % 4 + ((i / 4)*9+5), duel.getWager2().get(i));
		}

		page.setOnSlotClickListener(e -> {
			if (e.getEvent().getSlotType().equals(InventoryType.SlotType.OUTSIDE)) return;

			if (e.getSlot() != 40 && timer != 0) return;

			if (e.getEvent().getRawSlot() == 31) {
				e.getEvent().getWhoClicked().closeInventory();
				createInventoryGui((Player) e.getEvent().getWhoClicked(), duel);
			}

			if (p.equals(duel.getDueler1())) {
				if (e.getSlotX() < 4 && e.getEvent().getRawSlot() < 54 && e.getInventory().getInventory().getItem(e.getSlot()) != null && !e.getInventory().getInventory().getItem(e.getSlot()).equals(new ItemStack(Material.AIR))) {
					int index = e.getEvent().getRawSlot() + (e.getSlotY()*9);
					if (index >= duel.getWager1().size()) return;

					player1.getInventory().addItem(duel.getWager1().get(index));
					duel.getWager1().remove(index);

					createDuelGui(player1, duel, false, 0);
					createDuelGui(player2, duel, false, 0);
				}
			} else if (p.equals(duel.getDueler2())) {
				if (e.getSlotX() < 4 && e.getEvent().getRawSlot() < 54 && e.getInventory().getInventory().getItem(e.getSlot()) != null && !e.getInventory().getInventory().getItem(e.getSlot()).equals(new ItemStack(Material.AIR))) {
					int index = e.getEvent().getRawSlot() + (e.getSlotY()*9);
					if (index >= duel.getWager2().size()) return;

					player2.getInventory().addItem(duel.getWager2().get(index));
					duel.getWager2().remove(index);

					createDuelGui(player1, duel, false, 0);
					createDuelGui(player2, duel, false, 0);
				}
			}

			if (e.getEvent().getRawSlot() == 40 && p.equals(duel.getDueler1())) {
				if (!duel.isAccepted1()) duel.setAccepted1(true);
				else if (duel.isAccepted1()) duel.setAccepted1(false);

				createDuelGui(player1, duel, false, 0);
				createDuelGui(player2, duel, false, 0);
			}

			if (e.getEvent().getRawSlot() == 40 && p.equals(duel.getDueler2())) {
				if (!duel.isAccepted2()) duel.setAccepted2(true);
				else if (duel.isAccepted2()) duel.setAccepted2(false);

				createDuelGui(player1, duel, false, 0);
				createDuelGui(player2, duel, false, 0);
			}

			if (duel.isAccepted1() && duel.isAccepted2()) {
				BukkitTask task = new BukkitRunnable() {
					final Player p1 = player1;
					final Player p2 = player2;

					int timer = 100;

					@Override
					public void run() {

						if (timer > 0) {
							if (!duel.isAccepted1() || !duel.isAccepted2() || !p1.isOnline() || !p2.isOnline()) {
								timer = 100;
								this.cancel();
							}

							if (timer % 20 == 0) {
								createDuelGui(p1, duel, true, timer / 20);
								createDuelGui(p2, duel, true, timer / 20);
							}
						} else {
							duel.startDuel();
							this.cancel();
						}

						timer--;
					}
				}.runTaskTimer(plugin, 0L, 1);
			}

			if (e.getEvent().getRawSlot() >= 54) {
				if (p.equals(duel.getDueler1())) {
					ItemStack wagerItem = p.getInventory().getItem(e.getSlot());
					if (wagerItem == null) return;
					if (wagerItem.getType() == Material.AIR) return;

					duel.getWager1().add(wagerItem);
					p.getInventory().removeItem(wagerItem);

					createDuelGui(player1, duel, false, 0);
					createDuelGui(player2, duel, false, 0);
				} else if (p.equals(duel.getDueler2())) {
					ItemStack wagerItem = p.getInventory().getItem(e.getSlot());
					if (wagerItem == null) return;
					if (wagerItem.getType() == Material.AIR) return;

					duel.getWager2().add(p.getInventory().getItem(e.getSlot()));
					p.getInventory().removeItem(wagerItem);

					createDuelGui(player1, duel, false, 0);
					createDuelGui(player2, duel, false, 0);
				}
			}

			p.updateInventory();
		});

		inventory.setPage(page);
		inventory.show(p);

		inventory.update();
	}

	public void createInventoryGui(Player p, Duel duel) {
		MeteoriteInventory inventory = new MeteoriteInventory(plugin, plugin.getText("inventory-menu"), 9, 5, true);
		BasicInventory page = new BasicInventory(9, 5);
		page.setItem(36, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7));
		page.setItem(37, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7));
		page.setItem(40, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7));
		page.setItem(43, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7));
		page.setItem(44, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7));

		Player duelist = p.getPlayer().equals(duel.getDueler1()) ? duel.getDueler2() : duel.getDueler1();

		for (int i = 0; i < 9; i++) {
			page.setItem(27 + i, duelist.getInventory().getItem(i));
		}

		for (int i = 9; i < 36; i++) {
			page.setItem(i - 9, duelist.getInventory().getItem(i));
		}

		page.setItem(38, duelist.getInventory().getHelmet());
		page.setItem(39, duelist.getInventory().getChestplate());
		page.setItem(41, duelist.getInventory().getLeggings());
		page.setItem(42, duelist.getInventory().getBoots());

		page.setItem(44, Material.ARROW, plugin.getText("inventory-wagering"));

		page.setOnSlotClickListener(e -> {
			if (e.getEvent().getSlotType().equals(InventoryType.SlotType.OUTSIDE)) return;

			if (e.getEvent().getRawSlot() == 44) {
				e.getEvent().getWhoClicked().closeInventory();
				createDuelGui((Player) e.getEvent().getWhoClicked(), duel, true, 0);
			}
		});

		inventory.applyPage(page);
		inventory.show(p);
	}

}
