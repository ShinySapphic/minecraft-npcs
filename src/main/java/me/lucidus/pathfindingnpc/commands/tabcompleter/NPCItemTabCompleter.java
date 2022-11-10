package me.lucidus.pathfindingnpc.commands.tabcompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NPCItemTabCompleter implements TabCompleter {

    private final List<String> arguments = new ArrayList<>();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

        if (arguments.isEmpty()) {
            arguments.add("use");
            arguments.add("useon");
            arguments.add("release");
        }

        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            for (String a : arguments) {
                if (a.toLowerCase().startsWith(args[0].toLowerCase()))
                    if (sender.hasPermission("npc.item"))
                        result.add(a);
            }
        }
        if (args.length == 2) {
            if (sender.hasPermission("npc.item")) {
                if ("off".toLowerCase().startsWith(args[1].toLowerCase()))
                    result.add("off");
            }
        }
        return result;
    }
}
