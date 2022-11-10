package me.lucidus.pathfindingnpc.entity;

import com.mojang.authlib.GameProfile;
import me.lucidus.pathfindingnpc.entity.ai.Navigator;
import me.lucidus.pathfindingnpc.entity.ai.goals.AttackLivingEntityGoal;
import me.lucidus.pathfindingnpc.entity.ai.goals.EquipArmourGoal;
import me.lucidus.pathfindingnpc.entity.ai.goals.FireExtinguishGoal;
import me.lucidus.pathfindingnpc.entity.ai.goals.SaveFromFallGoal;
import me.lucidus.pathfindingnpc.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_18_R2.block.CraftBlock;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;

public abstract class PathfindingNPC extends ServerPlayer {

    private final GoalSelector goalSelector;
    private final GoalSelector targetSelector;
    private final Navigator navigator;

    private int destroyTick;
    private CraftBlock targetBlock;
    private net.minecraft.world.entity.LivingEntity target;

    protected PathfindingNPC(MinecraftServer minecraftserver, ServerLevel worldserver, GameProfile gameprofile) {
        super(minecraftserver, worldserver, gameprofile);
        this.goalSelector = new GoalSelector(worldserver.getProfilerSupplier());
        this.targetSelector = new GoalSelector(worldserver.getProfilerSupplier());
        this.navigator = new Navigator(this);

        if (worldserver != null && !worldserver.isClientSide) {
            this.initPathfinderGoals();
        }
        this.maxUpStep = 0.6F;
        this.wonGame = true;
    }

    protected void initPathfinderGoals() {
       // goalSelector.addGoal(8, new LookAtEntityGoal(this, Player.class, 8.0F, 1.0F));
        //goalSelector.addGoal(8, new LookAroundGoal(this));
        goalSelector.addGoal(0, new SaveFromFallGoal(this));
        goalSelector.addGoal(1, new FireExtinguishGoal(this));
        goalSelector.addGoal(2, new EquipArmourGoal(this));
        //goalSelector.addGoal(2, new BlockShieldGoal(this));
        this.initTargetGoals();
    }

    //Naming this method n() like mojang mappings makes it trigger on /reload after closing ui
    protected void initTargetGoals() {
        targetSelector.addGoal(3, new AttackLivingEntityGoal(this, Player.class ,8.0));
        targetSelector.addGoal(4, new AttackLivingEntityGoal(this, Monster.class ,8.0));
        //targetSelector.addGoal(1, new FollowTargetGoal(this, 8.0D));
    }

    public void aiStep() {
        super.aiStep();

        if (jumping)
            setJumping(false);

        if (targetBlock != null) {
            this.swing(InteractionHand.MAIN_HAND);
            org.bukkit.block.Block lookBlock = this.getBukkitEntity().getTargetBlockExact(6);

            double x = this.getX() - ((double)targetBlock.getX() + 0.5D);
            double y = this.getY() - ((double)targetBlock.getY() + 0.5D) + 1.5D;
            double z = this.getZ() - ((double)targetBlock.getZ() + 0.5D);
            double dist = x * x + y * y + z * z;

            if (targetBlock.getType().isAir() || dist > 36.0D || targetBlock.getY() >= this.getBlockX() + 7 || lookBlock == null || !lookBlock.equals(targetBlock)) {
                this.gameMode.handleBlockBreakAction(targetBlock.getPosition(), ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, this.getDirection(), this.getBlockY() + 7);
                targetBlock = null;
            } else {
                int multiplier = tickCount - destroyTick;
                float destroyProgress = targetBlock.getBreakSpeed(this.getBukkitEntity()) * (multiplier + 1);

                if (destroyProgress >= 1.0F) {
                    this.gameMode.handleBlockBreakAction(targetBlock.getPosition(), ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, this.getDirection(), this.getBlockY() + 7);
                }
            }
        }
    }

    protected void serverAiStep() {
        super.serverAiStep();
        this.goalSelector.tick();
        this.targetSelector.tick();
        this.navigator.tick();
    }

    //super.isImmobile() checks if npc is online player so this used to return true and break movement.
    public boolean isImmobile() {
        return this.isDeadOrDying() || this.isSleeping();
    }

    //Allows NPC to take damage after traveling dimension
    public Entity changeDimension(ServerLevel worldserver, PlayerTeleportEvent.TeleportCause cause) {
        Entity b = super.changeDimension(worldserver, cause);
        this.hasChangedDimension();
        return b;
    }

    public Entity getTarget() {
        return this.target;
    }

    public void setTarget(@Nullable net.minecraft.world.entity.LivingEntity entityliving) {
        this.setGoalTarget(entityliving, EntityTargetEvent.TargetReason.UNKNOWN, true);
    }

    public boolean setGoalTarget(net.minecraft.world.entity.LivingEntity entityliving, EntityTargetEvent.TargetReason reason, boolean fireEvent) {
        if (this.getTarget() == entityliving) {
            return false;
        } else {
            if (fireEvent) {
                if (reason == EntityTargetEvent.TargetReason.UNKNOWN && this.getTarget() != null && entityliving == null) {
                    reason = this.getTarget().isAlive() ? EntityTargetEvent.TargetReason.FORGOT_TARGET : EntityTargetEvent.TargetReason.TARGET_DIED;
                }

                if (reason == EntityTargetEvent.TargetReason.UNKNOWN) {
                    this.level.getCraftServer().getLogger().log(java.util.logging.Level.WARNING, "Unknown target reason, please report on the issue tracker", new Exception());
                }

                CraftLivingEntity ctarget = null;
                if (entityliving != null) {
                    ctarget = (CraftLivingEntity)entityliving.getBukkitEntity();
                }

                EntityTargetLivingEntityEvent event = new EntityTargetLivingEntityEvent(this.getBukkitEntity(), ctarget, reason);
                this.level.getCraftServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return false;
                }

                if (event.getTarget() != null) {
                    entityliving = ((CraftLivingEntity)event.getTarget()).getHandle();
                } else {
                    entityliving = null;
                }
            }

            this.target = entityliving;
            return true;
        }
    }

    public void hit(org.bukkit.entity.Entity entity) {
        Location entityLoc = Util.getMidLocFromEntity(entity);
        if (this.getEyeLocation().distanceSquared(entityLoc) > 11 || !this.getBukkitEntity().hasLineOfSight(entity))
            return;
        if (entity instanceof LivingEntity)
            entityLoc = ((LivingEntity) entity).getEyeLocation();

        Entity nmsEntity = ((CraftEntity) entity).getHandle();

        this.lookAt(entityLoc);

        if (this.swingTime > 0) //Weapon cooldown
            return;
        this.attack(nmsEntity);
        this.swing(InteractionHand.MAIN_HAND);
    }

    public void breakBlock(org.bukkit.block.Block block) {
        CraftBlock craftBlock = ((CraftBlock) block);

        this.lookAt(Util.getMidLocFromBlock(craftBlock));
        this.gameMode.handleBlockBreakAction(craftBlock.getPosition(), ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, this.getDirection(), this.getBlockY() + 7);
        this.destroyTick = tickCount;
        this.targetBlock = craftBlock;
    }

    //Eating food, using buckets, drawing back bows, ect...
    public void useItem(boolean useOffHand, boolean swingHand) {
        InteractionHand hand = InteractionHand.MAIN_HAND;
        if (useOffHand)
            hand = InteractionHand.OFF_HAND;

        InteractionResult result = this.gameMode.useItem(this, level, this.getItemInHand(hand), hand);

        if (result != InteractionResult.CONSUME)
            return;
        if (swingHand)
            this.swing(hand);
    }

    //Placing blocks, interacting with doors, buttons, ect...
    public void useItemOn(Location location, boolean useOffHand) {
        InteractionHand hand = InteractionHand.MAIN_HAND;
        if (useOffHand)
            hand = InteractionHand.OFF_HAND;
        InteractionResult result;

        this.lookAt(location);

        CraftBlock craftBlock = ((CraftBlock) this.getBukkitEntity().getTargetBlockExact(6));
        if (craftBlock == null)
            return;

        RayTraceResult rayTraceResult = craftBlock.rayTrace(this.getEyeLocation(), this.getEyeLocation().getDirection(), 6, FluidCollisionMode.NEVER);
        if (rayTraceResult == null) //NPC is not looking at block so return
            return;

        BlockFace hitBlockFace = rayTraceResult.getHitBlockFace();
        Direction direction = Direction.fromNormal(hitBlockFace.getModX(), hitBlockFace.getModY(), hitBlockFace.getModZ());

        BlockHitResult blockHitResult = new BlockHitResult(new Vec3(craftBlock.getX(), craftBlock.getY(), craftBlock.getZ()), direction, craftBlock.getPosition(), false);
        result = this.gameMode.useItemOn(this, level, this.getItemInHand(hand), hand, blockHitResult);

        if (result != InteractionResult.CONSUME)
            return;
        this.swing(hand); //maybe check to make sure if npc opened door or pressed button, always swing main hand
    }

    @Deprecated
    public void placeBlock(Location loc, Material blockType) { //Flint and steel, fire charge, and spawn eggs must be used here.
        placeBlock(loc, blockType, false);
    }

    @Deprecated
    public void placeBlock(Location loc, Material blockType, boolean useOffHand) {
        if (this.getEyeLocation().distanceSquared(Util.getMidLocFromBlock(loc.getBlock())) > 25) //Block is out of reach. Don't do anything.
            return;


        boolean placeable = false;
        for (BlockFace face : BlockFace.values()) {
            if (!Util.isBlockAdjacent(face))
                continue;
            Block block = ((CraftBlock) loc.getBlock().getRelative(face)).getNMS().getBlock();
            if (block instanceof AirBlock || block instanceof BushBlock)
                continue;
            placeable = true;
        }

        if (!placeable)
            return;

        InteractionHand hand = InteractionHand.MAIN_HAND;
        if (useOffHand)
            hand = InteractionHand.OFF_HAND;

        net.minecraft.world.item.ItemStack item = CraftItemStack.asNMSCopy(new ItemStack(blockType));

        BlockHitResult result = new BlockHitResult(new Vec3(loc.getX(), loc.getY(), loc.getZ()), this.getDirection().getOpposite(), new BlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), false);
        UseOnContext context = new UseOnContext(this, hand, result);

        InteractionResult interactionResult = item.useOn(context, hand);

        if (interactionResult != InteractionResult.CONSUME)
            return;

        this.setItemInHand(hand, item);
        this.swing(hand);
    }

    //If loc is directly behind, head might rotate opposite to body. Possible fix is temp loc incrementing adding to location over time.
    //TODO Fix head rotation bug
    public void lookAt(Location location) {
        try {
            Vector vec = location.clone().toVector();
            Vector direction = vec.subtract(this.getEyeLocation().toVector()).normalize();

            this.getBukkitEntity().teleport(this.getLocation().setDirection(direction));
        } catch (IllegalArgumentException ignored) {

        }
    }

    public void doJump() {
        this.setJumping(true);
    }

    public void swapItemsFromInv(int slot1, int slot2) {
        try {
            ItemStack item1 = getPlayerInventory().getItem(slot1);
            ItemStack item2 = getPlayerInventory().getItem(slot2);

            getPlayerInventory().setItem(slot1, item2);
            getPlayerInventory().setItem(slot2, item1);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public void setZza(float f) {
        this.zza = f;
    }

    public void setYya(float f) {
        this.yya = f;
    }

    public void setXxa(float f) {
        this.xxa = f;
    }

    public int getMaxHeadXRot() {
        return 40;
    }

    public int getMaxHeadYRot() {
        return 75;
    }

    public int getHeadRotSpeed() {
        return 10;
    }

    public boolean isJumping() {
        return this.jumping;
    }

    public Location getLocation() {
        return this.getBukkitEntity().getLocation();
    }

    public Location getEyeLocation() {
        return this.getBukkitEntity().getEyeLocation();
    }

    public PlayerInventory getPlayerInventory() {
        return this.getBukkitEntity().getInventory();
    }

    public Navigator getNavigator() {
        return navigator;
    }
}
