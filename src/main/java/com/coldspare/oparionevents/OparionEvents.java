package com.coldspare.oparionevents;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class OparionEvents extends JavaPlugin {
    private InfectionManager infectionManager;
    private KingVoting kingVoting;

    @Override
    public void onEnable() {
        createCureRecipe();

        infectionManager = new InfectionManager();
        getServer().getPluginManager().registerEvents(new InfectionListener(this, infectionManager), this);
        infectionManager.startCountdownTimer(this);

        getCommand("infect").setExecutor(new InfectCommand(infectionManager));
        kingVoting = new KingVoting(this);
        getCommand("voteForKing").setExecutor(new VoteForKingCommand(kingVoting));
        getCommand("endVoting").setExecutor(new EndVotingCommand(kingVoting)); // Register the endVoting command
        Bukkit.getPluginManager().registerEvents(new VoteGUIListener(kingVoting), this);
        Bukkit.getPluginManager().registerEvents(new KingListener(kingVoting), this); // Register the KingListener
    }

    private void createCureRecipe() {
        ItemStack curePotion = new ItemStack(Material.POTION); // Change Material.POTION to the desired potion type

        // Add custom name and lore using an ItemMeta
        ItemMeta curePotionMeta = curePotion.getItemMeta();
        curePotionMeta.setDisplayName(ChatColor.GREEN + "Cure Potion");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Drink this potion to");
        lore.add(ChatColor.GRAY + "cure the infection.");
        curePotionMeta.setLore(lore);

        // Add a persistent data key to identify the potion as a cure potion
        curePotionMeta.getPersistentDataContainer().set(new NamespacedKey(this, "cure_potion"), PersistentDataType.STRING, "cure_potion");
        curePotion.setItemMeta(curePotionMeta);

        ShapedRecipe cureRecipe = new ShapedRecipe(new NamespacedKey(this, "cure_potion"), curePotion);
        cureRecipe.shape("AAA", "ABA", "AAA");
        cureRecipe.setIngredient('A', Material.IRON_INGOT);
        cureRecipe.setIngredient('B', Material.WATER_BUCKET);

        Bukkit.addRecipe(cureRecipe);
    }

}