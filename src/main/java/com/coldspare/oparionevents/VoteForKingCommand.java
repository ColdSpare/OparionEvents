package com.coldspare.oparionevents;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoteForKingCommand implements CommandExecutor {
    private final KingVoting kingVoting;

    public VoteForKingCommand(KingVoting kingVoting) {
        this.kingVoting = kingVoting;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            kingVoting.openVotingGUI(player);
        } else {
            sender.sendMessage("§cOnly players can use this command.");
        }
        return true;
    }
}
