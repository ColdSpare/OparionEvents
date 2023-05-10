package com.coldspare.oparionevents;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class InfectionManager {
    private final OparionEvents plugin;
    private final Set<UUID> infectedPlayers = new HashSet<>();
    private BukkitTask prepPhaseTask = null;
    private int prepPhaseTime;

    // constructor
    public InfectionManager(OparionEvents plugin) {
        this.plugin = plugin;
    }

    public void startPrepPhase() {
        // Set the prep phase time to 30 minutes in seconds
        prepPhaseTime = 30 * 60;

        // If a prep phase is already running, cancel it
        if (prepPhaseTask != null) {
            prepPhaseTask.cancel();
        }

        // Start the new prep phase timer
        prepPhaseTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (prepPhaseTime <= 0) {
                    // Time's up, end the prep phase
                    endPrepPhase();
                } else {
                    // Send the remaining time to all online players
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        sendPrepPhaseTime(player, prepPhaseTime);
                    }

                    // Decrement the prep phase time
                    prepPhaseTime--;
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public void endPrepPhase() {
        if (prepPhaseTask != null) {
            prepPhaseTask.cancel();
            prepPhaseTask = null;
        }

        // Clear the action bar of all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
        }
    }

    public boolean isPrepPhase() {
        return prepPhaseTask != null;
    }

    private void sendPrepPhaseTime(Player player, int secondsLeft) {
        String message = String.format("Preparation phase time left: %02d:%02d", secondsLeft / 60, secondsLeft % 60);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    public void infectPlayer(Player player) {
        infectedPlayers.add(player.getUniqueId());

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 60 * 15, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 60 * 15, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 60 * 15, 1));
    }

    public boolean isInfected(Player player) {
        return infectedPlayers.contains(player.getUniqueId());
    }

    public void curePlayer(Player player) {
        infectedPlayers.remove(player.getUniqueId());

        player.removePotionEffect(PotionEffectType.SLOW);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        player.removePotionEffect(PotionEffectType.POISON);
    }

    public void sendRemainingTime(Player player, int secondsLeft) {
        String message = String.format("Time left: %02d:%02d", secondsLeft / 60, secondsLeft % 60);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    public void handleCurePotion(Player player) {
        if (isInfected(player)) {
            curePlayer(player);
        }
    }

    public void startCountdownTimer(OparionEvents plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : infectedPlayers) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        PotionEffect poisonEffect = player.getPotionEffect(PotionEffectType.POISON);
                        if (poisonEffect != null) {
                            int secondsLeft = poisonEffect.getDuration() / 20;
                            sendRemainingTime(player, secondsLeft);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }
}