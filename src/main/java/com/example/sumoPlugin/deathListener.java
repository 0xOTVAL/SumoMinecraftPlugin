package com.example.sumoPlugin;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class deathListener implements Listener {
    private ArenaManager arenaManager;
    deathListener(ArenaManager arenaManager){
        this.arenaManager=arenaManager;
    }
    @EventHandler
    public void PlayerDeath(PlayerDeathEvent event){
        Arena arena=arenaManager.getArenaByPlayer(event.getPlayer());
        if(arena==null)return;
        event.setCancelled(true);
        if(!arena.isGameStarted){
            return;
        }
        event.getPlayer().sendMessage("You died");
        event.getPlayer().setGameMode(GameMode.SPECTATOR);

    }
}
