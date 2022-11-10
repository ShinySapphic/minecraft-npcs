package me.lucidus.pathfindingnpc.commands;

import me.lucidus.pathfindingnpc.entity.NPC;
import me.lucidus.pathfindingnpc.entity.NPCManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NPCBlockCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player) || !sender.hasPermission("npc.block")) {
            sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command!");
            return true;
        }
        Player player = (Player) sender;

        switch (args.length) {

            case 4:
                if (args[0].equalsIgnoreCase("place")) {
                    for (NPC npc : NPCManager.allNPCs) {
                        Location loc = locationFromArgs(player, args[1], args[2], args[3]);

                        if (loc == null)
                            return true;

                        //npc.placeBlock(loc, Material.DIAMOND_BLOCK);
                        npc.useItemOn(loc, false);
                    }
                    return true;
                }

                if (args[0].equalsIgnoreCase("break")) {
                    for (NPC npc : NPCManager.allNPCs) {
                        Location loc = locationFromArgs(player, args[1], args[2], args[3]);

                        if (loc == null)
                            return true;

                        npc.breakBlock(loc.getBlock());
                    }
                    return true;
                }

            case 5:
                if (args[0].equalsIgnoreCase("place")) {
                    for (NPC npc : NPCManager.allNPCs) {
                        Location loc = locationFromArgs(player, args[2], args[3], args[4]);
                        if (loc == null)
                            return true;

                        Material material;
                        try {
                            material = Material.valueOf(args[1]);
                        } catch (IllegalArgumentException e) {
                            player.sendMessage(ChatColor.DARK_RED + "Material does not exist.");
                            return true;
                        }

                        npc.placeBlock(loc, material);
                    }
                    return true;
                }

                if (args[0].equalsIgnoreCase("break")) {
                    for (NPC npc : NPCManager.allNPCs) {
                        Location loc = locationFromArgs(player, args[2], args[3], args[4]);

                        if (loc == null)
                            return true;

                        Material material;
                        try {
                            material = Material.valueOf(args[1]);
                        } catch (IllegalArgumentException e) {
                            player.sendMessage(ChatColor.DARK_RED + "Material does not exist.");
                            return true;
                        }

                        npc.getBukkitEntity().getInventory().setItemInMainHand(new ItemStack(material));
                        npc.breakBlock(loc.getBlock());
                    }
                    return true;
                }

            default:
                sender.sendMessage(ChatColor.DARK_RED + "Incorrect Usage!");
                sender.sendMessage(ChatColor.DARK_RED + "/npcblock " + ChatColor.GOLD + "<place> " + ChatColor.WHITE + "- Have NPC place a block.");
                sender.sendMessage(ChatColor.DARK_RED + "/npcblock " + ChatColor.GOLD + "<break> " + ChatColor.WHITE + "- Have NPC break a block.");
                return true;
        }
    }

    private Location locationFromArgs(Player player, String x, String y, String z) {
        try {
            double locX = Double.parseDouble(x);
            double locY = Double.parseDouble(y);
            double locZ = Double.parseDouble(z);
            return new Location(player.getWorld(), locX, locY, locZ);

        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.DARK_RED + "Arguments are invalid. Make sure they're numbers!");
            return null;
        }
    }
}