package me.lucidus.pathfindingnpc.entity.ai.goals;

import me.lucidus.pathfindingnpc.entity.PathfindingNPC;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityTargetEvent;

//Entire class is just a test. Don't keep any of this garbage lol

public class AttackLivingEntityGoal extends Goal {

    protected final PathfindingNPC npc;
    protected final Class<? extends LivingEntity> targetType;
    protected final double distance;
    protected final TargetingConditions targetContext;

    private LivingEntity target;

    public AttackLivingEntityGoal(PathfindingNPC npc, Class<? extends LivingEntity> targetType, double distance) {
        this.npc = npc;
        this.targetType = targetType;
        this.distance = distance;

        if (targetType == Player.class) {
            this.targetContext = TargetingConditions.forNonCombat().range(distance).selector((var1x) -> EntitySelector.notRiding(npc).test(var1x));
        } else {
            this.targetContext = TargetingConditions.forNonCombat().range(distance);
        }
    }

    public boolean canUse() {
        if (this.npc.getTarget() != null && this.npc.getTarget() instanceof LivingEntity) {
            this.target = (LivingEntity) this.npc.getTarget();

            if (!target.isAlive())
                target = null;
        } else {
            if (this.targetType == Player.class) {
                this.target = this.npc.level.getNearestPlayer(this.targetContext, this.npc, this.npc.getX(), this.npc.getEyeY(), this.npc.getZ());

            } else {
                this.target = this.npc.level.getNearestEntity(this.npc.level.getEntitiesOfClass(this.targetType, this.npc.getBoundingBox().inflate(this.distance, 3.0D, this.distance), (var0) -> true), this.targetContext, this.npc, this.npc.getX(), this.npc.getEyeY(), this.npc.getZ());
            }
        }

        if (target != null) {
            if (target instanceof Player) {
                if (((Player) target).getBukkitEntity().getGameMode() == GameMode.CREATIVE || (((Player) target).getBukkitEntity().getGameMode() == GameMode.SPECTATOR)) {
                    target = null;
                }
            }
        }
        return target != null;
    }

    public void start() {
        this.npc.setGoalTarget(target, target instanceof Player ? EntityTargetEvent.TargetReason.CLOSEST_PLAYER : EntityTargetEvent.TargetReason.CLOSEST_ENTITY, true);
    }

    public void stop() {
        this.npc.setGoalTarget(null, EntityTargetEvent.TargetReason.FORGOT_TARGET, true);
        target = null;
    }

    public void tick() {
        if (npc.getLocation().distanceSquared(target.getBukkitEntity().getLocation()) < 4) {
            if (target.isBlocking()) {
                if (!npc.getPlayerInventory().getItemInMainHand().getType().toString().contains("_AXE"))
                    for (Material axeMat : Material.values()) { //TODO pick best axe, tho it won't matter in this case
                        if (!axeMat.toString().contains("_AXE"))
                            continue;
                        if (!npc.getPlayerInventory().contains(axeMat) && npc.getPlayerInventory().getItemInOffHand().getType() != axeMat)
                            continue;
                        npc.swapItemsFromInv(0, npc.getPlayerInventory().first(axeMat));
                        break;
                    }
                npc.hit(target.getBukkitEntity());
            }
            npc.doJump();
            if (npc.fallDistance > 0) //Makes bot crit target
                npc.hit(target.getBukkitEntity());
        }
        else
            this.npc.getNavigator().moveTo(target.getBukkitEntity().getLocation());
    }
}
