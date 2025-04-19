package com.example.sumoplugin.eventlisteners;

import com.example.sumoplugin.arena.Arena;
import com.example.sumoplugin.arena.ArenaManager;
import com.example.sumoplugin.Sumo;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class deathListener implements Listener {
    public ArenaManager arenaManager;
    public Sumo plugin;
    public deathListener(Sumo plugin){
        this.plugin=plugin;
        arenaManager=plugin.arenaManager;
    }
    @EventHandler
    public void PlayerDeath(PlayerDeathEvent event){
        Arena arena=arenaManager.getArenaByPlayer(event.getPlayer());
        if(arena==null)return;
        if(!arena.isGameStarted){
            event.setCancelled(true);
            return;
        }
        arena.killPlayer(event.getPlayer());
        event.setCancelled(true);
    }
}
