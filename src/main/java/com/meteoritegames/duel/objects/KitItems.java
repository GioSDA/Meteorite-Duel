package com.meteoritegames.duel.objects;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class KitItems {
	public KitItems() {

	}

	public ItemStack[] getSoupItems() {
		ItemStack sword = new ItemStack(Material.IRON_SWORD);
		sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
		sword.addUnsafeEnchantment(Enchantment.DURABILITY, 3);

		ItemStack helmet = new ItemStack(Material.IRON_HELMET);
		sword.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		sword.addUnsafeEnchantment(Enchantment.DURABILITY, 3);

		ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
		sword.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		sword.addUnsafeEnchantment(Enchantment.DURABILITY, 3);

		ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
		sword.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		sword.addUnsafeEnchantment(Enchantment.DURABILITY, 3);

		ItemStack boots = new ItemStack(Material.IRON_BOOTS);
		sword.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		sword.addUnsafeEnchantment(Enchantment.DURABILITY, 3);

		ItemStack bow = new ItemStack(Material.BOW);
		sword.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 5);
		sword.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);

		ItemStack arrow = new ItemStack(Material.ARROW);

		ItemStack speed = new ItemStack(Material.POTION, 1, (short)8226);

		ItemStack strength = new ItemStack(Material.POTION, 1, (short)8233);

		ItemStack soup = new ItemStack(Material.MUSHROOM_SOUP);

		ItemStack[] items = new ItemStack[]{sword, helmet, chestplate, leggings, boots, bow, arrow, speed, speed,
											speed, strength, strength, strength, soup, soup, soup, soup, soup,
											soup, soup, soup, soup, soup, soup, soup, soup, soup,
											soup, soup, soup, soup, soup, soup, soup, soup, soup};

		return addDuelLore(items);
	}

	public ItemStack[] getPotionItems() {
		ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
		sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
		sword.addUnsafeEnchantment(Enchantment.DURABILITY, 3);

		ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
		sword.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		sword.addUnsafeEnchantment(Enchantment.DURABILITY, 3);

		ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
		sword.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		sword.addUnsafeEnchantment(Enchantment.DURABILITY, 3);

		ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
		sword.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		sword.addUnsafeEnchantment(Enchantment.DURABILITY, 3);

		ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
		sword.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		sword.addUnsafeEnchantment(Enchantment.DURABILITY, 3);

		ItemStack gapples = new ItemStack(Material.GOLDEN_APPLE, 32);

		ItemStack carrots = new ItemStack(Material.GOLDEN_CARROT, 64);

		ItemStack bow = new ItemStack(Material.BOW);
		sword.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 5);
		sword.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);

		ItemStack arrow = new ItemStack(Material.ARROW);

		ItemStack regen = new ItemStack(Material.POTION, 1, (short)8225);

		ItemStack strength = new ItemStack(Material.POTION, 1, (short)8233);

		ItemStack speed = new ItemStack(Material.POTION, 1, (short)8226);

		ItemStack poison = new ItemStack(Material.POTION, 1, (short)16420);

		ItemStack health = new ItemStack(Material.POTION, 1, (short)16421);

		ItemStack[] items = new ItemStack[]{sword, helmet, chestplate, leggings, boots, gapples, carrots, bow, arrow,
				regen, speed, speed, speed, strength, strength, strength, poison, poison,
				poison, poison, poison, poison, health, health, health, health, health,
				health, health, health, health, health, health, health, health, health};

		return addDuelLore(items);
	}

	public ItemStack[] getNoDebuffItems() {
		ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
		sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
		sword.addUnsafeEnchantment(Enchantment.DURABILITY, 3);

		ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
		sword.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		sword.addUnsafeEnchantment(Enchantment.DURABILITY, 3);

		ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
		sword.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		sword.addUnsafeEnchantment(Enchantment.DURABILITY, 3);

		ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
		sword.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		sword.addUnsafeEnchantment(Enchantment.DURABILITY, 3);

		ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
		sword.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		sword.addUnsafeEnchantment(Enchantment.DURABILITY, 3);

		ItemStack gapples = new ItemStack(Material.GOLDEN_APPLE, 32);

		ItemStack carrots = new ItemStack(Material.GOLDEN_CARROT, 64);

		ItemStack bow = new ItemStack(Material.BOW);
		sword.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 5);
		sword.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);

		ItemStack arrow = new ItemStack(Material.ARROW);

		ItemStack regen = new ItemStack(Material.POTION, 1, (short)8225);

		ItemStack strength = new ItemStack(Material.POTION, 1, (short)8233);

		ItemStack speed = new ItemStack(Material.POTION, 1, (short)8226);

		ItemStack health = new ItemStack(Material.POTION, 1, (short)16421);

		ItemStack[] items = new ItemStack[]{sword, helmet, chestplate, leggings, boots, gapples, carrots, bow, arrow,
				regen, speed, speed, speed, strength, strength, strength, health, health,
				health, health, health, health, health, health, health, health, health,
				health, health, health, health, health, health, health, health, health};

		return addDuelLore(items);
	}

	private ItemStack[] addDuelLore(ItemStack[] items) {
		ItemStack[] modifiedItems = items.clone();

		for (int i = 0; i < items.length; i++) {
			ItemStack item = modifiedItems[i];
			ItemMeta meta = item.getItemMeta();
			List<String> lore;
			if (meta.hasLore()) lore = meta.getLore();
			else lore = new ArrayList<>();

			lore.add("§6§lDUEL KIT ITEM");
			meta.setLore(lore);
			item.setItemMeta(meta);
			modifiedItems[i] = item;
		}

		return modifiedItems;
	}
}
