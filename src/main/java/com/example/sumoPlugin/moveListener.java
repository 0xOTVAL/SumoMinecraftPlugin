package com.example.sumoPlugin;


import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Map;
import java.util.Objects;


public class moveListener implements Listener{
    private ArenaManager arenaManager;
    moveListener(ArenaManager arenaManager){
        this.arenaManager=arenaManager;
    }
    public <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
    @EventHandler
    public void PlayerMove(PlayerMoveEvent event){
        if(!arenaManager.players.containsValue(event.getPlayer()))return;
        Arena arena=getKeyByValue(arenaManager.players,event.getPlayer());
        if(!(Math.min(arena.getPos1().x,arena.getPos2().x)<event.getTo().x() && event.getTo().x()<Math.max(arena.getPos1().x,arena.getPos2().x) &&
           Math.min(arena.getPos1().y,arena.getPos2().y)<event.getTo().y() && event.getTo().y()<Math.max(arena.getPos1().y,arena.getPos2().y) &&
           Math.min(arena.getPos1().z,arena.getPos2().z)<event.getTo().z() && event.getTo().z()<Math.max(arena.getPos1().z,arena.getPos2().z))) event.setCancelled(true);
    }
}