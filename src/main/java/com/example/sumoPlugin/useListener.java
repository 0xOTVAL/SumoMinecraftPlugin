package com.example.sumoPlugin;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class useListener implements Listener {
    private ArenaManager arenaManager;
    useListener(ArenaManager arenaManager){
        this.arenaManager=arenaManager;
    }

    @EventHandler
    public void PlayerRightClick(PlayerInteractEvent event){
        //get arena and player
        Player player=event.getPlayer();
        Arena arena=arenaManager.getArenaByPlayer(player);
        //we don't want to do anything if:player is not on arena, arena is not started or game is already started
        if(arena==null)return;
        if(!arena.isStarted)return;
        if(arena.isGameStarted)return;
        //get previous team and target team
        Team prev_team=arena.getTeamByPlayer(player);
        Team team=arena.getTeamByItem(event.getItem());
        //do nothing if player is already in target team
        if(prev_team==team)return;
        //remove player from previous team
        if(prev_team!=null)prev_team.removePlayer(player);
        //set player armor to armor, colored by team color
        player.getInventory().setArmorContents(team.armor);
        //send message to player
        player.sendMessage("You joined "+team.name+" team");
    }
}
