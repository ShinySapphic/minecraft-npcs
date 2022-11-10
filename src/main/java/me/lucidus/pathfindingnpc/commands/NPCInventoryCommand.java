package me.lucidus.pathfindingnpc.commands;

import me.lucidus.pathfindingnpc.entity.NPC;
import me.lucidus.pathfindingnpc.entity.NPCManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class NPCInventoryCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("npc.inv")) {
            sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command!");
            return true;
        }

        if (args.length == 1) {
            for (NPC npc : NPCManager.allNPCs) {
                if (args[0].equalsIgnoreCase("drop"))
                    npc.getInventory().dropAll();

                if (args[0].equalsIgnoreCase("info"))
                    Bukkit.broadcastMessage(npc.getInventory().getContents() + " - INV CONTENTS");

                if (args[0].equalsIgnoreCase("clear"))
                    npc.getInventory().clearContent();
            }
            return true;
        }

        if (args.length == 2 || args.length == 3) {
            for (NPC npc : NPCManager.allNPCs) {
                if (args[0].equalsIgnoreCase("set")) {
                    Material material;
                    try {
                        material = Material.valueOf(args[1]);
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(ChatColor.DARK_RED + "Material does not exist.");
                        return true;
                    }
                    PlayerInventory inv = npc.getPlayerInventory();
                    ItemStack item;

                    if (inv.contains(material))
                        item = inv.getItem(inv.first(material));
                    else {
                        item = new ItemStack(material);
                        inv.addItem(item);
                    }

                    if (args.length == 2) {
                        if (inv.firstEmpty() == -1) {
                            inv.setItemInMainHand(item);
                            return true;
                        }
                        npc.swapItemsFromInv(0, inv.first(item));

                    } else if (args[2].equalsIgnoreCase("off")) {
                        if (inv.firstEmpty() == -1) {
                            inv.setItemInOffHand(item);
                            return true;
                        }
                        npc.swapItemsFromInv(40, inv.first(item));
                    }
                }
            }
            return true;
        }
        sender.sendMessage(ChatColor.DARK_RED + "Incorrect Usage!");
        sender.sendMessage(ChatColor.DARK_RED + "/npcinv " + ChatColor.GOLD + "<drop> " + ChatColor.WHITE + "- Drop all of NPC's items.");
        return true;
    }
}
