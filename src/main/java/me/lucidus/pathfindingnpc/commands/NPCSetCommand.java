package me.lucidus.pathfindingnpc.commands;

import me.lucidus.pathfindingnpc.entity.NPC;
import me.lucidus.pathfindingnpc.entity.NPCManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class NPCSetCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("npc.set")) {
            sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length == 2) {
            float value;

            try {
                value = Float.parseFloat(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.DARK_RED + "Value must be a number");
                return true;
            }

            for (NPC npc : NPCManager.allNPCs) {

                if (args[0].equalsIgnoreCase("health")) {
                    npc.setHealth(value);
                }

                if (args[0].equalsIgnoreCase("food")) {
                    npc.getFoodData().setFoodLevel(Math.round(value));
                }

            }
        }
        return false;
    }
}
