package com.example.sumoPlugin;

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
    private Arena getArenaByPlayer( Player player) {
        for (Map.Entry e: arenaManager.players.entrySet()) {
            for(Object p: (ArrayList)e.getValue()){
                if(p==player)return (Arena) e.getKey();
            }
        }
        return null;
    }
    @EventHandler
    public void PlayerDeath(PlayerDeathEvent event){
        Arena arena=getArenaByPlayer(event.getPlayer());
        if(arena==null)return;
        event.setCancelled(true);
        if(!arenaManager.isArenaGameStarted.get(arena)){
            return;
        }
        event.getPlayer().sendMessage("You died");
        arenaManager.returnPlayer(arena,event.getPlayer());

    }
}
