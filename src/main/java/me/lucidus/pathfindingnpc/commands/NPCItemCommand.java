package me.lucidus.pathfindingnpc.commands;

import me.lucidus.pathfindingnpc.entity.NPC;
import me.lucidus.pathfindingnpc.entity.NPCManager;
import me.lucidus.pathfindingnpc.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NPCItemCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player) || !sender.hasPermission("npc.item")) {
            sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 1 || args.length == 2) {
            for (NPC npc : NPCManager.allNPCs) {
                if (args[0].equalsIgnoreCase("use"))
                    npc.useItem(args.length == 2 && args[1].equalsIgnoreCase("off"), false);

                if (args[0].equalsIgnoreCase("useon")) {
                    Block block = player.getTargetBlockExact(10);
                    if (block == null)
                        return true;
                    npc.useItemOn(Util.getMidLocFromBlock(block), args.length == 2 && args[1].equalsIgnoreCase("off"));
                }

                if (args[0].equalsIgnoreCase("release"))
                    npc.releaseUsingItem();
                return true;
            }
        }

        sender.sendMessage(ChatColor.DARK_RED + "Incorrect Usage!");
        sender.sendMessage(ChatColor.DARK_RED + "/npcitem " + ChatColor.GOLD + "<use> " + ChatColor.WHITE + "- Make NPC use specified item.");
        sender.sendMessage(ChatColor.DARK_RED + "/npcitem " + ChatColor.GOLD + "<release> " + ChatColor.WHITE + "- Make NPC release specified item.");
        return true;
    }
}
