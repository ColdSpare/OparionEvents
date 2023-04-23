package com.coldspare.oparionevents;


import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfectCommand implements CommandExecutor {
    private final InfectionManager infectionManager;

    public InfectCommand(InfectionManager infectionManager) {
        this.infectionManager = infectionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("oparionevents.infect")) {
            if (args.length == 1) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    infectionManager.infectPlayer(target);
                    sender.sendMessage("§aSuccessfully infected " + target.getName() + ".");
                } else {
                    sender.sendMessage("§cPlayer not found.");
                }
            } else {
                sender.sendMessage("§cUsage: /infect <player>");
            }
        } else {
            sender.sendMessage("§cYou don't have permission to use this command.");
        }
        return true;
    }
}

