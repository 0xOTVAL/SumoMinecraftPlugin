package com.example.sumoPlugin;


import io.papermc.paper.event.player.*;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

public class attackListener implements Listener {
    @EventHandler
    public void PrePlayerAttackEntity(PrePlayerAttackEntityEvent event){
        if(!event.getPlayer().getInventory().getItemInMainHand().getType().name().equals("STICK"))return;
        Vector entityVelocityVector=event.getPlayer().getLocation().getDirection();
        entityVelocityVector.multiply(10);
        event.getAttacked().setVelocity(entityVelocityVector);
        event.setCancelled(true);
    }
}
