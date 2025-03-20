package com.example.sumoplugin.eventlisteners;

import com.example.sumoplugin.Sumo;
import com.example.sumoplugin.arena.Arena;
import com.example.sumoplugin.arena.ArenaManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class blockBreakListener implements Listener {
    public ArenaManager arenaManager;
    public Sumo plugin;
    public blockBreakListener(Sumo plugin){
        this.plugin=plugin;
        arenaManager=plugin.arenaManager;
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Arena arena=plugin.arenaManager.getArenaByPlayer(event.getPlayer());
        if(arena!=null && !arena.isGameStarted)event.setCancelled(true);
    }
}
