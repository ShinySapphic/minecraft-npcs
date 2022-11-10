package me.lucidus.pathfindingnpc.commands;

import me.lucidus.pathfindingnpc.entity.NPCManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NPCSpawnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player) || !sender.hasPermission("npc.spawn")) {
            sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command!");
            return true;
        }
        Player player = (Player) sender;

        switch (args.length) {

            case 0:
                NPCManager.generateNPC("FurBot", player.getLocation());
                return true;

            case 1:
                NPCManager.generateNPC(args[0], player.getLocation());
                return true;

            default:
                sender.sendMessage(ChatColor.DARK_RED + "Incorrect Usage!");
                sender.sendMessage(ChatColor.DARK_RED + "/npcspawn " + ChatColor.GOLD + "<name> " + ChatColor.WHITE + "- Spawn an NPC.");
                return true;
        }
    }
}
