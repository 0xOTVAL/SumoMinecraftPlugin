package com.example.sumoplugin.eventlisteners;

import com.example.sumoplugin.Sumo;
import com.example.sumoplugin.arena.Arena;
import com.example.sumoplugin.arena.ArenaManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class dropItemListener implements Listener {

    public ArenaManager arenaManager;
    public Sumo plugin;
    public dropItemListener(Sumo plugin){
        this.plugin=plugin;
        arenaManager=plugin.arenaManager;
    }
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event){
        Arena arena=plugin.arenaManager.getArenaByPlayer(event.getPlayer());
        if(arena!=null)event.setCancelled(true);
    }
}
