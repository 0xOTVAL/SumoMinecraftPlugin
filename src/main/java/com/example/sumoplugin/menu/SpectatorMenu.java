package com.example.sumoplugin.menu;

import com.example.sumoplugin.Sumo;
import com.example.sumoplugin.arena.Arena;
import com.example.sumoplugin.arena.ArenaManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SpectatorMenu {
    public Inventory menuInventory;
    Sumo plugin;
    public SpectatorMenu(Sumo plugin){
        this.plugin=plugin;
        menuInventory= Bukkit.createInventory(null,54, Component.text("Sumo spectator menu"));
        menuInventory.clear();
        setupStartPage();
    }
    private void addMenuItem(int row, int column, Material type, String name){
        ItemStack item=new ItemStack(type,1);
        ItemMeta meta=item.getItemMeta();
        meta.setItemName(name);
        item.setItemMeta(meta);
        menuInventory.setItem(column+row*9,item);
    }
    private void addMenuItem(int row,int column, Material type,String name, List<String> lore){
        ItemStack item=new ItemStack(type,1);
        ItemMeta meta=item.getItemMeta();
        meta.setItemName(name);
        item.setItemMeta(meta);
        item.setLore(lore);
        menuInventory.setItem(column+row*9,item);
    }
    void setupStartPage(){
        ArrayList<Arena>arenas=plugin.arenaManager.arenas;
        for(int i=0;i<arenas.size();i++){
            Arena a = arenas.get(i);
            ArrayList<String> players=new ArrayList<>();
            for(Player p: a.players){
                players.add(p.getName());
            }
            if (a.isStarted && a.isGameStarted) addMenuItem(i/9, i % 9, Material.GREEN_WOOL, a.name,players);
        }
        addMenuItem((arenas.size()+1)/9, (arenas.size()+1)%9, Material.END_ROD, "Выйти из режима наблюдателя");
    }
}
