package com.coldspare.oparionevents;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class InfectionManager {
    private final Set<UUID> infectedPlayers = new HashSet<>();

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