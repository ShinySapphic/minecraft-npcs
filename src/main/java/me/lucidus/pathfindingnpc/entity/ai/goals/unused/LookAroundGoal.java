package me.lucidus.pathfindingnpc.entity.ai.goals.unused;

import me.lucidus.pathfindingnpc.entity.PathfindingNPC;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class LookAroundGoal extends Goal {
    private final PathfindingNPC npc;
    private double relX;
    private double relZ;
    private int lookTime;

    public LookAroundGoal(PathfindingNPC var0) {
        this.npc = var0;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        return this.npc.getRandom().nextFloat() < 0.02F;
    }

    public boolean canContinueToUse() {
        return this.lookTime >= 0;
    }

    public void start() {
        double var0 = 6.283185307179586D * this.npc.getRandom().nextDouble();
        this.relX = Math.cos(var0);
        this.relZ = Math.sin(var0);
        this.lookTime = 20 + this.npc.getRandom().nextInt(20);
    }

    public void tick() {
        --this.lookTime;
        //this.npc.getLookControl().setLookAt(this.npc.getX() + this.relX, this.npc.getEyeY(), this.npc.getZ() + this.relZ);
    }
}
