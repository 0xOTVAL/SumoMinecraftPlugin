package com.example.sumoplugin.eventlisteners;

import com.example.sumoplugin.arena.Arena;
import com.example.sumoplugin.arena.ArenaManager;
import com.example.sumoplugin.Sumo;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.damage.DamageType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class deathListener implements Listener {
    public ArenaManager arenaManager;
    public Sumo plugin;
    public deathListener(Sumo plugin){
        this.plugin=plugin;
        arenaManager=plugin.arenaManager;
    }
    @EventHandler
    public void PlayerDeath(PlayerDeathEvent event){
        Arena arena=arenaManager.getArenaByPlayer(event.getPlayer());
        if(arena==null)return;
        if(!arena.isGameStarted){
            event.setCancelled(true);
            return;
        }
        if(event.getDamageSource().getDamageType()== DamageType.FALL)event.setCancelled(true);
        arena.killPlayer(event.getPlayer(), PlainTextComponentSerializer.plainText().serialize(event.deathMessage()));
        event.setCancelled(true);
    }
}
