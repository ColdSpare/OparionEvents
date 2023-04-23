package com.coldspare.oparionevents;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class VoteGUIListener implements Listener {
    private final KingVoting kingVoting;

    public VoteGUIListener(KingVoting kingVoting) {
        this.kingVoting = kingVoting;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!event.getView().getTitle().equals("Vote for King")) return;

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        Player voter = (Player) event.getWhoClicked();
        if (clickedItem.getType() == Material.GREEN_CONCRETE) {
            int candidateSlot = event.getSlot() - 1;
            ItemStack candidateHead = event.getClickedInventory().getItem(candidateSlot);
            if (candidateHead != null && candidateHead.getType() == Material.PLAYER_HEAD) {
                String candidateName = ChatColor.stripColor(candidateHead.getItemMeta().getDisplayName());
                Player candidate = Bukkit.getPlayer(candidateName);
                if (candidate != null) {
                    kingVoting.castVote(voter.getUniqueId(), candidate.getUniqueId());
                    voter.sendMessage(ChatColor.GREEN + "You have voted for " + candidate.getName() + ".");
                    voter.closeInventory();
                }
            }
        }
    }
}
