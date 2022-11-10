package me.lucidus.pathfindingnpc.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class PlayerPlaceBlockTestListener implements Listener {

/*    @EventHandler
    public void onPlace(PlayerInteractEvent event) {
        if (!event.getHand().equals(EquipmentSlot.HAND))
            return;
        if (event.getClickedBlock() == null)
            return;
        Bukkit.broadcastMessage(event.getPlayer().getEyeLocation().distanceSquared(Util.getMidLocFromBlock(event.getClickedBlock())) + "");
        Bukkit.broadcastMessage(ChatColor.GREEN + event.getPlayer().getEyeLocation().toString());
        Bukkit.broadcastMessage(ChatColor.RED + Util.getMidLocFromBlock(event.getClickedBlock()).toString());
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        if (!(event.getDamager() instanceof Player))
            return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        LivingEntity damager = (LivingEntity) event.getDamager();

        Bukkit.broadcastMessage(damager.getEyeLocation().distanceSquared(Util.getMidLocFromEntity(entity)) + "");

        Bukkit.broadcastMessage(ChatColor.GREEN + damager.getEyeLocation().toString());
        Bukkit.broadcastMessage(ChatColor.RED + Util.getMidLocFromEntity(entity).toString());
    }*/
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Bukkit.broadcastMessage("BREAK");
    }

}
