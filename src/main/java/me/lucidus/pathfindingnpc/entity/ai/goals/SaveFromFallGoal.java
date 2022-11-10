package me.lucidus.pathfindingnpc.entity.ai.goals;

import me.lucidus.pathfindingnpc.entity.PathfindingNPC;
import me.lucidus.pathfindingnpc.util.Util;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class SaveFromFallGoal extends Goal {

    protected final PathfindingNPC npc;

    public SaveFromFallGoal(PathfindingNPC npc) {
        this.npc = npc;
    }

    public boolean canUse() {
        if (!npc.getLocation().getWorld().getEnvironment().equals(World.Environment.NETHER) && npc.fallDistance > 3.0f) {
            if (!npc.getPlayerInventory().contains(Material.WATER_BUCKET) && npc.getPlayerInventory().getItemInOffHand().getType() != Material.WATER_BUCKET)
                return false;

            Location npcLoc = npc.getLocation().subtract(0, 1, 0);
            npc.lookAt(npcLoc);

            Block targetBlock = npc.getBukkitEntity().getTargetBlockExact(4);
            if (targetBlock == null)
                return false;

            int prevY = 0;
            for (Block blocks : Util.getPossibleStandingBlocks(npc.getBukkitEntity())) {
                if (blocks.getY() < prevY)
                    continue;
                prevY = blocks.getY();
                npcLoc = Util.getMidLocFromBlock(blocks);
            }
            npc.lookAt(npcLoc);
            return true;
        }
        return false;
    }

    public void start() {
        int bucketSlot = npc.getPlayerInventory().first(Material.WATER_BUCKET);

        if (bucketSlot == -1) {
            if (npc.getPlayerInventory().getItemInOffHand().getType() != Material.WATER_BUCKET)
                return;
            bucketSlot = 40;
        }

        npc.swapItemsFromInv(0, bucketSlot);
        npc.useItem(false, true);
    }

    public void stop() {
        npc.useItem(false, true);
    }
}
