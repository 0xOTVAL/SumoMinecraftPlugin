package com.example.sumoplugin.eventlisteners;

import com.example.sumoplugin.Sumo;
import com.example.sumoplugin.arena.Arena;
import com.example.sumoplugin.arena.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class changeBlockListener implements Listener {
    public ArenaManager arenaManager;
    public Sumo plugin;
    public changeBlockListener(Sumo plugin){
        this.plugin=plugin;
        arenaManager=plugin.arenaManager;
    }
    @EventHandler
    public void changeBlock(EntityChangeBlockEvent event) {
        Arena arena =arenaManager.getArenaByWorld(event.getBlock().getWorld().getName());
        if(arena!=null &&arena.isGameStarted){
            if(event.getEntityType() == EntityType.FALLING_BLOCK)event.setCancelled(true);
            event.getBlock().setBlockData(Material.CHEST.createBlockData());
            Container cb =(Container)event.getBlock().getState();
            Inventory inv = cb.getInventory();
            arena.fillBonusInventory(inv);
        }
    }
}