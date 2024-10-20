package com.example.sumoPlugin;

import io.papermc.paper.event.player.PlayerOpenSignEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class interactListener implements Listener {
    Sumo plugin;
    public interactListener(Sumo plugin){
        this.plugin=plugin;
    }
    @EventHandler
    public void PlayerInteract(PlayerOpenSignEvent event){
        //get lines of sign
        String firstline= event.getSign().getSide(event.getSide()).lines().getFirst().toString();
        String secondline= event.getSign().getSide(event.getSide()).lines().get(1).toString();
        //first line is indicator of sign being arena entry point
        if(!firstline.equals("sumo"))return;
        //get arena and add player to it
        Arena arena=plugin.arenaManager.getArenaByName(secondline);
        if(arena!=null)arena.addPlayer(event.getPlayer());
    }
}
