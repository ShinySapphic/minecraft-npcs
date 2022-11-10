package me.lucidus.pathfindingnpc.entity.ai.goals;

import me.lucidus.pathfindingnpc.entity.PathfindingNPC;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.bukkit.Location;
import org.bukkit.event.entity.EntityTargetEvent;

public class FollowTargetGoal extends Goal {

    protected final PathfindingNPC npc;
    protected final TargetingConditions lookAtContext;
    protected final double distance;
    protected Entity target;
    protected Location targetLocation;

    public FollowTargetGoal(PathfindingNPC npc, double distance) { //Want to have a min and max distance so they aren't constantly ticking location even if they've found it
        this.npc = npc;
        this.distance = distance;
        this.lookAtContext = TargetingConditions.forNonCombat().range(distance).selector((var1x) -> EntitySelector.notRiding(npc).test(var1x));
    }

    public boolean canUse() {
        this.target = this.npc.level.getNearestPlayer(this.lookAtContext, this.npc, this.npc.getX(), this.npc.getEyeY(), this.npc.getZ());
        return target != null;
    }

    public void start() {
        this.npc.setGoalTarget((LivingEntity) target, target instanceof ServerPlayer ? EntityTargetEvent.TargetReason.CLOSEST_ENTITY : EntityTargetEvent.TargetReason.CLOSEST_ENTITY, true);
    }

    public void stop() {
        this.npc.setGoalTarget(null, EntityTargetEvent.TargetReason.FORGOT_TARGET, true);
        this.target = null;
    }

    public void tick() {
        targetLocation = target.getBukkitEntity().getLocation();
        //Fine in survival, but location gets wrong block if player instantly teleports through portal (creative mode)
        if (targetLocation.getWorld().getEnvironment().equals(npc.getLocation().getWorld().getEnvironment()))
            npc.getNavigator().moveTo(targetLocation);
    }
}
