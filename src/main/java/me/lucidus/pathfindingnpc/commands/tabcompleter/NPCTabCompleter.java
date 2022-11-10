package me.lucidus.pathfindingnpc.commands.tabcompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class NPCTabCompleter implements TabCompleter {

    private final List<String> arguments = new ArrayList<>();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

        if (arguments.isEmpty()) {
            arguments.add("heal"); arguments.add("tp");
            arguments.add("look"); arguments.add("jump");
            arguments.add("test"); arguments.add("move");
            arguments.add("attack");
        }

        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            for (String a : arguments) {
                if (a.toLowerCase().startsWith(args[0].toLowerCase()))
                    result.add(a);
            }
        }
        return result;
    }
}