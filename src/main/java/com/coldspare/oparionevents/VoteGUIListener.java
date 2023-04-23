package com.coldspare.oparionevents;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class VoteGUIListener implements Listener {
    private final KingVoting kingVoting;

    public VoteGUIListener(KingVoting kingVoting) {
        this.kingVoting = kingVoting;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        if (event.getClickedInventory() == null) return;

        ItemStack currentItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        // Check if the player is trying to move the crown to another slot
        if (kingVoting.isKing(player) &&
                (isKingsCrown(currentItem) || isKingsCrown(cursorItem)) &&
                (event.getAction() == InventoryAction.HOTBAR_SWAP || event.getAction() == InventoryAction.SWAP_WITH_CURSOR)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot move the King's Crown!");
            return;
        }

        if (kingVoting.isKing(player) && (event.getSlot() == 39 || event.getHotbarButton() == 39) &&
                isKingsCrown(currentItem)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot remove the King's Crown!");
            return;
        }

        if (event.getView().getTitle().equals("Vote for King")) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            if (clickedItem.getType() == Material.PLAYER_HEAD) {
                String candidateName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
                Player candidate = Bukkit.getPlayer(candidateName);
                if (candidate != null) {
                    kingVoting.castVote(player.getUniqueId(), candidate.getUniqueId());
                    player.sendMessage(ChatColor.GREEN + "You have voted for " + candidate.getName() + ".");
                    player.closeInventory();
                }
            }
        }
    }

    private boolean isKingsCrown(ItemStack item) {
        if (item == null || item.getType() != Material.GOLDEN_HELMET) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName() && meta.hasLore()) {
            String displayName = meta.getDisplayName();
            List<String> lore = meta.getLore();
            return ChatColor.stripColor(displayName).equals("King's Crown") &&
                    lore.contains(ChatColor.GRAY + "The King's Royal Crown");
        }
        return false;
    }
}
