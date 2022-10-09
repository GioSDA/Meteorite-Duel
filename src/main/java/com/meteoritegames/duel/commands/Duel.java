package com.meteoritegames.duel.commands;

import com.avaje.ebean.Page;
import com.meteoritegames.duel.Main;
import com.meteoritegames.duel.objects.DuelArg;
import com.meteoritegames.duel.objects.DuelObject;
import com.meteoritepvp.api.command.Command;
import com.meteoritepvp.api.command.CommandClass;
import com.meteoritepvp.api.command.DefaultCommand;
import com.meteoritepvp.api.inventory.MeteoriteInventory;
import com.meteoritepvp.api.inventory.presets.BasicInventory;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@DefaultCommand
public class Duel implements CommandClass {

	@Command(description="Invite another player to a duel",
			params="@player")
	public void duelPlayer(CommandSender sender, String[] params) {
		if (!(sender instanceof Player)) return;

		Player p = (Player) sender;
		Player d = sender.getServer().getPlayer(params[0]);

		if (d == null) {
			sender.sendMessage("§cThat player is not online!");
			return;
		}

		createDuelGui(p,d);
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

	private void createDuelGui(Player p, Player d) {
		DuelObject duel = new DuelObject(p, d);
		ArrayList<DuelArg> duelArgs = duel.getDuelArgs();

		MeteoriteInventory inventory = new MeteoriteInventory(Main.plugin, "Duel Settings", 9, 3, true);
		BasicInventory page = new BasicInventory(9, 3);
		page.fill(Material.STAINED_GLASS_PANE);

		for (int i = 0; i < 15; i++) {
			setGuiElement(i, page, duelArgs);
		}

		page.setOnSlotClickListener(e -> {
			duelArgs.get(e.getSlot()).setEnabled(!duelArgs.get(e.getSlot()).isEnabled());

			setGuiElement(e.getSlot(), page, duelArgs);

			inventory.applyPage(page);
			inventory.show(p);
		});

		inventory.applyPage(page);

		inventory.show(p);
	}
}
