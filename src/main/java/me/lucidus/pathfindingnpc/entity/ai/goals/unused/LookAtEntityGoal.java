package me.lucidus.pathfindingnpc.entity.ai.goals.unused;

import me.lucidus.pathfindingnpc.entity.PathfindingNPC;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class LookAtEntityGoal extends Goal {
    protected final PathfindingNPC npc;
    protected Entity lookAt;
    protected final float lookDistance;
    private int lookTime;
    protected final float probability;
    private final boolean onlyHorizontal;
    protected final Class<? extends LivingEntity> lookAtType;
    protected final TargetingConditions lookAtContext;

    public LookAtEntityGoal(PathfindingNPC var0, Class<? extends LivingEntity> var1, float var2) {
        this(var0, var1, var2, 0.02F);
    }

    public LookAtEntityGoal(PathfindingNPC var0, Class<? extends LivingEntity> var1, float var2, float var3) {
        this(var0, var1, var2, var3, false);
    }

    public LookAtEntityGoal(PathfindingNPC var0, Class<? extends LivingEntity> var1, float var2, float var3, boolean var4) {
        this.npc = var0;
        this.lookAtType = var1;
        this.lookDistance = var2;
        this.probability = var3;
        this.onlyHorizontal = var4;
        this.setFlags(EnumSet.of(Flag.LOOK));
        if (var1 == Player.class) {
            this.lookAtContext = TargetingConditions.forNonCombat().range(var2).selector((var1x) -> EntitySelector.notRiding(var0).test(var1x));
        } else {
            this.lookAtContext = TargetingConditions.forNonCombat().range(var2);
        }

    }

    public boolean canUse() {
        if (this.npc.getRandom().nextFloat() >= this.probability) {
            return false;
        } else {
            if (this.npc.getTarget() != null) {
                this.lookAt = this.npc.getTarget();
            }

            if (this.lookAtType == Player.class) {
                this.lookAt = this.npc.level.getNearestPlayer(this.lookAtContext, this.npc, this.npc.getX(), this.npc.getEyeY(), this.npc.getZ());
            } else {
                this.lookAt = this.npc.level.getNearestEntity(this.npc.level.getEntitiesOfClass(this.lookAtType, this.npc.getBoundingBox().inflate(this.lookDistance, 3.0D, this.lookDistance), (var0) -> true), this.lookAtContext, this.npc, this.npc.getX(), this.npc.getEyeY(), this.npc.getZ());
            }

            return this.lookAt != null;
        }
    }

    public boolean canContinueToUse() {
        if (!this.lookAt.isAlive()) {
            return false;
        } else if (this.npc.distanceToSqr(this.lookAt) > (double)(this.lookDistance * this.lookDistance)) {
            return false;
        } else {
            return this.lookTime > 0;
        }
    }

    public void start() {
        this.lookTime = 40 + this.npc.getRandom().nextInt(40);
    }

    public void stop() {
        this.lookAt = null;
    }

    public void tick() {
        double var0 = this.onlyHorizontal ? this.npc.getEyeY() : this.lookAt.getEyeY();
        this.npc.lookAt(lookAt.getBukkitEntity().getLocation());
        --this.lookTime;
    }
}
