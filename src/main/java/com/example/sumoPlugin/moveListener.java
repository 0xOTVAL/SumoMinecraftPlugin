package com.example.sumoPlugin;


import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;


public class moveListener implements Listener{
    private ArenaManager arenaManager;
    moveListener(ArenaManager arenaManager){
        this.arenaManager=arenaManager;
    }

    @EventHandler
    public void PlayerMove(PlayerMoveEvent event) {
        //get arena
        Arena arena = arenaManager.getArenaByPlayer(event.getPlayer());
        if (arena == null) return;

        if (!arena.isInsideBarrier(event.getTo().toVector().toVector3f())) {
            //if player is outside barrier and game has not started teleport in to lobby
            if (!arena.isGameStarted) {
                Location loc = new Location(arena.world, arena.lobbypos.x, arena.lobbypos.y, arena.lobbypos.z);
                event.getPlayer().teleport(loc);
            }
            //if game started inflict damage to player
            else{
                event.getPlayer().damage(6);
            }
        }
    }
}