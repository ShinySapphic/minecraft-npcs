package me.lucidus.pathfindingnpc.entity;

import com.mojang.authlib.GameProfile;
import me.lucidus.pathfindingnpc.NPCPlugin;
import me.lucidus.pathfindingnpc.packets.PacketSender;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class NPC extends PathfindingNPC {

    private final PacketSender packet;

    public NPC(MinecraftServer minecraftserver, ServerLevel worldserver, GameProfile gameprofile) {
        super(minecraftserver, worldserver, gameprofile);
        this.packet = new PacketSender(this);
    }

    @Override
    public void tick() {
        super.tick();
        doTick();
    }

    @Override
    protected void checkFallDamage(double d0, boolean flag, BlockState iblockdata, BlockPos blockposition) {
        super.doCheckFallDamage(d0, flag);
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        boolean damaged = super.hurt(damagesource, f);

        if (damaged && hurtMarked && !isDeadOrDying()) {
            this.hurtMarked = false;

            new BukkitRunnable() {
                public void run() {
                    hurtMarked = true;
                }
            }.runTaskLater(NPCPlugin.getPlugin(NPCPlugin.class), 1);
        }
        return damaged;
    }

    @Override //Prevents npc from being pushed forward.
    protected void blockUsingShield(LivingEntity entityliving) {
        this.knockback(0.0D, this.getX() - entityliving.getX(), this.getZ() - entityliving.getZ());
        if (entityliving.getMainHandItem().getItem() instanceof AxeItem) {
            this.disableShield(true);
        }
    }

    @Override
    public void doTick() {
        super.doTick();
        /*baseTick();
        this.lerpSteps = (int) this.zza;
        if (this.hurtTime > 0) {
            this.hurtTime -= 1;
        }
        tickEffects();
        this.xOld = this.getX();
        this.yOld = this.getY();
        this.zOld = this.getZ();
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
        Bukkit.broadcastMessage("DO TICK METHOD");*/
    }

    @Override
    public void die(DamageSource damagesource) {
        NPCManager.allNPCs.remove(this);
        NPCManager.removeData(this);

        super.die(damagesource);
        new BukkitRunnable() {
            public void run() {
                remove();
            }
        }.runTaskLater(NPCPlugin.getPlugin(NPCPlugin.class), 25);
    }

    @Override
    public void aiStep() {
        super.aiStep();
    }

    public void remove() {
        this.remove(RemovalReason.DISCARDED);
        for (Player all : Bukkit.getOnlinePlayers())
            packet.removeNPCPacket(all);
    }

    public void render(Player player) {
        packet.addNPCPacket(player);
            /*If npc is removed from tablist, do this to load
            if (all.getLocation().distanceSquared(this.getBukkitEntity().getLocation()) < 2304) {
                packet.addNPCPacket(all);
            }*/
    }
}
