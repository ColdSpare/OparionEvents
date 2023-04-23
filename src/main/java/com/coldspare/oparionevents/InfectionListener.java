package com.coldspare.oparionevents;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class InfectionListener implements Listener {
    private final OparionEvents plugin;
    private final InfectionManager infectionManager;

    public InfectionListener(OparionEvents plugin, InfectionManager infectionManager) {
        this.plugin = plugin;
        this.infectionManager = infectionManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (infectionManager.isInfected(player)) {
            player.getNearbyEntities(3, 3, 3).stream()
                    .filter(entity -> entity instanceof Player)
                    .map(entity -> (Player) entity)
                    .forEach(infectionManager::infectPlayer);
        }
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (isCurePotion(item)) {
            infectionManager.handleCurePotion(event.getPlayer());
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            String key = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "cure_potion"), PersistentDataType.STRING);
            if (key != null) {
                Player player = (Player) event.getWhoClicked();
                if (infectionManager.isInfected(player)) {
                    infectionManager.curePlayer(player);
                }
            }
        }
    }

    private boolean isCurePotion(ItemStack item) {
        if (item.getType() != Material.POTION) {
            return false;
        }

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            return false;
        }

        // Check for the custom name
        if (!itemMeta.hasDisplayName() || !(ChatColor.GREEN + "Cure Potion").equals(itemMeta.getDisplayName())) {
            return false;
        }

        // Check for the custom lore
        List<String> expectedLore = new ArrayList<>();
        expectedLore.add(ChatColor.GRAY + "Drink this potion to");
        expectedLore.add(ChatColor.GRAY + "cure the infection.");
        if (!itemMeta.hasLore() || !expectedLore.equals(itemMeta.getLore())) {
            return false;
        }

        // Check for the persistent data key
        String curePotionKey = itemMeta.getPersistentDataContainer().get(new NamespacedKey(plugin, "cure_potion"), PersistentDataType.STRING);
        return "cure_potion".equals(curePotionKey);
    }
}