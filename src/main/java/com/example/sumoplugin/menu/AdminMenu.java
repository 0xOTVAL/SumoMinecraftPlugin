package com.example.sumoplugin.menu;

import com.example.sumoplugin.Sumo;
import com.example.sumoplugin.arena.Arena;
import com.example.sumoplugin.arena.ArenaManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class AdminMenu {
    public Inventory menuInventory;
    Sumo plugin;
    public AdminMenu(Sumo plugin){
        this.plugin=plugin;
        menuInventory=Bukkit.createInventory(null,54, Component.text("Admin menu"));
        menuInventory.clear();
        setupStartPage();
    }
    private void addMenuItem(int row,int column, Material type,String name){
        ItemStack item=new ItemStack(type,1);
        ItemMeta meta=item.getItemMeta();
        meta.setItemName(name);
        item.setItemMeta(meta);
        menuInventory.setItem(column+row*9,item);
    }
    private void addMenuItem(int row,int column, Material type,String name,String lore){
        ItemStack item=new ItemStack(type,1);
        ItemMeta meta=item.getItemMeta();
        meta.setItemName(name);
        item.setItemMeta(meta);
        item.setLore(List.of(lore));
        menuInventory.setItem(column+row*9,item);
    }
    public void setupStartPage(){
        addMenuItem(0,0,Material.NETHERITE_SWORD,"Арены");
        addMenuItem(0,1,Material.RED_BANNER,"Команды");
    }
    public void setupTeamsPage(){
        addMenuItem(0,0,Material.PAPER,"Импортировать");
        for(int i=0;i<plugin.teamManager.teams.size();i++){
            addMenuItem((i+1)/9,(i+1)%9,Material.PLAYER_HEAD,plugin.teamManager.teams.get(i).name);
        }
    }
    public void setupArenaPage(){
        ArenaManager manager=plugin.arenaManager;
        ArrayList<Arena> arenas=manager.arenas;
        IntStream.range(0, arenas.size()).forEach(i -> {
            Arena a = arenas.get(i);
            if (!a.isStarted) addMenuItem(i/9, i % 9, Material.RED_WOOL, a.name);
            if (a.isStarted && !a.isGameStarted) addMenuItem(i/9, i % 9, Material.GREEN_WOOL, a.name);
            if (a.isGameStarted) addMenuItem(i/9, i % 9, Material.BLUE_WOOL, a.name);
        });
    }
    public void setupArenaActionsPage(String arenaName){
        menuInventory=Bukkit.createInventory(null,54, Component.text(arenaName));
        Arena a=plugin.arenaManager.getArenaByName(arenaName);
        if(!a.isStarted){addMenuItem(0,0,Material.GREEN_WOOL,"Запустить арену");return;}
        if(a.isStarted)addMenuItem(0,0,Material.RED_WOOL,"Остановить арену");
        if(!a.isGameStarted)addMenuItem(0,1,Material.BLUE_WOOL,"Начать игру");
        for(int i=0;i<plugin.teamManager.teams.size();i++){
            if(plugin.teamManager.teams.get(i).players.isEmpty())addMenuItem((i+2)/9,(i+2)%9,Material.SKELETON_SKULL,plugin.teamManager.teams.get(i).name,"В команде нет игроков");
            if(!plugin.teamManager.teams.get(i).isUsed)addMenuItem((i+2)/9,(i+2)%9,Material.PLAYER_HEAD,plugin.teamManager.teams.get(i).name,"Нажми чтобы отправить эту команду на арену");
            else if(plugin.arenaManager.getArenaByName(arenaName).teams.contains(plugin.teamManager.teams.get(i)))addMenuItem((i+2)/9,(i+2)%9,Material.ZOMBIE_HEAD,plugin.teamManager.teams.get(i).name,"Нажми чтобы убрать команду с арены");
            else addMenuItem((i+2)/9,(i+2)%9,Material.SKELETON_SKULL,plugin.teamManager.teams.get(i).name,"Команда на другой арене");
        }
    }


}
