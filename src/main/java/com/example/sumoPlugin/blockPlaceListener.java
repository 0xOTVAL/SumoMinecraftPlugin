package com.example.sumoPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class blockPlaceListener implements Listener {
    public ArenaManager arenaManager;
    public Sumo plugin;
    blockPlaceListener(Sumo plugin){
        this.plugin=plugin;
        arenaManager=plugin.arenaManager;
    }
    @EventHandler
    void PlaceBlock(BlockPlaceEvent event){
        Arena arena=arenaManager.getArenaByPlayer(event.getPlayer());
        if(arena==null)return;
        if(!arena.isStarted){
            return;
        }
        if(!arena.isGameStarted){
            event.setCancelled(true);
            return;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                event.getBlockPlaced().setType(Material.YELLOW_WOOL);
                event.getPlayer().sendMessage("Pizdec");
            }
        }, 20L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                event.getBlockPlaced().setType(Material.RED_WOOL);
                event.getPlayer().sendMessage("Pizdec2");
            }
        }, 2*20L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                event.getBlockPlaced().setType(Material.AIR);
                event.getPlayer().sendMessage("Pizdec3");
            }
        }, 3*20L);
    }
}
