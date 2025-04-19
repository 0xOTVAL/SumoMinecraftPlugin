package com.example.sumoplugin.eventlisteners;

import com.example.sumoplugin.Sumo;
import com.example.sumoplugin.arena.Arena;
import com.example.sumoplugin.arena.ArenaManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
        else if(arena!=null && arena.isGameStarted){
            if(event.getBlock().getType()!= Material.GREEN_WOOL && event.getBlock().getType()!= Material.RED_WOOL && event.getBlock().getType()!= Material.YELLOW_WOOL)event.setCancelled(true);
            event.setDropItems(false);
        }


    }
}
