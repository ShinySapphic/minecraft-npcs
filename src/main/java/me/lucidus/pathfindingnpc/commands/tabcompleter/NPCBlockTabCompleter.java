package me.lucidus.pathfindingnpc.commands.tabcompleter;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class NPCBlockTabCompleter implements TabCompleter {

    private final List<String> arguments = new ArrayList<>();
    private final List<String> materials = new ArrayList<>();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

        if (arguments.isEmpty()) {
            arguments.add("place");
            arguments.add("break");
        }
        if (materials.isEmpty())
            for (Material types : Material.values())
                materials.add(types.toString());

        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            for (String a : arguments) {
                if (a.toLowerCase().startsWith(args[0].toLowerCase()))
                    if (sender.hasPermission("npc.block"))
                        result.add(a);
            }
        }

        if (args.length == 2 && arguments.contains(args[0]))
            for (String m : materials) {
                if (m.toLowerCase().startsWith(args[1].toLowerCase()))
                    if (sender.hasPermission("npc.block"))
                        result.add(m);
            }
        return result;
    }

}
