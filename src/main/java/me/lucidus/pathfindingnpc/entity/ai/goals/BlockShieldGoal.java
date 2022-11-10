package me.lucidus.pathfindingnpc.entity.ai.goals;

import me.lucidus.pathfindingnpc.entity.PathfindingNPC;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;

//TODO Make bot only block if AIState == defensive/whatever it'll be called.
public class BlockShieldGoal extends Goal {

    protected final PathfindingNPC npc;

    public BlockShieldGoal(PathfindingNPC npc) {
        this.npc = npc;
    }

    public boolean canUse() {
        for (Entity entities : npc.getBukkitEntity().getNearbyEntities(5, 5, 5)) { //Maybe check if attacker is charging bow/crossbow, and block then
            if (!(entities instanceof AbstractArrow))
                continue;
            AbstractArrow arrow = (AbstractArrow) entities;
            if (arrow.isInBlock() || arrow.getShooter().equals(npc.getBukkitEntity()))
                continue;
            npc.lookAt(arrow.getLocation());
            return true;
        }
        if (npc.getTarget() == null || !(npc.getTarget() instanceof LivingEntity) || !npc.getPlayerInventory().contains(Material.SHIELD) && npc.getPlayerInventory().getItemInOffHand().getType() != Material.SHIELD)
            return false;
        LivingEntity attacker = (LivingEntity) npc.getTarget();
        return attacker.swingTime > 0; //Blocking has a slight delay before it activates. There's no way to perfectly do this without overwritting the isBlocking() method and changing cooldown but that'd be cheating
    }

    public void start() {
        int shieldSlot = npc.getPlayerInventory().first(Material.SHIELD);

        if (shieldSlot == -1) {
            if (npc.getPlayerInventory().getItemInOffHand().getType() != Material.SHIELD)
                return;
            shieldSlot = 40;
        }
        if (shieldSlot != 0 && shieldSlot != 40)
            npc.swapItemsFromInv(40, shieldSlot);

        boolean offHand = shieldSlot == 40;
        npc.useItem(offHand, false); Bukkit.broadcastMessage(ChatColor.GREEN + "BLOCKSHIELD USE");
    }

    public void stop() {
        if (npc.isBlocking()) {
            npc.releaseUsingItem(); Bukkit.broadcastMessage(ChatColor.RED + "BLOCKSHIELD RELEASE");
        }
    }
}
