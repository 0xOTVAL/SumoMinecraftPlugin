package com.example.sumoplugin.eventlisteners;

import com.example.sumoplugin.arena.Arena;
import com.example.sumoplugin.arena.ArenaManager;
import com.example.sumoplugin.Sumo;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class logoutListener implements Listener {
    public ArenaManager arenaManager;
    public Sumo plugin;
    public logoutListener(Sumo plugin){
        this.plugin=plugin;
        arenaManager=plugin.arenaManager;
    }
    @EventHandler
    public void playerLogout(PlayerQuitEvent event){
        Arena arena =arenaManager.getArenaByPlayer(event.getPlayer());
        if(arena!=null)arena.logoutPlayer(event.getPlayer());
    }
}
