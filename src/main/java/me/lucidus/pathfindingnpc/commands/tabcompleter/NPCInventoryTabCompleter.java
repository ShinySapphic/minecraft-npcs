package me.lucidus.pathfindingnpc.commands.tabcompleter;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NPCInventoryTabCompleter implements TabCompleter {

    private final List<String> arguments = new ArrayList<>();
    private final List<String> materials = new ArrayList<>();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

        if (arguments.isEmpty()) {
            arguments.add("drop");
            arguments.add("info");
            arguments.add("clear");
            arguments.add("set");
        }

        if (materials.isEmpty())
            for (Material types : Material.values())
                materials.add(types.toString());

        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            for (String a : arguments) {
                if (a.toLowerCase().startsWith(args[0].toLowerCase()))
                    result.add(a);
            }
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (args.length == 2 && arguments.contains(args[0]))
                for (String m : materials) {
                    if (m.toLowerCase().startsWith(args[1].toLowerCase()))
                        if (sender.hasPermission("npc.inv"))
                            result.add(m);
                }
            if (args.length == 3)
                if (sender.hasPermission("npc.inv"))
                    if ("off".toLowerCase().startsWith(args[2].toLowerCase()))
                        result.add("off");
        }
        return result;
    }

}
