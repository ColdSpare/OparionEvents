package com.coldspare.oparionevents;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class HungerPhaseCommand implements CommandExecutor, TabCompleter {
    private final HungerPhaseManager hungerPhaseManager;

    public HungerPhaseCommand(HungerPhaseManager hungerPhaseManager) {
        this.hungerPhaseManager = hungerPhaseManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            return false;
        }

        if (args[0].equalsIgnoreCase("enable")) {
            hungerPhaseManager.startHungerPhase();
        } else if (args[0].equalsIgnoreCase("disable")) {
            hungerPhaseManager.endHungerPhase();
        } else {
            return false;
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return Stream.of("enable", "disable")
                    .filter(e -> e.startsWith(args[0]))
                    .collect(Collectors.toList());
        }
        return null;
    }
}

