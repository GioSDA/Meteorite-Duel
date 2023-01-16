package com.meteoritegames.duel.commands;

import com.meteoritegames.duel.Main;
import com.meteoritegames.duel.objects.DuelArg;
import com.meteoritegames.duel.objects.DuelMap;
import com.meteoritegames.duel.objects.Duel;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

@DefaultCommand
public class DuelCommand implements CommandClass {
	private final Main plugin;
	
	public DuelCommand(Main plugin) {
		this.plugin = plugin;
	}
	
	@Command(description="Invite another player to a duel",
			params="@player")
	public void duelPlayer(Player p, String[] params) {
		Player d = p.getServer().getPlayer(params[0]);

		if (p.equals(d)) {
			p.sendMessage("§cYou can't duel yourself!");
			return;
		}

		if (d == null) {
			p.sendMessage("§cThat player is not online!");
			return;
		}

		if (plugin.playerIsInDuel(d) != null) {
			p.sendMessage("§cThat player is already in a duel!");
			return;
		}

		if (plugin.playerIsInDuel(p) != null) {
			p.sendMessage("§cYou are already in a duel!");
			return;
		}

		if (plugin.getDuel(p) != null) {
			plugin.removeDuel(plugin.getDuel(p));
		}

		if (plugin.noDuel.contains(p)) {
			p.sendMessage("§cThis player has duel requests disabled!");
			return;
		}

		if (plugin.duelRewards.get(p) != null) {
			p.sendMessage("§cYou have duel rewards waiting to be collected!");
			return;
		}

		Duel duel = new Duel(plugin, p,d);

		createArgsGui(duel);
	}

	@Command(description="Toggle duel invites from other players",
			args="toggle")
	public void duelToggle(Player sender) {
		if (!plugin.noDuel.contains(sender)) {
			plugin.noDuel.add(sender);
			sender.sendMessage("§cDuel requests have been disabled!");
		} else {
			plugin.noDuel.remove(sender);
			sender.sendMessage("§aDuel requests have been enabled!");
		}
	}

	@Command(description="Collect your duel winnings",
			args="collect")
	public void duelCollect(Player sender) {
		ArrayList<ItemStack> rewards = plugin.duelRewards.get(sender);

		if (rewards != null) {
			MeteoriteInventory inventory = new MeteoriteInventory(plugin, "§8Duel Collect Bin", 9, 6, true);
			BasicInventory page = new BasicInventory(9, 6);

			ItemStack hopper = new ItemStack(Material.HOPPER);
			ItemMeta hopperMeta = hopper.getItemMeta();
			hopperMeta.setDisplayName("&e&lDuel Collect");
			hopperMeta.setLore(Collections.singletonList("&7Click to collect everything"));
			hopper.setItemMeta(hopperMeta);
			page.setItem(4, Material.HOPPER);

			for (int i = 0; i < 45 && i < rewards.size(); i++) {
				page.setItem(9+i, rewards.get(i));
			}

			page.setOnSlotClickListener(e -> {
				if (e.getEvent().getRawSlot() == 4) {

					plugin.duelRewards.get(sender).removeIf(reward -> sender.getInventory().addItem(reward) != null);

					if (plugin.duelRewards.get(sender).size() == 0) {
						plugin.duelRewards.remove(sender);
						sender.sendMessage("§aWinnings have been collected!");
						sender.closeInventory();
					} else {
						sender.sendMessage("§cNot all winnings were collected! Try emptying your inventory.");
						sender.closeInventory();
					}
				} else if (e.getEvent().getRawSlot() > 8 && e.getEvent().getRawSlot() - 9 <= rewards.size()) {
					ItemStack reward = rewards.get(e.getEvent().getRawSlot() - 9);

					if (sender.getInventory().addItem(reward) != null) {
						plugin.duelRewards.get(sender).remove(reward);
						duelCollect(sender);
					} else {
						sender.sendMessage("§cItem could not be collected! Try emptying your inventory.");
						sender.closeInventory();
					}
				}
			});

			inventory.applyPage(page);
			inventory.show(sender);
		} else {
			sender.sendMessage("§c§l(!) §cYour §n/duel collect§c is empty.");
		}

	}

	@Command(description="Spectate a player",
			args="spectate",
			params="@player")
	public void duelSpectate(Player sender, String[] params) {
		if (plugin.playerIsInDuel(sender) != null) return;

		if (params[0].equalsIgnoreCase("stop")) {
			if (!plugin.spectators.containsKey(sender)) {
				sender.sendMessage("§cYou are not currently spectating anyone!");
				return;
			}

			sender.teleport(plugin.spectators.get(sender));
			sender.setGameMode(GameMode.SURVIVAL);
			sender.sendMessage("§cStopped spectating!");

			plugin.spectators.remove(sender);
			return;
		}

		Player d = sender.getServer().getPlayer(params[0]);

		Duel duel = plugin.playerIsInDuel(d);
		if (duel == null) {
			sender.sendMessage("§cThat player is not currently in a duel!");
			return;
		}

		if (!plugin.spectators.containsKey(sender)) plugin.spectators.put(sender, sender.getLocation());

		sender.setGameMode(GameMode.SPECTATOR);
		sender.teleport(duel.getMap().getSpawn1());
		sender.sendMessage("§eType §6/duel spectate stop§e to stop spectating!");
	}

	@Command(description="Accept a duel invitation",
			args="accept",
			params="@player")
	public void duelAccept(Player sender, String[] params) {
		Player p = sender.getServer().getPlayer(params[0]);

		if (p == null) {
			sender.sendMessage("§cThat player is not online!");
			return;
		}

		Duel duel = plugin.getDuel(p);

		if (duel == null || duel.isActive()) {
			sender.sendMessage("§cThat duel request is not active!");
			return;
		}

		createDuelGui(duel.getDueler1(), duel, true, 0);
		createDuelGui(duel.getDueler2(), duel, true, 0);
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

		page.setItem(slot, item);
	}

	private void createArgsGui(Duel duel) {
		ArrayList<DuelArg> duelArgs = duel.getDuelArgs();

		MeteoriteInventory inventory = new MeteoriteInventory(plugin, "§8Duel Settings", 9, 3, true);
		BasicInventory page = new BasicInventory(9, 3);
		page.fill(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7));

		for (int i = 0; i < duelArgs.size(); i++) {
			setGuiElement(i, page, duelArgs);
		}

		ItemStack continueItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13);
		ItemMeta continueMeta = continueItem.getItemMeta();
		continueMeta.setDisplayName("§a§lContinue to map select");

		List<String> continueLore = new ArrayList<>();
		for (DuelArg arg : duel.getDuelArgs()) {
			continueLore.add(arg.getName() + ": " + (arg.isEnabled() ? "§aENABLED" : "§cDISABLED"));
		}

		continueMeta.setLore(continueLore);

		continueItem.setItemMeta(continueMeta);
		page.setItem(22, continueItem);

		page.setOnSlotClickListener(e -> {
			if (e.getEvent().getSlotType().equals(InventoryType.SlotType.OUTSIDE)) return;

			if (e.getEvent().getRawSlot() == 22) {
				duel.getDueler1().closeInventory();
				createMapGui(duel);
			}

			if (duelArgs.size() <= e.getEvent().getRawSlot()) return;
			duelArgs.get(e.getEvent().getRawSlot()).setEnabled(!duelArgs.get(e.getEvent().getRawSlot()).isEnabled());

			setGuiElement(e.getEvent().getRawSlot(), page, duelArgs);
			ItemStack ncontinueItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13);
			ItemMeta ncontinueMeta = ncontinueItem.getItemMeta();
			ncontinueMeta.setDisplayName("§a§lContinue to map select");

			List<String> ncontinueLore = new ArrayList<>();
			for (DuelArg arg : duel.getDuelArgs()) {
				ncontinueLore.add(arg.getName() + ": " + (arg.isEnabled() ? "§aENABLED" : "§cDISABLED"));
			}

			ncontinueMeta.setLore(ncontinueLore);

			ncontinueItem.setItemMeta(ncontinueMeta);
			page.setItem(22, ncontinueItem);

			inventory.applyPage(page);
			inventory.show(duel.getDueler1());
		});

		inventory.applyPage(page);

		inventory.show(duel.getDueler1());
	}

	private void createMapGui(Duel duel) {
		ArrayList<DuelMap> maps = plugin.getMaps();

		MeteoriteInventory inventory = new MeteoriteInventory(plugin, "§8Map Settings", 9, 3, true);
		BasicInventory page = new BasicInventory(9, 3);
		page.fill(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7));

		for (DuelMap map : plugin.getMaps()) {
			ItemStack item = new ItemStack(map.getIcon());
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(map.getName());
			Duel mapDuel = plugin.mapIsActive(map);

			if (mapDuel == null) meta.setLore(Collections.singletonList("§a§lOPEN"));
			else meta.setLore(Collections.singletonList("§e§n%player1% §evs §e§n%player2%".replace("%player1%", mapDuel.getDueler1().getName()).replace("%player2%", mapDuel.getDueler2().getName())));

			item.setItemMeta(meta);

			page.setItem(map.getInvPos(), item);
			System.out.println(item);
			System.out.println(map.getInvPos());
		}

		page.setOnSlotClickListener(e -> {
			if (e.getEvent().getSlotType().equals(InventoryType.SlotType.OUTSIDE)) return;
			Optional<DuelMap> m = maps.stream().filter(n -> n.getInvPos() == e.getEvent().getRawSlot()).findAny();
			DuelMap map;
			if (m.isPresent()) map = m.get();
			else return;



			if (plugin.mapIsActive(map) != null) {
				e.getEvent().getWhoClicked().sendMessage("§cThat map is currently unavailable!");
				return;
			}

			duel.setMap(map);
			duel.getDueler1().closeInventory();

			BaseComponent[] message =
					new ComponentBuilder(duel.getDueler1().getName()).color(ChatColor.BLUE)
							.append(" has invited you to a duel!").color(ChatColor.AQUA)
							.append(" Click here to accept.").color(ChatColor.GOLD).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel accept " + duel.getDueler1().getName())).create();

			duel.getDueler2().spigot().sendMessage(message);

			e.getEvent().getWhoClicked().sendMessage("§eYour duel request has been sent.");
			plugin.addDuel(duel);
		});

		inventory.applyPage(page);
		inventory.show(duel.getDueler1());
	}

	public void createDuelGui(Player p, Duel duel, boolean forced, int timer) {
		MeteoriteInventory inventory = new MeteoriteInventory(plugin, "§8Duel Wager", 9, 6, true);
		BasicInventory page = new BasicInventory(9, 6);

		if (p.getOpenInventory().getTitle().equals("§8Inventory view") && !forced) return;
		if (duel.isActive()) return;

		String ready1 = "%player1%: %ready%";
		if (duel.isAccepted1()) ready1 = ready1.replace("%player1%","§a" + duel.getDueler1().getName()).replace("%ready%", "READY");
		else ready1 = ready1.replace("%player1%","§c" + duel.getDueler1().getName()).replace("%ready%", "NOT READY");

		String ready2 = "%player2%: %ready%";
		if (duel.isAccepted2()) ready2 = ready2.replace("%player2%","§a" + duel.getDueler2().getName()).replace("%ready%", "READY");
		else ready2 = ready2.replace("%player2%","§c" + duel.getDueler2().getName()).replace("%ready%", "NOT READY");

		if (duel.getDueler1().equals(p)) { //Player is first
			ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());

			SkullMeta skmeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);

			skmeta.setOwner(duel.getDueler2().getName());
			skmeta.setDisplayName(duel.getDueler2().getName());
			skmeta.setLore(Arrays.asList(duel.isAccepted2() ? "§aREADY " : "§cNOT READY ",ready1, ready2));
			skull.setItemMeta(skmeta);

			page.setItem(4, skull);

			ItemStack item2;
			if (duel.isAccepted2()) {
				item2 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13);
				ItemMeta meta = item2.getItemMeta();
				meta.setDisplayName("§6" + duel.getDueler2().getDisplayName() + "§a§l Has accepted!");
				item2.setItemMeta(meta);
			} else {
				item2 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
				ItemMeta meta = item2.getItemMeta();
				meta.setDisplayName("§6" + duel.getDueler2().getDisplayName() + "§c§l Has not accepted!");
				item2.setItemMeta(meta);
			}

			page.setItem(13, item2);

			ItemStack book = new ItemStack(Material.BOOK);
			ItemMeta bookMeta = book.getItemMeta();
			List<String> bookLore = new ArrayList<>();
			for (DuelArg arg : duel.getDuelArgs()) {
				bookLore.add(arg.getName() + ": " + (arg.isEnabled() ? "§aENABLED" : "§cDISABLED"));
			}

			bookMeta.setLore(bookLore);
			book.setItemMeta(bookMeta);
			page.setItem(22, book);

			ItemStack anvil = new ItemStack(Material.ANVIL);
			ItemMeta anvilMeta = anvil.getItemMeta();
			anvilMeta.setDisplayName("§c§l%player% Inventory".replace("%player%", duel.getDueler2().getName()));
			anvilMeta.setLore(Collections.singletonList("§7Click to see your opponent's inventory"));
			anvil.setItemMeta(anvilMeta);

			page.setItem(31, anvil);

			ItemStack item1;
			if (duel.isAccepted1()) {
				if (duel.isAccepted2()) {
					item1 = new ItemStack(Material.WATCH, timer);
					ItemMeta meta = item1.getItemMeta();
					meta.setDisplayName("§e§lDUEL STARTING §ein §e§n%seconds%s§r".replace("%seconds%", ""+ timer));
					meta.setLore(Collections.singletonList("§7Prepare for battle!"));
					item1.setItemMeta(meta);
				} else {
					item1 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13);
					ItemMeta meta = item1.getItemMeta();
					meta.setDisplayName("§6" + duel.getDueler1().getDisplayName() + "§a§l Has accepted!");
					item1.setItemMeta(meta);
				}
			} else {
				item1 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
				ItemMeta meta = item1.getItemMeta();
				meta.setDisplayName("§6" + duel.getDueler1().getDisplayName() + "§c§l Has not accepted!");
				item1.setItemMeta(meta);
			}

			page.setItem(40, item1);

			ItemStack skull2 = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());

			SkullMeta skmeta2 = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);

			skmeta2.setOwner(duel.getDueler1().getName());
			skmeta2.setDisplayName(duel.getDueler1().getName());
			skmeta2.setLore(Arrays.asList(duel.isAccepted1() ? "§aREADY " : "§cNOT READY ",ready1, ready2));
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
		} else { //Player is second
			ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());

			SkullMeta skmeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);

			skmeta.setOwner(duel.getDueler1().getName());
			skmeta.setDisplayName(duel.getDueler1().getName());
			skmeta.setLore(Arrays.asList(duel.isAccepted1() ? "§aREADY " : "§cNOT READY ",ready1, ready2));
			skull.setItemMeta(skmeta);

			page.setItem(4, skull);

			ItemStack item2;
			if (duel.isAccepted1()) {
				item2 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13);
				ItemMeta meta = item2.getItemMeta();
				meta.setDisplayName("§6" + duel.getDueler1().getDisplayName() + "§a§l Has accepted!");
				item2.setItemMeta(meta);
			} else {
				item2 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
				ItemMeta meta = item2.getItemMeta();
				meta.setDisplayName("§6" + duel.getDueler1().getDisplayName() + "§c§l Has not accepted!");
				item2.setItemMeta(meta);
			}

			page.setItem(13, item2);

			ItemStack book = new ItemStack(Material.BOOK);
			ItemMeta bookMeta = book.getItemMeta();
			List<String> bookLore = new ArrayList<>();
			for (DuelArg arg : duel.getDuelArgs()) {
				bookLore.add(arg.getName() + ": " + (arg.isEnabled() ? "§aENABLED" : "§cDISABLED"));
			}

			bookMeta.setLore(bookLore);
			book.setItemMeta(bookMeta);
			page.setItem(22, book);

			ItemStack anvil = new ItemStack(Material.ANVIL);
			ItemMeta anvilMeta = anvil.getItemMeta();
			anvilMeta.setDisplayName("§c§l%player% Inventory".replace("%player%", duel.getDueler1().getName()));
			anvilMeta.setLore(Collections.singletonList("§7Click to see your opponent's inventory"));
			anvil.setItemMeta(anvilMeta);

			page.setItem(31, anvil);

			ItemStack item1;
			if (duel.isAccepted2()) {
				if (duel.isAccepted1()) {
					item1 = new ItemStack(Material.WATCH, timer);
					ItemMeta meta = item1 .getItemMeta();
					meta.setDisplayName("§e§lDUEL STARTING §ein §e§n%seconds%s§r".replace("%seconds%", ""+ timer));
					meta.setLore(Collections.singletonList("§7Prepare for battle!"));
					item1.setItemMeta(meta);
				} else {
					item1 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13);
					ItemMeta meta = item1.getItemMeta();
					meta.setDisplayName("§6" + duel.getDueler2().getDisplayName() + "§a§l Has accepted!");
					item1.setItemMeta(meta);
				}
			} else {
				item1 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
				ItemMeta meta = item1.getItemMeta();
				meta.setDisplayName("§6" + duel.getDueler2().getDisplayName() + "§c§l Has not accepted!");
				item1.setItemMeta(meta);
			}

			page.setItem(40, item1);

			ItemStack skull2 = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());

			SkullMeta skmeta2 = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);

			skmeta2.setOwner(duel.getDueler2().getName());
			skmeta2.setDisplayName(duel.getDueler2().getName());
			skmeta2.setLore(Arrays.asList(duel.isAccepted2() ? "§aREADY " : "§cNOT READY ",ready1, ready2));
			skull2.setItemMeta(skmeta2);

			page.setItem(49, skull2);

			for (int i = 0; i < 24; i++) {
				if (i >= duel.getWager2().size()) break;
				page.setItem(i % 4 + ((i / 4)*9), duel.getWager2().get(i));
			}

			for (int i = 0; i < 24; i++) {
				if (i >= duel.getWager1().size()) break;
				page.setItem(i % 4 + ((i / 4)*9+5), duel.getWager1().get(i));
			}
		}

		page.setOnSlotClickListener(e -> {
			if (e.getEvent().getSlotType().equals(InventoryType.SlotType.OUTSIDE)) return;

			if (e.getSlot() != 40 && timer != 0) return;

			if (e.getEvent().getRawSlot() == 31) {
				e.getEvent().getWhoClicked().closeInventory();
				createInventoryGui((Player) e.getEvent().getWhoClicked(), duel);
			}

			if (duel.getDueler1().equals(p)) {
				if (e.getSlotX() < 4 && e.getEvent().getRawSlot() < 54 && e.getInventory().getInventory().getItem(e.getSlot()) != null && !e.getInventory().getInventory().getItem(e.getSlot()).equals(new ItemStack(Material.AIR))) {
					int index = e.getEvent().getRawSlot() + (e.getSlotY()*9);
					if (index >= duel.getWager1().size()) return;

					duel.getDueler1().getInventory().addItem(duel.getWager1().get(index));
					duel.getWager1().remove(index);

					createDuelGui(duel.getDueler1(), duel, false, 0);
					createDuelGui(duel.getDueler2(), duel, false, 0);
				}
			} else if (duel.getDueler2().equals(p)) {
				if (e.getSlotX() < 4 && e.getEvent().getRawSlot() < 54 && e.getInventory().getInventory().getItem(e.getSlot()) != null && !e.getInventory().getInventory().getItem(e.getSlot()).equals(new ItemStack(Material.AIR))) {
					int index = e.getEvent().getRawSlot() + (e.getSlotY()*9);
					if (index >= duel.getWager2().size()) return;

					duel.getDueler2().getInventory().addItem(duel.getWager2().get(index));
					duel.getWager2().remove(index);

					createDuelGui(duel.getDueler1(), duel, false, 0);
					createDuelGui(duel.getDueler2(), duel, false, 0);
				}
			}

			if (e.getEvent().getRawSlot() == 40 && duel.getDueler1().equals(p)) {
				if (!duel.isAccepted1()) duel.setAccepted1(true);
				else if (duel.isAccepted1()) duel.setAccepted1(false);

				createDuelGui(duel.getDueler1(), duel, false, 0);
				createDuelGui(duel.getDueler2(), duel, false, 0);
			}

			if (e.getEvent().getRawSlot() == 40 && duel.getDueler2().equals(p)) {
				if (!duel.isAccepted2()) duel.setAccepted2(true);
				else if (duel.isAccepted2()) duel.setAccepted2(false);

				createDuelGui(duel.getDueler1(), duel, false, 0);
				createDuelGui(duel.getDueler2(), duel, false, 0);
			}

			if (duel.isAccepted1() && duel.isAccepted2()) {
				BukkitTask task = new BukkitRunnable() {
					final Player p1 = duel.getDueler1();
					final Player p2 = duel.getDueler2();

					int timer = 5;

					@Override
					public void run() {

						if (timer > 0) {
							if (!duel.isAccepted1() || !duel.isAccepted2() || !p1.isOnline() || !p2.isOnline()) return;
							createDuelGui(p1, duel, true, timer);
							createDuelGui(p2, duel, true, timer);
						} else {
							duel.startDuel();
							this.cancel();
						}

						timer--;
					}
				}.runTaskTimer(plugin, 0L, 20);
			}

			if (e.getEvent().getRawSlot() >= 54) {
				if (duel.getDueler1().equals(p)) {
					ItemStack wagerItem = p.getInventory().getItem(e.getSlot());
					if (wagerItem == null || wagerItem.isSimilar(new ItemStack(Material.AIR))) return;

					duel.getWager1().add(wagerItem);
					p.getInventory().removeItem(wagerItem);

					createDuelGui(duel.getDueler1(), duel, false, 0);
					createDuelGui(duel.getDueler2(), duel, false, 0);
				} else if (duel.getDueler2().equals(p)) {
					ItemStack wagerItem = p.getInventory().getItem(e.getSlot());
					if (wagerItem == null || wagerItem.isSimilar(new ItemStack(Material.AIR))) return;

					duel.getWager2().add(p.getInventory().getItem(e.getSlot()));
					p.getInventory().removeItem(wagerItem);

					createDuelGui(duel.getDueler1(), duel, false, 0);
					createDuelGui(duel.getDueler2(), duel, false, 0);
				}
			}

			p.updateInventory();
		});

		inventory.setPage(page);
		inventory.show(p);

		inventory.update();
	}

	public void createInventoryGui(Player p, Duel duel) {
		MeteoriteInventory inventory = new MeteoriteInventory(plugin, "§8Inventory view", 9, 5, true);
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

		page.setItem(44, Material.ARROW, "§a§lWagering");

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
