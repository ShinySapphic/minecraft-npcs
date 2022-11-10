package me.lucidus.pathfindingnpc.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.lucidus.pathfindingnpc.NPCPlugin;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class NPCManager {

    public static final List<NPC> allNPCs = new ArrayList<>();

    public static void generateNPC(String name, Location location) {
        generateNPC(name, location, UUID.randomUUID(), getProtoSkin()[0], getProtoSkin()[1]);
    }

    public static void generateNPC(String name, Location location, UUID uuid, String texture, String signuature) {
        MinecraftServer server = ((org.bukkit.craftbukkit.v1_17_R1.CraftServer) Bukkit.getServer()).getServer();
        ServerLevel world = ((org.bukkit.craftbukkit.v1_17_R1.CraftWorld) location.getWorld()).getHandle();
        GameProfile profile = new GameProfile(uuid, name);

        profile.getProperties().put("textures", new Property("textures", texture, signuature));

        NPC npc = new NPC(server, world, profile);

        npc.connection = new ServerGamePacketListenerImpl(server, new Connection(PacketFlow.CLIENTBOUND), npc); //Required, otherwise world.addFressEntity(npc) will give a null pointer
        npc.absMoveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        world.addFreshEntity(npc); //This allows npc.tick() and others to function

        if (NPCPlugin.getData().contains("npcs." + npc.getStringUUID()))
            loadStats(npc);

        for (Player all : Bukkit.getOnlinePlayers())
            npc.render(all);

        allNPCs.add(npc);
    }

    public static void removeAll() {
        removeAll(false);
    }

    public static void removeAll(boolean removeData) {
        Iterator<NPC> i = allNPCs.iterator();
        while (i.hasNext()) {
            NPC npc = i.next();
            npc.remove();
            i.remove();

            if (removeData)
                removeData(npc);
        }
    }

    public static void removeData(NPC npc) {
        if (!NPCPlugin.getData().getConfigurationSection("npcs.").contains(npc.getStringUUID()))
            return;
        NPCPlugin.getData().set("npcs." + npc.getStringUUID(), null);
        NPCPlugin.saveData();
    }

    public static void save() {
        for (NPC npc : allNPCs) {
            NPCPlugin.getData().set("npcs." + npc.getStringUUID() + ".location", npc.getLocation());
            NPCPlugin.getData().set("npcs." + npc.getStringUUID() + ".name", npc.getBukkitEntity().getDisplayName());

            npc.getGameProfile().getProperties().get("textures").forEach(key -> {
                NPCPlugin.getData().set("npcs." + npc.getStringUUID() + ".texture", key.getValue());
                NPCPlugin.getData().set("npcs." + npc.getStringUUID() + ".signature", key.getSignature());
            });

            NPCPlugin.getData().set("npcs." + npc.getStringUUID() + ".attributes.exhaustion", npc.getFoodData().getExhaustionLevel());
            NPCPlugin.getData().set("npcs." + npc.getStringUUID() + ".attributes.health", npc.getHealth());
            NPCPlugin.getData().set("npcs." + npc.getStringUUID() + ".attributes.hunger", npc.getFoodData().getFoodLevel());
            NPCPlugin.getData().set("npcs." + npc.getStringUUID() + ".attributes.saturation", npc.getFoodData().getSaturationLevel());

            NPCPlugin.getData().set("npcs." + npc.getStringUUID() + ".inventory", npc.getPlayerInventory().getContents());

            //TODO: Write a method so you don't have to do all of this ugly typing
        }
        NPCPlugin.saveData();
    }

    public static void load() {
        NPCPlugin.getData().getConfigurationSection("npcs").getKeys(false).forEach(uuid -> {
            Location location = NPCPlugin.getData().getLocation("npcs." + uuid + ".location");
            String name = NPCPlugin.getData().getString("npcs." + uuid + ".name");
            String texture = NPCPlugin.getData().getString("npcs." + uuid + ".texture");
            String signature = NPCPlugin.getData().getString("npcs." + uuid + ".signature");

            generateNPC(name, location, UUID.fromString(uuid), texture, signature);
        });
    }

    private static void loadStats(NPC npc) {
        int hunger = NPCPlugin.getData().getInt("npcs." + npc.getStringUUID() + ".attributes.hunger");
        float exhaustion = (float) NPCPlugin.getData().getDouble("npcs." + npc.getStringUUID() + ".attributes.exhaustion");
        float health = (float) NPCPlugin.getData().getDouble("npcs." + npc.getStringUUID() + ".attributes.health");
        float saturation = (float) NPCPlugin.getData().getDouble("npcs." + npc.getStringUUID() + ".attributes.saturation");

        @SuppressWarnings("unchecked")
        ItemStack[] inv = ((List<ItemStack>) NPCPlugin.getData().get("npcs." + npc.getStringUUID() + ".inventory")).toArray(new ItemStack[0]);

        npc.getPlayerInventory().setContents(inv);
        npc.setHealth(health);
        npc.getFoodData().setExhaustion(exhaustion);
        npc.getFoodData().setFoodLevel(hunger);
        npc.getFoodData().setSaturation(saturation);
    }

    private static String[] getProtoSkin() {
        //Skin credit - https://www.planetminecraft.com/skin/protogen-template/
        return new String[] {"ewogICJ0aW1lc3RhbXAiIDogMTYyNTU3Mjc4MjExNSwKICAicHJvZmlsZUlkIiA6ICI3MmNiMDYyMWU1MTA0MDdjOWRlMDA1OTRmNjAxNTIyZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNb3M5OTAiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzBjNTMzM2FmYzA4NWY3MWU0YzNiYmE5NTkyMGM4ZjRlNTg5ZTFjNzkwZjQ5MWRjNTA3YzcxNWU0ZDYzMDU0IgogICAgfQogIH0KfQ==", "PJBqjM3rWL60ofbWBN8RL2OAlvz8oSAV0j6THveeVXbDEC2H61sotfL2UD9OLHCJQohyNst3ZdAqGCP3UNF74+NJP+xWALIWCnKCcty6H8uLM5061rS08dyWAGORGA6HElXU+W4RqUtFqKZ/KMYiU2WFB0teYD38eh43V001FxmF57vaO3A7MlgO4QXwD6fbmQn20VQoRdVNr+u62PvYnSjKMNzSrQSWWeyQnEVGlnUqTB9ef7Mo05hLkcU+BDHsCLMtaf/HgiQMEhJvL2yFEyJPAnguLjyq4MSlG/csySg60gTnnupKeldm/wsNd/pCu4M8yDGzGWhd9PWoJCoNosnZ2mEJkihJog3GNPshy7cLklbhPqNvomhsOaQ78kPOcgoYJP/YPn1W4x6BuqCcTeHxisaX3gQFPpcSl47DRHgSCsI2TKLLCjoxdQ5dBB/GNVToRqb8bSfgi8lgvHAhKLSOsbz06XXpMukv5EBwnO9u/6RWlRaoFXorX3MW+d9hWccrL4TnWaaYgs3sSUPrEo482KbTdrAn86jR3+HVU23LEcspxNsqUBT9iLmO6KC6C0s9gbhQ9b2Pfso+7OV03P59BVvbma8tR3wzU1eOgknqbpI7wI7225KCF1XpykF1Xr9ut1oZLFniAJNKksh3Hz+2KqIunQD1IU1oSlmuE4Y="};
    }

    public static boolean isNPC(Player player) { //TODO Find better method of checking if player is an npc
        return NPCPlugin.getData().getConfigurationSection("npcs.").contains(player.getUniqueId().toString());
    }
}
