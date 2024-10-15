package com.example.sumoPlugin;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;


public class moveListener implements Listener{
    private ArenaManager arenaManager;
    moveListener(ArenaManager arenaManager){
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
    public void PlayerMove(PlayerMoveEvent event){
        Arena arena= getArenaByPlayer(event.getPlayer());
        if(arena==null)return;
        if(!(Math.min(arena.getPos1().x,arena.getPos2().x)<event.getTo().x() && event.getTo().x()<Math.max(arena.getPos1().x,arena.getPos2().x) &&
           Math.min(arena.getPos1().y,arena.getPos2().y)<event.getTo().y() && event.getTo().y()<Math.max(arena.getPos1().y,arena.getPos2().y) &&
           Math.min(arena.getPos1().z,arena.getPos2().z)<event.getTo().z() && event.getTo().z()<Math.max(arena.getPos1().z,arena.getPos2().z))){
            if(!arenaManager.isArenaGameStarted.get(arena)){
                Location loc=new Location(Bukkit.getWorld(arena.world),arena.getLobbypos().x,arena.getLobbypos().y,arena.getLobbypos().z);
                event.getPlayer().teleport(loc);
            }
            if(arenaManager.playerDeathTimers.containsKey(event.getPlayer())){event.setCancelled(true);return;}
            arenaManager.playerDeathTimers.put(event.getPlayer(), new Timer());
            arenaManager.playerDeathTimers.get(event.getPlayer()).schedule(new TimerTask() {
                int a=5;
                @Override
                public void run() {
                    event.getPlayer().sendMessage("you will die in "+Integer.toString(a));
                    a-=1;
                }
            },0,1000);
            arenaManager.playerDeathTimers.get(event.getPlayer()).schedule(new TimerTask() {
                @Override
                public void run() {
                    event.getPlayer().sendMessage("You died");
                    arenaManager.returnPlayer(arena,event.getPlayer());
                    arenaManager.playerDeathTimers.get(event.getPlayer()).cancel();
                    arenaManager.playerDeathTimers.remove(event.getPlayer());
                }
            },5000);
        }
        else{
            if(arenaManager.playerDeathTimers.containsKey(event.getPlayer())){
                arenaManager.playerDeathTimers.get(event.getPlayer()).cancel();
                arenaManager.playerDeathTimers.remove(event.getPlayer());
            }
        }
    }
}