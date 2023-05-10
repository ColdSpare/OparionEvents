package com.coldspare.oparionevents;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PrepPhaseCommand implements CommandExecutor, TabCompleter {
    private final InfectionManager infectionManager;

    public PrepPhaseCommand(InfectionManager infectionManager) {
        this.infectionManager = infectionManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Usage: /prepphase <enable|disable>");
            return false;
        }

        if ("enable".equalsIgnoreCase(args[0])) {
            infectionManager.startPrepPhase();
            return true;
        } else if ("disable".equalsIgnoreCase(args[0])) {
            infectionManager.endPrepPhase();
            return true;
        } else {
            sender.sendMessage("Invalid argument. Usage: /prepphase <enable|disable>");
            return false;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            if ("enable".startsWith(args[0].toLowerCase())) {
                list.add("enable");
            }
            if ("disable".startsWith(args[0].toLowerCase())) {
                list.add("disable");
            }
            return list;
        }

        return null;
    }
}
