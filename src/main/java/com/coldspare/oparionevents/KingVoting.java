package com.coldspare.oparionevents;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.types.InheritanceNode;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Color;

public class KingVoting {
    private final OparionEvents plugin;
    private final Map<UUID, UUID> votes;
    private UUID currentKingUUID = null;

    public KingVoting(OparionEvents plugin) {
        this.plugin = plugin;
        this.votes = new HashMap<>();
    }


    public void castVote(UUID voter, UUID candidate) {
        votes.put(voter, candidate);
    }

    public UUID getElectedKing() {
        if (votes.isEmpty()) {
            return null;
        }

        Map<UUID, Integer> voteCounts = new HashMap<>();
        for (UUID candidate : votes.values()) {
            voteCounts.put(candidate, voteCounts.getOrDefault(candidate, 0) + 1);
        }

        UUID electedKing = Collections.max(voteCounts.entrySet(), Map.Entry.comparingByValue()).getKey();
        return electedKing;
    }


    public boolean isKing(Player player) {
        return currentKingUUID != null && currentKingUUID.equals(player.getUniqueId());
    }

    public List<Player> getPlayersInSameGroup(Player player) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        UserManager userManager = luckPerms.getUserManager();
        User user = userManager.getUser(player.getUniqueId());
        if (user != null) {
            String userGroup = user.getNodes().stream()
                    .filter(InheritanceNode.class::isInstance)
                    .map(InheritanceNode.class::cast)
                    .map(InheritanceNode::getGroupName)
                    .findFirst().orElse("");

            List<Player> playersInSameGroup = new ArrayList<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!onlinePlayer.isOp()) {
                    User onlineUser = userManager.getUser(onlinePlayer.getUniqueId());
                    if (onlineUser != null) {
                        String onlineUserGroup = onlineUser.getNodes().stream()
                                .filter(InheritanceNode.class::isInstance)
                                .map(InheritanceNode.class::cast)
                                .map(InheritanceNode::getGroupName)
                                .findFirst().orElse("");
                        if (userGroup.equals(onlineUserGroup)) {
                            playersInSameGroup.add(onlinePlayer);
                        }
                    }
                }
            }
            return playersInSameGroup;
        }
        return new ArrayList<>();
    }

    public void openVotingGUI(Player player) {
        List<Player> playersInSameGroup = getPlayersInSameGroup(player);
        Inventory votingGUI = Bukkit.createInventory(null, 54, "Vote for King");

        for (int i = 0; i < playersInSameGroup.size(); i++) {
            Player candidate = playersInSameGroup.get(i);
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
            skullMeta.setOwningPlayer(candidate);
            skullMeta.setDisplayName(ChatColor.GREEN + candidate.getName());
            playerHead.setItemMeta(skullMeta);

            votingGUI.setItem(i * 2, playerHead);
        }

        player.openInventory(votingGUI);
    }

    public void removeCurrentKing() {
        if (currentKingUUID != null) {
            Player previousKing = Bukkit.getPlayer(currentKingUUID);
            if (previousKing != null) {
                previousKing.getInventory().setHelmet(null);
                previousKing.removePotionEffect(PotionEffectType.GLOWING);
            }
            currentKingUUID = null;
        }
    }

    public void endVotingAndDeclareKing() {
        UUID electedKingUUID = getElectedKing();
        Player electedKing = Bukkit.getPlayer(electedKingUUID);
        if (electedKingUUID == null) {
            // Handle the case when no king is elected, e.g., broadcast a message
            Bukkit.broadcastMessage(ChatColor.RED + "No king has been elected as there were no votes.");
            return;
        }
        if (electedKing != null) {
            // Remove the previous king
            if (currentKingUUID != null) {
                Player previousKing = Bukkit.getPlayer(currentKingUUID);
                if (previousKing != null) {
                    previousKing.getInventory().setHelmet(null);
                    previousKing.removePotionEffect(PotionEffectType.GLOWING);
                }
            }
            currentKingUUID = electedKing.getUniqueId();
            // Broadcast the winner
            Bukkit.broadcastMessage(ChatColor.GOLD + electedKing.getName() + " has been elected as the king!");

            // Play a sound to all players
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 1.0f);
            }

            // Give the golden helmet with Protection 10 and unbreakable properties
            ItemStack goldenHelmet = new ItemStack(Material.GOLDEN_HELMET);
            ItemMeta helmetMeta = goldenHelmet.getItemMeta();
            helmetMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true);
            helmetMeta.setUnbreakable(true);
            goldenHelmet.setItemMeta(helmetMeta);

            // Replace current helmet, if any, and equip the new one
            ItemStack currentHelmet = electedKing.getInventory().getHelmet();
            if (currentHelmet != null && currentHelmet.getType() != Material.AIR) {
                electedKing.getInventory().remove(currentHelmet);
            }
            electedKing.getInventory().setHelmet(goldenHelmet);

            // Apply the glowing effect
            PotionEffect glowingEffect = new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, false, false);
            electedKing.addPotionEffect(glowingEffect);

        }
    }
}

