package me.lucidus.pathfindingnpc.entity.ai.goals;

import me.lucidus.pathfindingnpc.entity.PathfindingNPC;
import me.lucidus.pathfindingnpc.util.Util;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class FireExtinguishGoal extends Goal {

    protected final PathfindingNPC npc;

    public FireExtinguishGoal(PathfindingNPC npc) {
        this.npc = npc;
    }

    public boolean canUse() {
        if (npc.getLastDamageSource() == null)
            return false;
        return npc.getLastDamageSource().isFire();
    }

    public void start() {
        if (npc.isOnFire() && (npc.getPlayerInventory().contains(Material.WATER_BUCKET) || npc.getPlayerInventory().getItemInOffHand().getType() == Material.WATER_BUCKET)) {
            Location lookLoc = npc.getLocation().subtract(0, 1, 0);

            for (Block blocks : Util.getPossibleStandingBlocks(npc.getBukkitEntity())) {
                lookLoc = blocks.getLocation();
                break;
            }
            npc.lookAt(lookLoc);

            int bucketSlot = npc.getPlayerInventory().first(Material.WATER_BUCKET);

            if (bucketSlot == -1) {
                if (npc.getPlayerInventory().getItemInOffHand().getType() != Material.WATER_BUCKET)
                    return;
                bucketSlot = 40;
            }

            npc.swapItemsFromInv(0, bucketSlot);
            npc.useItem(false, true);
        }
    }

    public void stop() {
        if (npc.wasOnFire)
            npc.useItem(false, true);
    }

    public void tick() {
        for (Block blocks : Util.getPossibleStandingBlocks(npc.getBukkitEntity())) {
            if (blocks.getType() != Material.FIRE)
                continue;
            npc.breakBlock(blocks);
        }
    }
}
