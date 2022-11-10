package me.lucidus.pathfindingnpc.packets;

import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketSender {

    private final ServerPlayer npc;

    public PacketSender(ServerPlayer npc) {
        this.npc = npc;
    }

    public void addNPCPacket(Player player) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        SynchedEntityData watcher = npc.getEntityData();
        watcher.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 255);
        connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, npc));
        connection.send(new ClientboundAddPlayerPacket(npc));
        connection.send(new ClientboundRotateHeadPacket(npc, (byte) (npc.yRotO * 256 / 360)));
        connection.send(new ClientboundSetEntityDataPacket(npc.getId(), watcher, true));
    }

    public void removeNPCPacket(Player player) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        SynchedEntityData watcher = npc.getEntityData();
        watcher.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 255);
        connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, npc));
        connection.send(new ClientboundRemoveEntitiesPacket(npc.getId()));
    }

}
