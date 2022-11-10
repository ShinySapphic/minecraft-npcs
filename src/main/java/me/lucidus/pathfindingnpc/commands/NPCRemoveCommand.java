package me.lucidus.pathfindingnpc.commands;

import me.lucidus.pathfindingnpc.entity.NPC;
import me.lucidus.pathfindingnpc.entity.NPCManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Iterator;

public class NPCRemoveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("npc.remove")) {
            sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command!");
            return true;
        }
        switch (args.length) {

            case 0:
                NPCManager.removeAll(true);
                return true;

            case 1:
                Iterator<NPC> i = NPCManager.allNPCs.iterator();
                while (i.hasNext()) {
                    NPC npc = i.next();
                    if (!npc.getBukkitEntity().getDisplayName().equals(args[1]))
                        continue;

                    npc.remove();
                    i.remove();
                    NPCManager.removeData(npc);
                }

            default:
                sender.sendMessage(ChatColor.DARK_RED + "Incorrect Usage!");
                sender.sendMessage(ChatColor.DARK_RED + "/npcremove " + ChatColor.GOLD + "<name> " + ChatColor.WHITE + "- Remove all NPCs with specified name.");
                return true;
        }
    }
}
