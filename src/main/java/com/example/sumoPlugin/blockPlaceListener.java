package com.example.sumoPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.Map;

public class blockPlaceListener implements Listener {
    public ArenaManager arenaManager;
    public Sumo plugin;
    blockPlaceListener(Sumo plugin){
        this.plugin=plugin;
        arenaManager=plugin.arenaManager;
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
    void PlaceBlock(BlockPlaceEvent event){
        ArenaData arena=getArenaByPlayer(event.getPlayer());
        if(!arenaManager.isArenaGameStarted.get(arena)){
            event.setCancelled(true);
            return;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                event.getBlockPlaced().setType(Material.YELLOW_WOOL);
            }
        }, 1*20L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                event.getBlockPlaced().setType(Material.RED_WOOL);
            }
        }, 2*20L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                event.getBlockPlaced().setType(Material.AIR);
            }
        }, 3*20L);
    }
}
