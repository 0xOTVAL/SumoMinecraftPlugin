package com.example.sumoPlugin;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.Map;

public class deathListener implements Listener {
    private ArenaManager arenaManager;
    deathListener(ArenaManager arenaManager){
        this.arenaManager=arenaManager;
    }
    private ArenaData getArenaByPlayer(Player player) {
        for (Map.Entry e: arenaManager.players.entrySet()) {
            for(Object p: (ArrayList)e.getValue()){
                if(p==player)return (ArenaData) e.getKey();
            }
        }
        return null;
    }
    @EventHandler
    public void PlayerDeath(PlayerDeathEvent event){
        ArenaData arena=getArenaByPlayer(event.getPlayer());
        if(arena==null)return;
        event.setCancelled(true);
        if(!arenaManager.isArenaGameStarted.get(arena)){
            return;
        }
        event.getPlayer().sendMessage("You died");
        event.getPlayer().setGameMode(GameMode.SPECTATOR);

    }
}
