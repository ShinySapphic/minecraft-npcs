package me.lucidus.pathfindingnpc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.lucidus.pathfindingnpc.packets.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Deprecated
public class CustomNPC {

    private final List<UUID> loaded = new ArrayList<>();

    private int updateTask;
    private ServerPlayer npc;

    private final Location location;
    private final PacketSender packet;

    public CustomNPC(Location location) {
        this.location = location;
        this.packet = new PacketSender(this.npc);

        generateNPC();
        playerUpdate();
    }

    private void generateNPC() {
        MinecraftServer server = ((org.bukkit.craftbukkit.v1_18_R2.CraftServer) Bukkit.getServer()).getServer();
        ServerLevel world = ((org.bukkit.craftbukkit.v1_18_R2.CraftWorld) location.getWorld()).getHandle();
        GameProfile profile = getProfile();

        this.npc = new ServerPlayer(server, world, profile);
    }

    private GameProfile getProfile() {
        // Skin #1916544477 generated on Jan 12, 2021 4:00:38 AM via MineSkin.org - https://minesk.in/1916544477
        GameProfile profile = new GameProfile(UUID.fromString("ec70bcaf-702f-4bb8-b48d-276fa52a780c"), "Dream");
        profile.getProperties().put("textures", new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYxMDQ0NTYzNzg0MywKICAicHJvZmlsZUlkIiA6ICJlYzcwYmNhZjcwMmY0YmI4YjQ4ZDI3NmZhNTJhNzgwYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJEcmVhbSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jYTkzZjZmYzQwNDg4ZjE4NzdjZGE5NGE4MzBiNTRlOWY2ZjU0YWI1OGE1NDUzYmFkNWM5NDc3MjZkZDFmNDczIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=", "eKbpx8sJu5c8XbASG67IGsUi7K7H3ZQpybSHvUnyS1jbGKB0SwYGkPk9ptUgWPWIgZATjX/nZYFCzWP4zxLGR1pOQg6Tk4rTWWuD7VQKNHJA7NA1yaeb4eWvcpR/IL/hU94IQ2wNgmdZbxsORhCXbm0O2NzRu1IPE5a4PP2PEkZdASSG6SLHn4MumUTIERemalDYwAHA1hlIh7Hg5Lei6P7ak2PD4WkHL/EJ57PpC/mS/myyjgs3E8++fH2FSRWhs32n2+WJnxpTskXBP5csdrObtDfrjIDiU8w0gH6rdxeely1aOGyCjAfGex4bWmSKwl66Sj2ZGg2a71JMNR9MYwIAC62OTQpN0gbBE6uxkcKUXJ/77XgPeAlV/tCT7rtyhQN3e1HOlCkt8FexT1I33dyX/nAL0Bd/NTkaxm37M9HdiirjE81x4cFMclAcvyE/YmwCKn9PJxRIo5gALzKLJdsBY5qPImtTMZNeOKXjNBFROE+NDANc3YV7bxA/gN7dHxXuaj9A20dN7lw8El7+VVYUfBDkwJKC7qDEcIMuAsPlU4QTczy+UwPH/1a0gDyAHl6vO/h4q7KYZ4LQDeqkW07dWsNjEuZJXK07XShwAFoIqKJBd5couCbFvQ/NsNslK4TrW93sFHf86LK//61HC89e/xLWhwiCwFt3UWLmjoA="));
        return profile;
    }

    //Player specific

    /*private void generateNPC() {
        MinecraftServer server = ((org.bukkit.craftbukkit.v1_17_R1.CraftServer) Bukkit.getServer()).getServer();
        ServerLevel world = ((org.bukkit.craftbukkit.v1_17_R1.CraftWorld) Bukkit.getWorld(location.getWorld().getName())).getHandle();
        GameProfile profile = new GameProfile(UUID.randomUUID(), name);
        ServerPlayer npc = new ServerPlayer(server, world, profile);

        String[] skin = getSkin();
        profile.getProperties().put("textures", new Property("textures", skin[0], skin[1]));
    }*/

    /*private String[] getSkin() {
        ServerPlayer player = ((org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer) player).getHandle();
        GameProfile profile = player.getGameProfile();
        Property property = profile.getProperties().get("textures").iterator().next();
        String texture = property.getValue();
        String signature = property.getSignature();

        return new String[] {texture, signature};
    }*/

    private void load(Player player) {
        packet.addNPCPacket(player);
    }

    private void unload(Player player) {
        packet.removeNPCPacket(player);
    }

    private void playerUpdate() {
        updateTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(NPCPlugin.getPlugin(NPCPlugin.class), new Runnable() {
            public void run() {
                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (all.getWorld() != location.getWorld())
                        continue;

                    if (all.getLocation().distance(location) <= 48) {
                        if (loaded.contains(all.getUniqueId()))
                            continue;

                        if (isVisibleToEntity(all)) {
                            loaded.add(all.getUniqueId());
                            load(all);
                        }


                    } else {
                        if (!loaded.contains(all.getUniqueId()))
                            continue;

                        loaded.remove(all.getUniqueId());
                        unload(all);
                    }
                }
            }
        }, 0, 0);
    }

    private boolean isVisibleToEntity(LivingEntity entity) {
        Vector vector = getLocation().toVector().subtract(entity.getLocation().toVector());
        Vector direction = entity.getEyeLocation().getDirection();
        double dot = vector.dot(direction);

        return dot >= 0 && entity.hasLineOfSight(npc.getBukkitEntity());
    }

    public void remove() {
        Bukkit.getScheduler().cancelTask(updateTask);

        for (Player all : Bukkit.getOnlinePlayers()) {
            if (!loaded.contains(all.getUniqueId()))
                continue;
            unload(all);
        }

        if (!loaded.isEmpty())
            loaded.clear();
    }

    public Location getLocation() {
        return npc.getBukkitEntity().getLocation();
    }
}
