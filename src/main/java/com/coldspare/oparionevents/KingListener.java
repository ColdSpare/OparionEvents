package com.coldspare.oparionevents;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;

public class KingListener implements Listener {
    private final KingVoting kingVoting;

    public KingListener(KingVoting kingVoting) {
        this.kingVoting = kingVoting;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
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
}
