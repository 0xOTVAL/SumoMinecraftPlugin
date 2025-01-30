package com.example.sumoplugin.eventlisteners;


import com.example.sumoplugin.arena.Arena;
import com.example.sumoplugin.arena.ArenaManager;
import com.example.sumoplugin.Sumo;
import io.papermc.paper.event.player.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

public class attackListener implements Listener {
    public ArenaManager arenaManager;
    public Sumo plugin;
    public attackListener(Sumo plugin){
        this.plugin=plugin;
        arenaManager=plugin.arenaManager;
    }
    @EventHandler
    public void PrePlayerAttackEntity(PrePlayerAttackEntityEvent event){
        if(!event.getPlayer().getInventory().getItemInMainHand().getType().name().equals("STICK"))return;
        if(event.getAttacked().getType()!= EntityType.PLAYER)return;
        Arena arena=arenaManager.getArenaByPlayer(event.getPlayer());
        if(arena==null)return;
        if(!arena.isGameStarted)return;
        Player attackedPlayer=(Player)event.getAttacked();
        if(arena.getTeamByPlayer(attackedPlayer)==arena.getTeamByPlayer(event.getPlayer()))return;
        Vector entityVelocityVector=event.getPlayer().getLocation().getDirection();
        entityVelocityVector.multiply(Integer.parseInt(plugin.getConfig().get("attack_strength").toString()));
        event.getAttacked().setVelocity(entityVelocityVector);
        event.setCancelled(true);
    }
}
