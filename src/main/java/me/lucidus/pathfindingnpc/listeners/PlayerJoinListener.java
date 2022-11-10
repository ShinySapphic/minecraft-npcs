package me.lucidus.pathfindingnpc.listeners;

import me.lucidus.pathfindingnpc.entity.NPC;
import me.lucidus.pathfindingnpc.entity.NPCManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        for (NPC npcs : NPCManager.allNPCs) {
            npcs.render(event.getPlayer()); event.getPlayer().getLocation();
        }
    }
}
