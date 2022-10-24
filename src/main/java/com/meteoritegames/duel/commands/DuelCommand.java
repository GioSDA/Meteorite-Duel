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
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@DefaultCommand
public class DuelCommand implements CommandClass {
	@Command(description="Invite another player to a duel",
			params="@player")
	public void duelPlayer(CommandSender sender, String[] params) {
		if (!(sender instanceof Player)) return;
//		ADD LINE MAKING SURE THEY DONT DO THEMSELVES
		Player p = (Player) sender;
		Player d = sender.getServer().getPlayer(params[0]);

		if (d == null) {
			sender.sendMessage("§cThat player is not online!");
			return;
		}

		if (Main.playerIsInDuel(d)) {
			sender.sendMessage("§cThat player is already in a duel!");
			return;
		}

		if (Main.playerIsInDuel(p)) {
			sender.sendMessage("§cYou are already in a duel!");
			return;
		}

		if (Main.getDuel(p) != null) {
			//TODO: come up with way to make it so that duel requests expire/end
			sender.sendMessage("§cYou can only have 1 duel request active at once!");
			return;
		}

		Duel duel = new Duel(p,d);

		createArgsGui(duel);
	}

	@Command(description="Toggle duel invites from other players",
			args="toggle")
	public void duelToggle() {

	}

	@Command(description="Collect your duel winnings",
			args="collect")
	public void duelCollect() {

	}

	@Command(description="Access the duel spectate GUI",
			args="spectate",
			params="@player")
	public void duelSpectate() {

	}

	@Command(description="Accept a duel invitation",
			args="accept",
			params="@player")
	public void duelAccept(CommandSender sender, String[] params) {
		if (!(sender instanceof Player)) return;
		Player p = sender.getServer().getPlayer(params[0]);

		if (p == null) {
			sender.sendMessage("§cThat player is not online!");
			return;
		}

		Duel duel = Main.getDuel(p);

		if (duel == null || duel.isActive()) {
			sender.sendMessage("§cThat duel request is not active!");
			return;
		}

		createWagerGui(duel.getDueler1(), duel, true);
		createWagerGui(duel.getDueler2(), duel, true);
	}

	private void setGuiElement(int slot, BasicInventory page, ArrayList<DuelArg> duelArgs) {
		ItemStack item = new ItemStack(duelArgs.get(slot).getMaterial());
		ItemMeta meta = item.getItemMeta();
		if (duelArgs.get(slot).isEnabled()) {
			meta.addEnchant(Enchantment.DIG_SPEED, 1,true);
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

		MeteoriteInventory inventory = new MeteoriteInventory(Main.plugin, "Duel Settings", 9, 3, true);
		BasicInventory page = new BasicInventory(9, 3);
		page.fill(Material.STAINED_GLASS_PANE);

		for (int i = 0; i < duelArgs.size(); i++) {
			setGuiElement(i, page, duelArgs);
		}

		page.setItem(26, Material.ARROW, "§a§lContinue to map select");

		page.setOnSlotClickListener(e -> {
			if (e.getEvent().getSlotType().equals(InventoryType.SlotType.OUTSIDE)) return;

			if (e.getEvent().getRawSlot() == 26) {
				duel.getDueler1().closeInventory();
				createMapGui(duel);
			}

			if (duelArgs.size() <= e.getEvent().getRawSlot()) return;
			duelArgs.get(e.getEvent().getRawSlot()).setEnabled(!duelArgs.get(e.getEvent().getRawSlot()).isEnabled());

			setGuiElement(e.getEvent().getRawSlot(), page, duelArgs);

			inventory.applyPage(page);
			inventory.show(duel.getDueler1());
		});

		inventory.applyPage(page);

		inventory.show(duel.getDueler1());
	}

	private void createMapGui(Duel duel) {
		ArrayList<DuelMap> maps = Main.getMaps();

		MeteoriteInventory inventory = new MeteoriteInventory(Main.plugin, "Map Settings", 9, 3, true);
		BasicInventory page = new BasicInventory(9, 3);
		page.fill(Material.STAINED_GLASS_PANE);

		for (int i = 0; i < maps.size(); i++) {
			ItemStack item = new ItemStack(maps.get(i).getIcon());
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', maps.get(i).getName()));
			if (!maps.get(i).isActive()) meta.setLore(Collections.singletonList("§4§lThis map is not currently available!"));

			item.setItemMeta(meta);

			page.setItem(i, item);
		}

		page.setOnSlotClickListener(e -> {
			if (e.getEvent().getSlotType().equals(InventoryType.SlotType.OUTSIDE)) return;

			if (maps.size() <= e.getEvent().getRawSlot()) return;
			if (!maps.get(e.getEvent().getRawSlot()).isActive()) {
				e.getEvent().getWhoClicked().sendMessage("§cThat map is currently unavailable!");
				return;
			}

			duel.setMap(maps.get(e.getEvent().getRawSlot()));
			duel.getDueler1().closeInventory();

			BaseComponent[] message =
					new ComponentBuilder(duel.getDueler1().getName()).color(ChatColor.BLUE)
							.append(" has invited you to a duel!").color(ChatColor.AQUA)
							.append(" Click here to accept.").color(ChatColor.GOLD).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel accept " + duel.getDueler1().getName())).create();

			duel.getDueler2().spigot().sendMessage(message);

			e.getEvent().getWhoClicked().sendMessage("§eYour duel request has been sent.");
			Main.addDuel(duel);
			Main.mapActive(e.getEvent().getRawSlot(), false);
		});

		inventory.applyPage(page);
		inventory.show(duel.getDueler1());
	}

	public void createInventoryGui(Player p, Duel duel) {
		MeteoriteInventory inventory = new MeteoriteInventory(Main.plugin, "Inventory view", 9, 5, true);
		BasicInventory page = new BasicInventory(9, 5);
		page.setItem(36, new ItemStack(Material.STAINED_GLASS_PANE));
		page.setItem(37, new ItemStack(Material.STAINED_GLASS_PANE));
		page.setItem(40, new ItemStack(Material.STAINED_GLASS_PANE));
		page.setItem(43, new ItemStack(Material.STAINED_GLASS_PANE));
		page.setItem(44, new ItemStack(Material.STAINED_GLASS_PANE));

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

		page.setItem(44, new ItemStack(Material.ARROW));

		page.setOnSlotClickListener(e -> {
			if (e.getEvent().getSlotType().equals(InventoryType.SlotType.OUTSIDE)) return;

			if (e.getEvent().getRawSlot() == 44) {
				e.getEvent().getWhoClicked().closeInventory();
				createWagerGui((Player) e.getEvent().getWhoClicked(), duel, true);
			}
		});

		inventory.applyPage(page);
		inventory.show(p);
	}

	public void createWagerGui(Player p, Duel duel, boolean forced) {
		if (p.getOpenInventory().getTitle().equals("Inventory view") && !forced) return;

		MeteoriteInventory inventory = new MeteoriteInventory(Main.plugin, "Wagering", 9, 3, true);
		BasicInventory page = new BasicInventory(9, 3);
		page.setItem(4, new ItemStack(Material.STAINED_GLASS_PANE));
		page.setItem(13, new ItemStack(Material.STAINED_GLASS_PANE));
		page.setItem(22, new ItemStack(Material.STAINED_GLASS_PANE));

		page.setItem(26, new ItemStack(Material.ARROW));

		for (int i = 0; i < 12; i++) {
			if (i >= duel.getWager1().size()) continue;
			page.setItem(i % 4 + ((i / 4)*9), duel.getWager1().get(i));
		}

		for (int i = 0; i < 12; i++) {
			if (i >= duel.getWager1().size()) continue;
			page.setItem((i % 4 + ((i / 4)*9))+5, duel.getWager1().get(i));
		}

		page.setOnSlotClickListener(e -> {
			if (e.getEvent().getSlotType().equals(InventoryType.SlotType.OUTSIDE)) return;

			if (e.getEvent().getRawSlot() == 26) {
				e.getEvent().getWhoClicked().closeInventory();
				createInventoryGui((Player) e.getEvent().getWhoClicked(), duel);
			}

			if (duel.getDueler1().equals(p)) {
				if (e.getSlotX() < 4 && e.getEvent().getRawSlot() < 27 && e.getInventory().getInventory().getItem(e.getSlot()) != null && !e.getInventory().getInventory().getItem(e.getSlot()).equals(new ItemStack(Material.AIR))) {
					int index = e.getEvent().getRawSlot() - (e.getSlotY()*9);
					if (index > duel.getWager1().size()) return;

					duel.getDueler1().getInventory().addItem(duel.getWager1().get(index));
					duel.getWager1().remove(index);

					createWagerGui(duel.getDueler1(), duel, false);
					createWagerGui(duel.getDueler2(), duel, false);
				}
			} else if (duel.getDueler2().equals(p)) {
				if (e.getSlotX() > 4 && e.getEvent().getRawSlot() < 27 && e.getInventory().getInventory().getItem(e.getSlot()) != null && !e.getInventory().getInventory().getItem(e.getSlot()).equals(new ItemStack(Material.AIR))) {
					int index = e.getEvent().getRawSlot() - (e.getSlotY()*9) - 5;
					if (index > duel.getWager2().size()) return;

					duel.getDueler2().getInventory().addItem(duel.getWager2().get(index));
					duel.getWager2().remove(index);

					createWagerGui(duel.getDueler1(), duel, false);
					createWagerGui(duel.getDueler2(), duel, false);
				}
			}

			if (e.getEvent().getRawSlot() > 27) {
				if (duel.getDueler1().equals(p)) {
					duel.getWager1().add(p.getInventory().getItem(e.getSlot()));
					p.getInventory().remove(p.getInventory().getItem(e.getSlot()));

					createWagerGui(duel.getDueler1(), duel, false);
					createWagerGui(duel.getDueler2(), duel, false);
				} else if (duel.getDueler2().equals(p)) {
					duel.getWager2().add(p.getInventory().getItem(e.getSlot()));
					p.getInventory().remove(p.getInventory().getItem(e.getSlot()));

					createWagerGui(duel.getDueler1(), duel, false);
					createWagerGui(duel.getDueler2(), duel, false);
				}
			}

			p.updateInventory();
		});

		inventory.setPage(page);
		inventory.show(p);

		inventory.update();
	}
}
