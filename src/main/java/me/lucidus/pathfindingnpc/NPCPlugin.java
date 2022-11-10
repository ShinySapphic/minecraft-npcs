package me.lucidus.pathfindingnpc;

import me.lucidus.pathfindingnpc.commands.*;
import me.lucidus.pathfindingnpc.commands.tabcompleter.*;
import me.lucidus.pathfindingnpc.entity.NPCManager;
import me.lucidus.pathfindingnpc.files.DataManager;
import me.lucidus.pathfindingnpc.listeners.PlayerJoinListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NPCPlugin extends JavaPlugin {

    private static DataManager data;

    @Override
    public void onEnable() {
        data = new DataManager(this, "data");
        if (getData().contains("npcs"))
            NPCManager.load();

        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        NPCManager.save();
        NPCManager.removeAll();
    }

    private void registerCommands() {
        this.getCommand("npc").setExecutor(new NPCCommand());
        this.getCommand("npcblock").setExecutor(new NPCBlockCommand());
        this.getCommand("npcinventory").setExecutor(new NPCInventoryCommand());
        this.getCommand("npcremove").setExecutor(new NPCRemoveCommand());
        this.getCommand("npcspawn").setExecutor(new NPCSpawnCommand());
        this.getCommand("npcitem").setExecutor(new NPCItemCommand());
        this.getCommand("npcset").setExecutor(new NPCSetCommand());

        registerTabCompleters();
    }

    private void registerTabCompleters() {
        this.getCommand("npc").setTabCompleter(new NPCTabCompleter());
        this.getCommand("npcblock").setTabCompleter(new NPCBlockTabCompleter());
        this.getCommand("npcinventory").setTabCompleter(new NPCInventoryTabCompleter());
        this.getCommand("npcitem").setTabCompleter(new NPCItemTabCompleter());
        this.getCommand("npcset").setTabCompleter(new NPCSetTabCompleter());
    }

    private void registerListeners() {
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new PlayerJoinListener(), this);
        //pm.registerEvents(new PlayerPlaceBlockTestListener(), this);
    }

    public static FileConfiguration getData() {
        return data.getConfig();
    }

    public static void saveData() {
        data.saveConfig();
    }

}
