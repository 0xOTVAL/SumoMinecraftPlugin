package com.example.sumoplugin.eventlisteners;


import com.example.sumoplugin.arena.Arena;
import com.example.sumoplugin.arena.ArenaManager;
import com.example.sumoplugin.Sumo;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;


public class moveListener implements Listener{
    public ArenaManager arenaManager;
    public Sumo plugin;
    public moveListener(Sumo plugin){
        this.plugin=plugin;
        arenaManager=plugin.arenaManager;
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
                event.getPlayer().damage(Integer.parseInt(plugin.getConfig().get("barrier_damage").toString()));
            }
        }
    }
}