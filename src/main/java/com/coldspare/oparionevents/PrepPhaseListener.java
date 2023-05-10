package com.coldspare.oparionevents;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PrepPhaseListener implements Listener {
    private final InfectionManager infectionManager;

    public PrepPhaseListener(InfectionManager infectionManager) {
        this.infectionManager = infectionManager;
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player && infectionManager.isPrepPhase()) {
            event.setCancelled(true);
        }
    }
}
