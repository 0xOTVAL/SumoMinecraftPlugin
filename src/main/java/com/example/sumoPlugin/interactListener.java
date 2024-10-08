package com.example.sumoPlugin;

import io.papermc.paper.event.player.PlayerOpenSignEvent;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class interactListener implements Listener {
    @EventHandler
    public void PlayerInteract(PlayerOpenSignEvent event){
        event.getPlayer().sendMessage(event.getSign().getSide(event.getSide()).lines().getFirst());
        String firstline=event.getSign().getSide(event.getSide()).getLines()[0];
        if(firstline.equals("sumo")){
            event.getPlayer().teleport(new Location(event.getPlayer().getWorld(),0,200,0));
            event.setCancelled(true);
        }

    }
}
