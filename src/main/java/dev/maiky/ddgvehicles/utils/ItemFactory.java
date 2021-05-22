package dev.maiky.ddgvehicles.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemFactory {

    public ItemStack edit(ItemStack stack, String displayName){
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        stack.setItemMeta(meta);
        return stack;
    }

    public ItemStack edit(ItemStack stack, String displayName, List<String> lore){
        ItemMeta meta = stack.getItemMeta();

        if (displayName != null)
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));

        List<String> lore2 = new ArrayList<>();
        for (String s : lore){
            lore2.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(lore2);

        stack.setItemMeta(meta);
        return stack;
    }

    public ItemStack createItem(Material material){
        return new ItemStack(material);
    }

    public ItemStack createItem(Material material, int amount){
        return new ItemStack(material, amount);
    }

    public ItemStack createItem(Material material, int amount, int damage){
        return new ItemStack(material, amount, (short)damage);
    }

    public ItemStack createItem(Material material, int amount, int damage,
                                String displayName){
        ItemStack stack = new ItemStack(material, amount, (short)damage);
        ItemMeta meta = stack.getItemMeta();
        if (displayName != null){
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        }
        stack.setItemMeta(meta);

        return stack;
    }

    public ItemStack createItem(Material material, int amount, int damage,
                                String displayName, List<String> lore){
        ItemStack stack = new ItemStack(material, amount, (short)damage);
        ItemMeta meta = stack.getItemMeta();
        if (displayName != null){
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        }

        if (lore != null){
            List<String> newLore = new ArrayList<>();
            for (String s : lore){
                newLore.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            meta.setLore(newLore);
        }
        stack.setItemMeta(meta);

        return stack;
    }

    public ItemStack createItem(Material material, int amount, int damage,
                                String displayName, List<String> lore, boolean unbreakable){
        ItemStack stack = new ItemStack(material, amount, (short)damage);
        ItemMeta meta = stack.getItemMeta();
        if (displayName != null){
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        }

        if (lore != null){
            List<String> newLore = new ArrayList<>();
            for (String s : lore){
                newLore.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            meta.setLore(newLore);
        }
        meta.setUnbreakable(unbreakable);
        stack.setItemMeta(meta);

        return stack;
    }

    public ItemStack createItem(Material material, int amount, int damage,
                                String displayName, List<String> lore, boolean unbreakable, ItemFlag... flags){
        ItemStack stack = new ItemStack(material, amount, (short)damage);
        ItemMeta meta = stack.getItemMeta();
        if (displayName != null){
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        }

        if (lore != null){
            List<String> newLore = new ArrayList<>();
            for (String s : lore){
                newLore.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            meta.setLore(newLore);
        }
        meta.setUnbreakable(unbreakable);
        meta.addItemFlags(flags);
        stack.setItemMeta(meta);

        return stack;
    }


}