package com.coldspare.oparionevents;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class HungerPhaseManager {
    private final OparionEvents plugin;
    private BossBar hungerPhaseBar;
    private boolean isHungerPhase;

    public HungerPhaseManager(OparionEvents plugin) {
        this.plugin = plugin;
    }

    public boolean isHungerPhase() {
        return isHungerPhase;
    }

    public void startHungerPhase() {
        isHungerPhase = true;
        hungerPhaseBar = Bukkit.createBossBar("Hunger Phase", BarColor.RED, BarStyle.SOLID);
        hungerPhaseBar.setProgress(1.0);

        for (Player player : Bukkit.getOnlinePlayers()) {
            hungerPhaseBar.addPlayer(player);
            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20 * 60 * 20, 1));
        }

        new BukkitRunnable() {
            int timeLeft = 20 * 60; // 20 minutes

            @Override
            public void run() {
                if (timeLeft <= 0) {
                    endHungerPhase();
                    this.cancel();
                    return;
                }

                hungerPhaseBar.setProgress(timeLeft / (20.0 * 60));
                timeLeft--;
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public void endHungerPhase() {
        isHungerPhase = false;
        hungerPhaseBar.removeAll();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.removePotionEffect(PotionEffectType.HUNGER);
        }
    }
}
