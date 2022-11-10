package me.lucidus.pathfindingnpc.commands;

import me.lucidus.pathfindingnpc.entity.NPC;
import me.lucidus.pathfindingnpc.entity.NPCManager;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.BucketItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NPCCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("heal")) {
                for (NPC npc : NPCManager.allNPCs) {
                    npc.setHealth(20);
                    npc.getFoodData().setFoodLevel(20);
                    player.sendMessage(ChatColor.GREEN + npc.getBukkitEntity().getDisplayName() + " has been healed.");
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("test")) {
                for (NPC npc : NPCManager.allNPCs) {
                    player.sendMessage(ChatColor.GOLD + "" + npc.getStringUUID() + " - UUID");
                    player.sendMessage(ChatColor.GOLD + "" + npc.isControlledByLocalInstance() + " - Controlled by local instance?");
                    player.sendMessage(ChatColor.BLUE + "" + npc.invulnerableTime + " - Invulnerable Time");
                    player.sendMessage(ChatColor.BLUE + "" + npc.invulnerableDuration + " - Invulnerable Duration");
                    player.sendMessage(ChatColor.YELLOW + "" + npc.isInvulnerableTo(DamageSource.IN_FIRE) + " - Invulnerable To Fire");
                    player.sendMessage(ChatColor.YELLOW + "" + npc.isInvulnerableTo(DamageSource.FALL) + " - Invulnerable to Fall");
                    player.sendMessage(ChatColor.YELLOW + "" + npc.isChangingDimension() + " - Changing Dimension?");
                    player.sendMessage(ChatColor.YELLOW + "" + npc.wonGame + " - Won Game");
                    player.sendMessage(ChatColor.GREEN + "" + npc.getHealth() + " - Health");
                    player.sendMessage(ChatColor.GREEN + "" + npc.getFoodData().getFoodLevel() + " - Food Level");
                    player.sendMessage(ChatColor.RED + "" + npc.getLocation() + " - Location");
                    player.sendMessage(ChatColor.RED + "" + npc.getSpeed() + " - speed");
                    player.sendMessage(ChatColor.RED + "" + npc.zza + " - W S?"); //forward = 0.98, backwards = -0.98  Actual value set is 1 or -1 and gets handled by aiStep() which then calls travel()
                    player.sendMessage(ChatColor.RED + "" + npc.xxa + " - A D?"); //left = 0.98, right = -0.98
                    player.sendMessage(ChatColor.AQUA + "" + npc.getDeltaMovement() + " - Delta Movement");
                    player.sendMessage(ChatColor.AQUA + "" + npc.isImmobile() + " - Immobile?");
                    player.sendMessage(ChatColor.AQUA + "" + npc.isEffectiveAi() + " - Effective AI?");
                    player.sendMessage(ChatColor.AQUA + "" + npc.getUseItem() + " - Use item");
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "" + npc.getBukkitEntity().getBoundingBox().getMin() + " - Bounding box Min");
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "" + npc.getBukkitEntity().getBoundingBox().getMax() + " - Bounding box Max");
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "" + npc.getBukkitEntity().getBoundingBox().getCenter() + " - Bounding box Centre");
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "" + npc.getBukkitEntity().getBoundingBox().getWidthX() + " - X " + npc.getBukkitEntity().getBoundingBox().getWidthZ() + " - Z " + " - Bounding box Width");
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("look")) {
                for (NPC npc : NPCManager.allNPCs) {
                    Location lookLoc = player.getEyeLocation();
                    npc.lookAt(lookLoc);
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("jump")) {
                for (NPC npc : NPCManager.allNPCs) {
                    npc.doJump();
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("move")) {
                for (NPC npc : NPCManager.allNPCs) {
                    npc.getNavigator().moveTo(player.getLocation());
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("tp")) {
                Location loc = player.getLocation();
                for (NPC npc : NPCManager.allNPCs) {
                    npc.moveTo(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("attack")) {
                for (NPC npc : NPCManager.allNPCs) {
                    npc.hit(player);
                }
                return true;
            }
        }
        sender.sendMessage(ChatColor.DARK_RED + "Incorrect Usage!");
        sender.sendMessage(ChatColor.DARK_RED + "/npc " + ChatColor.GOLD + "<test> " + ChatColor.WHITE + "- Gets random info about NPC.");
        return true;
    }
}
