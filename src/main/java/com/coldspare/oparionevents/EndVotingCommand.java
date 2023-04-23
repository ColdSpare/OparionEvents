package com.coldspare.oparionevents;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EndVotingCommand implements CommandExecutor {
    private final KingVoting kingVoting;

    public EndVotingCommand(KingVoting kingVoting) {
        this.kingVoting = kingVoting;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("oparionevents.endvoting")) {
            player.sendMessage("You don't have permission to use this command.");
            return true;
        }

        kingVoting.endVotingAndDeclareKing();
        return true;
    }
}
