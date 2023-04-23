package com.coldspare.oparionevents;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class KingListener implements Listener {
    private final KingVoting kingVoting;

    public KingListener(KingVoting kingVoting) {
        this.kingVoting = kingVoting;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        if (kingVoting.isKing(player)) {
            if (event.getSlot() == 39 || (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.GOLDEN_HELMET)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot remove the King's Crown!");
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (kingVoting.isKing(player)) {
            PlayerInventory inventory = player.getInventory();
            ItemStack helmet = inventory.getHelmet();
            if (helmet != null && helmet.getType() == Material.GOLDEN_HELMET) {
                inventory.setHelmet(null);
                event.getDrops().remove(helmet);
            }
            // Remove the current king
            kingVoting.removeCurrentKing();
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (kingVoting.isKing(player)) {
            // Re-equip the King's Crown after respawning
            kingVoting.endVotingAndDeclareKing();
        }
    }
}
