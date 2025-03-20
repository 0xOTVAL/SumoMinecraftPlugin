package com.example.sumoplugin.eventlisteners;

import com.example.sumoplugin.arena.Arena;
import com.example.sumoplugin.arena.ArenaManager;
import com.example.sumoplugin.Sumo;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class blockPlaceListener implements Listener {
    public ArenaManager arenaManager;
    public Sumo plugin;
    public blockPlaceListener(Sumo plugin){
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
        if(event.getBlockPlaced().getType()!=Material.GREEN_WOOL)return;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                event.getBlockPlaced().setType(Material.YELLOW_WOOL);
            }
        }, 20L);
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
